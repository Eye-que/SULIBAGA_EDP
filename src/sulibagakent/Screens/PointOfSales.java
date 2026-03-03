package sulibagakent.Screens;

import sulibagakent.Screens.Gradients.POSGradient;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.print.PrinterException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PointOfSales extends POSGradient {

    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private final SimpleDateFormat dtFmt = new SimpleDateFormat("MMM dd, yyyy  hh:mm:ss a");

    // session
    private int currentUserId = 0;
    private String currentUsername = "Cashier";

    // state
    private String invoiceNumber = "";
    private boolean recalcLock = false;
    private boolean checkoutLock = false;

    // ====== IMPORTANT: hidden columns in cart table ======
    // Visible columns in UI table:
    // 0 Product, 1 Quantity, 2 Price, 3 Discount, 4 Subtotal
    // Hidden columns (we will add programmatically):
    // 5 product_id, 6 barcode, 7 stock
    private static final int COL_PRODUCT = 0;
    private static final int COL_QTY = 1;
    private static final int COL_PRICE = 2;
    private static final int COL_DISC = 3;
    private static final int COL_SUB = 4;

    private static final int COL_PID = 5;
    private static final int COL_BARCODE = 6;
    private static final int COL_STOCK = 7;

    public PointOfSales() {
        initComponents();
        setupPOS();
    }

    /** Call this after login */
    public void setLoggedInUser(int userId, String username) {
        this.currentUserId = userId;
        this.currentUsername = (username == null || username.isBlank()) ? "Cashier" : username;
        lblUserType.setText(currentUsername);
        buildReceiptPreview();
    }

    // ===================== SETUP =====================
    private void setupPOS() {
        // make cash tendered editable (your design set it to false)
        txtCashTendered.setEditable(true);

        // receipt readonly
        txtReceipt.setEditable(false);

        // default values
        txtSubtotal.setText("0.00");
        txtApplyVat.setText("0.00");
        txtTotal.setText("0.00");
        txtChange.setText("0.00");
        txtCashTendered.setText("0");

        // setup cart model with hidden cols
        setupCartTableModel();

        // events
        wireEvents();

        // start clock + invoice
        startClock();
        newInvoice();
        recalcTotals();
    }

    private void setupCartTableModel() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Product", "Quantity", "Price", "Discount", "Subtotal", "product_id", "barcode", "stock"},
                0
        ) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == COL_QTY || col == COL_DISC; // allow qty + discount edit
            }
        };

        tblCartItems.setModel(model);

        // hide technical columns
        hideCol(COL_PID);
        hideCol(COL_BARCODE);
        hideCol(COL_STOCK);

        tblCartItems.setRowHeight(26);
    }

    private void hideCol(int index) {
        tblCartItems.getColumnModel().getColumn(index).setMinWidth(0);
        tblCartItems.getColumnModel().getColumn(index).setMaxWidth(0);
        tblCartItems.getColumnModel().getColumn(index).setWidth(0);
    }

    private void wireEvents() {
        // add by barcode
        txtBarcode.addActionListener(e -> addByBarcode());
        btnAdd.addActionListener(e -> addByBarcode());
        btnFocusBarcode.addActionListener(e -> txtBarcode.requestFocusInWindow());

        // VAT toggle
        checkboxVAT.addActionListener(e -> recalcTotals());

        // cash tendered typing
        txtCashTendered.addCaretListener(e -> recalcTotals());

        // checkout
        btnCheckOut.addActionListener(e -> checkout());

        // print
        btnPrintReceipt.addActionListener(e -> {
            try {
                txtReceipt.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage());
            }
        });

        // table edit listener (Qty/Discount)
        DefaultTableModel m = (DefaultTableModel) tblCartItems.getModel();
        m.addTableModelListener((TableModelEvent e) -> {
            if (recalcLock) return;
            if (e.getType() != TableModelEvent.UPDATE) return;

            int row = e.getFirstRow();
            int col = e.getColumn();

            if (row < 0) return;

            // enforce stock limit on qty
            if (col == COL_QTY) {
                int stock = toInt(m.getValueAt(row, COL_STOCK), 0);
                String name = String.valueOf(m.getValueAt(row, COL_PRODUCT));
                int qty = toInt(m.getValueAt(row, COL_QTY), 1);

                int clamped = clampQty(qty, stock);
                if (clamped == 0) {
                    JOptionPane.showMessageDialog(this, "Out of stock: " + name);
                    m.removeRow(row);
                    recalcTotals();
                    return;
                }
                if (clamped != qty) {
                    m.setValueAt(clamped, row, COL_QTY);
                    JOptionPane.showMessageDialog(this,
                            "Quantity adjusted (stock limit)\nItem: " + name + "\nAvailable: " + stock);
                    return;
                }
            }

            // discount cannot be negative
            if (col == COL_DISC) {
                double disc = Math.max(0, toDouble(m.getValueAt(row, COL_DISC), 0));
                m.setValueAt(df.format(disc), row, COL_DISC);
            }

            recalcTotals();
        });
    }

    // ===================== CLOCK / INVOICE =====================
    private void startClock() {
        Timer t = new Timer(1000, e -> lblDateTime.setText(dtFmt.format(new Date())));
        t.start();
    }

    private void newInvoice() {
        invoiceNumber = "INV-" + new SimpleDateFormat("yyyyMMdd-HHmmssSSS").format(new Date());
        lblInvoice.setText(invoiceNumber);
        buildReceiptPreview();
    }

    // ===================== CART: ADD BY BARCODE =====================
    private void addByBarcode() {
        String barcode = txtBarcode.getText().trim();
        if (barcode.isEmpty()) return;

        try {
            Product p = fetchProductByBarcode(barcode);

            if (p == null) {
                JOptionPane.showMessageDialog(this, "Product not found: " + barcode);
                txtBarcode.selectAll();
                return;
            }

            if (p.stockQty <= 0) {
                JOptionPane.showMessageDialog(this, "Out of stock: " + p.name);
                txtBarcode.selectAll();
                return;
            }

            DefaultTableModel m = (DefaultTableModel) tblCartItems.getModel();
            int row = findRowByProductId(p.productId);

            if (row >= 0) {
                int currentQty = toInt(m.getValueAt(row, COL_QTY), 1);
                int newQty = currentQty + 1;

                if (newQty > p.stockQty) {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient stock for " + p.name + "\nAvailable: " + p.stockQty);
                    return;
                }
                m.setValueAt(newQty, row, COL_QTY);
            } else {
                int qty = 1;
                double price = p.sellingPrice;
                double disc = 0.00;
                double sub = (qty * price) - disc;

                m.addRow(new Object[]{
                        p.name,
                        qty,
                        df.format(price),
                        df.format(disc),
                        df.format(sub),
                        p.productId,
                        p.barcode,
                        p.stockQty
                });
            }

            txtBarcode.setText("");
            txtBarcode.requestFocusInWindow();
            recalcTotals();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Add error: " + ex.getMessage());
        }
    }

    private int findRowByProductId(int productId) {
        DefaultTableModel m = (DefaultTableModel) tblCartItems.getModel();
        for (int i = 0; i < m.getRowCount(); i++) {
            int pid = toInt(m.getValueAt(i, COL_PID), -1);
            if (pid == productId) return i;
        }
        return -1;
    }

    // ===================== TOTALS =====================
    private void recalcTotals() {
        if (recalcLock) return;
        recalcLock = true;

        try {
            DefaultTableModel m = (DefaultTableModel) tblCartItems.getModel();

            double subtotal = 0;

            for (int i = 0; i < m.getRowCount(); i++) {
                int stock = toInt(m.getValueAt(i, COL_STOCK), 0);

                int qty = toInt(m.getValueAt(i, COL_QTY), 1);
                int clampedQty = clampQty(qty, stock);
                if (clampedQty == 0) {
                    m.removeRow(i);
                    i--;
                    continue;
                }
                if (clampedQty != qty) m.setValueAt(clampedQty, i, COL_QTY);

                double price = toDouble(m.getValueAt(i, COL_PRICE), 0);
                double disc = Math.max(0, toDouble(m.getValueAt(i, COL_DISC), 0));

                double lineSub = (clampedQty * price) - disc;
                if (lineSub < 0) lineSub = 0;

                m.setValueAt(df.format(disc), i, COL_DISC);
                m.setValueAt(df.format(lineSub), i, COL_SUB);

                subtotal += lineSub;
            }

            double vat = checkboxVAT.isSelected() ? subtotal * 0.12 : 0.0;
            double total = subtotal + vat;

            double cash = Math.max(0, parseSafeDouble(txtCashTendered.getText()));
            double change = cash - total;
            if (change < 0) change = 0;

            txtSubtotal.setText(df.format(subtotal));
            txtApplyVat.setText(df.format(vat));
            txtTotal.setText(df.format(total));
            txtChange.setText(df.format(change));

            buildReceiptPreview();

        } finally {
            recalcLock = false;
        }
    }

    // ===================== CHECKOUT =====================
    private void checkout() {
        if (checkoutLock) return;
        checkoutLock = true;
        btnCheckOut.setEnabled(false);

        try {
            DefaultTableModel m = (DefaultTableModel) tblCartItems.getModel();
            if (m.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Cart is empty.");
                return;
            }

            double total = parseSafeDouble(txtTotal.getText());
            double cash = parseSafeDouble(txtCashTendered.getText());
            String payment = String.valueOf(cmbPayment.getSelectedItem());

            // only require cash if CASH payment
            if (payment.equalsIgnoreCase("CASH") && cash < total) {
                JOptionPane.showMessageDialog(this, "Insufficient cash.");
                return;
            }

            // fresh stock check
            for (int i = 0; i < m.getRowCount(); i++) {
                int productId = toInt(m.getValueAt(i, COL_PID), 0);
                int qty = toInt(m.getValueAt(i, COL_QTY), 1);
                int currentStock = fetchStockByProductId(productId);
                if (qty > currentStock) {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient stock for item.\nNeeded: " + qty + "\nAvailable: " + currentStock);
                    return;
                }
            }

            double subtotal = parseSafeDouble(txtSubtotal.getText());
            double vat = parseSafeDouble(txtApplyVat.getText());
            double change = parseSafeDouble(txtChange.getText());

            try (Connection con = getConnection()) {
                con.setAutoCommit(false);
                try {
                    // retry invoice if duplicate key
                    int saleId = -1;
                    for (int attempts = 0; attempts < 3; attempts++) {
                        try {
                            saleId = insertSale(con, invoiceNumber, currentUserId, subtotal, vat, 0, total,
                                    payment, cash, change);
                            break;
                        } catch (SQLIntegrityConstraintViolationException dup) {
                            newInvoice();
                        }
                    }
                    if (saleId <= 0) throw new SQLException("Failed to create sale.");

                    // insert items + deduct stock + inventory transaction
                    for (int i = 0; i < m.getRowCount(); i++) {
                        int productId = toInt(m.getValueAt(i, COL_PID), 0);
                        int qty = toInt(m.getValueAt(i, COL_QTY), 1);
                        double price = toDouble(m.getValueAt(i, COL_PRICE), 0);
                        double disc = toDouble(m.getValueAt(i, COL_DISC), 0);
                        double lineSub = toDouble(m.getValueAt(i, COL_SUB), 0);

                        insertSaleItem(con, saleId, productId, qty, price, disc, lineSub);

                        if (!deductStock(con, productId, qty)) {
                            throw new SQLException("Stock update failed for product_id: " + productId);
                        }

                        insertInventoryTransaction(con, productId, "Sale", qty, invoiceNumber, "Sold via POS");
                    }

                    insertTransactionsLog(con, currentUserId, "SALE",
                            "Completed sale: " + invoiceNumber + " Total: " + df.format(total));

                    con.commit();

                    JOptionPane.showMessageDialog(this, "Payment successful!\nInvoice: " + invoiceNumber);

                    // clear cart and new invoice
                    m.setRowCount(0);
                    txtCashTendered.setText("0");
                    checkboxVAT.setSelected(false);
                    newInvoice();
                    recalcTotals();
                    txtBarcode.requestFocusInWindow();

                } catch (Exception ex) {
                    con.rollback();
                    JOptionPane.showMessageDialog(this, "Checkout failed: " + ex.getMessage());
                } finally {
                    con.setAutoCommit(true);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Checkout error: " + ex.getMessage());
        } finally {
            checkoutLock = false;
            btnCheckOut.setEnabled(true);
        }
    }

    // ===================== RECEIPT =====================
    private void buildReceiptPreview() {
        DefaultTableModel m = (DefaultTableModel) tblCartItems.getModel();

        StringBuilder sb = new StringBuilder();
        sb.append("SULIBAGAKENT POS\n");
        sb.append("Invoice: ").append(invoiceNumber).append("\n");
        sb.append("User: ").append(currentUsername).append("\n");
        sb.append("Date: ").append(dtFmt.format(new Date())).append("\n");
        sb.append("----------------------------------------\n");

        for (int i = 0; i < m.getRowCount(); i++) {
            String name = String.valueOf(m.getValueAt(i, COL_PRODUCT));
            int qty = toInt(m.getValueAt(i, COL_QTY), 1);
            String price = String.valueOf(m.getValueAt(i, COL_PRICE));
            String sub = String.valueOf(m.getValueAt(i, COL_SUB));

            sb.append(name).append("\n");
            sb.append("  ").append(qty).append(" x ").append(price).append(" = ").append(sub).append("\n");
        }

        sb.append("----------------------------------------\n");
        sb.append("Subtotal: ").append(txtSubtotal.getText()).append("\n");
        sb.append("VAT:      ").append(txtApplyVat.getText()).append("\n");
        sb.append("TOTAL:    ").append(txtTotal.getText()).append("\n");
        sb.append("Payment:  ").append(cmbPayment.getSelectedItem()).append("\n");
        sb.append("Cash:     ").append(txtCashTendered.getText()).append("\n");
        sb.append("Change:   ").append(txtChange.getText()).append("\n");
        sb.append("\nThank you!\n");

        txtReceipt.setText(sb.toString());
    }

    // ===================== DB =====================
    private Product fetchProductByBarcode(String barcode) throws SQLException {
        String sql = "SELECT product_id, barcode, name, selling_price, stock_quantity " +
                "FROM products WHERE barcode = ? LIMIT 1";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, barcode);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Product p = new Product();
                p.productId = rs.getInt("product_id");
                p.barcode = rs.getString("barcode");
                p.name = rs.getString("name");
                p.sellingPrice = rs.getDouble("selling_price");
                p.stockQty = rs.getInt("stock_quantity");
                return p;
            }
        }
    }

    private int fetchStockByProductId(int productId) throws SQLException {
        String sql = "SELECT stock_quantity FROM products WHERE product_id = ? LIMIT 1";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return 0;
                return rs.getInt("stock_quantity");
            }
        }
    }

    private boolean deductStock(Connection con, int productId, int qty) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? " +
                "WHERE product_id = ? AND stock_quantity >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            return ps.executeUpdate() > 0;
        }
    }

    private int insertSale(Connection con,
                          String invoice,
                          int userId,
                          double subtotal,
                          double vat,
                          double discount,
                          double total,
                          String paymentMethod,
                          double cashTendered,
                          double changeAmount) throws SQLException {

        String sql = "INSERT INTO sales " +
                "(invoice_number, user_id, subtotal, vat, discount, total_amount, payment_method, cash_tendered, change_amount, sale_date) " +
                "VALUES (?,?,?,?,?,?,?,?,?, NOW())";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, invoice);
            ps.setInt(2, userId);
            ps.setDouble(3, subtotal);
            ps.setDouble(4, vat);
            ps.setDouble(5, discount);
            ps.setDouble(6, total);
            ps.setString(7, paymentMethod);
            ps.setDouble(8, cashTendered);
            ps.setDouble(9, changeAmount);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Failed to get sale_id.");
                return keys.getInt(1);
            }
        }
    }

    private void insertSaleItem(Connection con,
                                int saleId,
                                int productId,
                                int qty,
                                double unitPrice,
                                double discount,
                                double subtotal) throws SQLException {

        String sql = "INSERT INTO sale_items " +
                "(sale_id, product_id, quantity, unit_price, discount, subtotal) " +
                "VALUES (?,?,?,?,?,?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            ps.setDouble(4, unitPrice);
            ps.setDouble(5, discount);
            ps.setDouble(6, subtotal);
            ps.executeUpdate();
        }
    }

    private void insertInventoryTransaction(Connection con,
                                            int productId,
                                            String type,
                                            int quantity,
                                            String reference,
                                            String reason) throws SQLException {

        String sql = "INSERT INTO inventory_transactions " +
                "(product_id, transaction_type, quantity, reference_number, reason, transaction_date) " +
                "VALUES (?,?,?,?,?, NOW())";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, type);
            ps.setInt(3, quantity);
            ps.setString(4, reference);
            ps.setString(5, reason);
            ps.executeUpdate();
        }
    }

    private void insertTransactionsLog(Connection con,
                                       int userId,
                                       String action,
                                       String description) throws SQLException {

        String sql = "INSERT INTO transactions_log (user_id, action, description, created_at) " +
                "VALUES (?,?,?, NOW())";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, description);
            ps.executeUpdate();
        }
    }

    private Connection getConnection() throws SQLException {
        return DbConnection.DBConnection.getConnection();
    }

    // ===================== HELPERS =====================
    private int clampQty(int desiredQty, int availableStock) {
        if (availableStock <= 0) return 0;
        if (desiredQty < 1) return 1;
        if (desiredQty > availableStock) return availableStock;
        return desiredQty;
    }

    private int toInt(Object v, int def) {
        try { return Integer.parseInt(String.valueOf(v).trim()); }
        catch (Exception e) { return def; }
    }

    private double toDouble(Object v, double def) {
        try {
            String s = String.valueOf(v).trim().replace(",", "");
            if (s.isEmpty()) return def;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return def;
        }
    }

    private double parseSafeDouble(String s) {
        try {
            if (s == null) return 0;
            s = s.trim().replace(",", "");
            if (s.isEmpty()) return 0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private static class Product {
        int productId;
        String barcode;
        String name;
        double sellingPrice;
        int stockQty;
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblDateTime = new javax.swing.JLabel();
        lblUserType = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblInvoice = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnFocusBarcode = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCartItems = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnCheckOut = new javax.swing.JButton();
        btnPrintReceipt = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        checkboxVAT = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        txtTotal = new javax.swing.JTextField();
        txtApplyVat = new javax.swing.JTextField();
        cmbPayment = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtChange = new javax.swing.JTextField();
        txtCashTendered = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtReceipt = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1250, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel1.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                jPanel1AncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("User:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, -1));

        jLabel2.setText("Date/Time:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblDateTime.setText("{}");
        jPanel1.add(lblDateTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, -1, -1));

        lblUserType.setText("{}");
        jPanel1.add(lblUserType, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Invoice:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 10, -1, -1));

        lblInvoice.setText("{}");
        jPanel1.add(lblInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 10, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 1530, 40));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Barcode:");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));
        jPanel2.add(txtBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 1000, 40));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 204, 204));
        jLabel9.setText("Scanner-ready: scan/type then press Enter");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1100, 20, -1, -1));

        btnAdd.setBackground(new java.awt.Color(109, 213, 180));
        btnAdd.setText("Add");
        btnAdd.addActionListener(this::btnAddActionPerformed);
        jPanel2.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 20, -1, -1));

        btnFocusBarcode.setBackground(new java.awt.Color(109, 213, 180));
        btnFocusBarcode.setText("Focus Barcode");
        btnFocusBarcode.addActionListener(this::btnFocusBarcodeActionPerformed);
        jPanel2.add(btnFocusBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(1410, 20, -1, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 1530, 60));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Product Scan");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblCartItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Product", "Quantity", "Price", "Dsicount", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblCartItems);

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 960, 450));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Cart Items");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 980, 480));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCheckOut.setBackground(new java.awt.Color(109, 213, 180));
        btnCheckOut.setText("Checkout");
        btnCheckOut.addActionListener(this::btnCheckOutActionPerformed);
        jPanel4.add(btnCheckOut, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, 220, -1));

        btnPrintReceipt.setBackground(new java.awt.Color(109, 213, 180));
        btnPrintReceipt.setText("Print Receipt");
        btnPrintReceipt.addActionListener(this::btnPrintReceiptActionPerformed);
        jPanel4.add(btnPrintReceipt, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 260, 230, -1));

        jLabel3.setText("Subtotal:");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        txtSubtotal.setEditable(false);
        jPanel4.add(txtSubtotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 22, 390, 30));

        checkboxVAT.setText(" Apply VAT (12%)");
        checkboxVAT.addActionListener(this::checkboxVATActionPerformed);
        jPanel4.add(checkboxVAT, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        jLabel4.setText("Change:");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, -1, -1));

        txtTotal.setEditable(false);
        jPanel4.add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 390, 30));

        txtApplyVat.setEditable(false);
        jPanel4.add(txtApplyVat, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 390, 30));

        cmbPayment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CASH", "CARD", "GCASH" }));
        jPanel4.add(cmbPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 390, 30));

        jLabel6.setText("Total:");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, -1, -1));

        jLabel12.setText("Payment:");
        jPanel4.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        jLabel13.setText("Cash Tendered:");
        jPanel4.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, -1, -1));

        txtChange.setEditable(false);
        jPanel4.add(txtChange, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 220, 390, 30));
        jPanel4.add(txtCashTendered, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 180, 390, 30));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Computation and Payment");
        jPanel4.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 160, 530, 300));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        txtReceipt.setColumns(20);
        txtReceipt.setRows(5);
        jScrollPane1.setViewportView(txtReceipt);

        jPanel5.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 510, 140));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Receipt");
        jPanel5.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 470, 530, 170));

        jButton1.setBackground(new java.awt.Color(109, 213, 180));
        jButton1.setText("Clear Cart");
        add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 650, -1, -1));

        jButton2.setBackground(new java.awt.Color(109, 213, 180));
        jButton2.setText("Remove Selected Product");
        add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 650, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1AncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_jPanel1AncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1AncestorAdded

    private void checkboxVATActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxVATActionPerformed
    recalcTotals(); // recompute VAT + totals
    }//GEN-LAST:event_checkboxVATActionPerformed

    private void btnCheckOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckOutActionPerformed
     checkout(); // insert to sales + sale_items + deduct stock + inventory_transactions
    }//GEN-LAST:event_btnCheckOutActionPerformed

    private void btnPrintReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintReceiptActionPerformed
     try {
        txtReceipt.print();
    } catch (java.awt.print.PrinterException ex) {
        JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage());
    }
    }//GEN-LAST:event_btnPrintReceiptActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
     addByBarcode(); // add product using txtBarcode
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnFocusBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFocusBarcodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFocusBarcodeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCheckOut;
    private javax.swing.JButton btnFocusBarcode;
    private javax.swing.JButton btnPrintReceipt;
    private javax.swing.JCheckBox checkboxVAT;
    private javax.swing.JComboBox<String> cmbPayment;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDateTime;
    private javax.swing.JLabel lblInvoice;
    private javax.swing.JLabel lblUserType;
    private javax.swing.JTable tblCartItems;
    private javax.swing.JTextField txtApplyVat;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtCashTendered;
    private javax.swing.JTextField txtChange;
    private javax.swing.JTextArea txtReceipt;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}