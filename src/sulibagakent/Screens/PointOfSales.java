package sulibagakent.Screens;

import sulibagakent.Screens.Gradients.POSGradient;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PointOfSales extends POSGradient {

    // ===================== THEME =====================
    private final Color BTN_MAIN = new Color(109, 213, 180);
    private final Color BTN_HOVER = new Color(88, 194, 160);
    private final Color BTN_DARK = new Color(55, 160, 130);
    private final Color BTN_DANGER = new Color(231, 76, 60);
    private final Color BTN_DANGER_HOVER = new Color(210, 60, 45);
    private final Color BG_CARD = Color.WHITE;
    private final Color BORDER = new Color(225, 225, 225);
    private final Color TXT_MUTED = new Color(110, 110, 110);

    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    private final SimpleDateFormat dtFmt = new SimpleDateFormat("MMM dd, yyyy  hh:mm:ss a");

    // ===================== SESSION USER =====================
    private int currentUserId = 0;
    private String currentUsername = "Cashier";

    // ===================== UI COMPONENTS =====================
    private JLabel lblDateTime;
    private JLabel lblUser;
    private JLabel lblInvoice;

    private JTextField txtBarcode;
    private JButton btnAddBarcode;

    private JTable tblCart;
    private DefaultTableModel cartModel;
    private JButton btnRemove;
    private JButton btnClear;

    private JTextField txtSubtotal;
    private JCheckBox chkVat;
    private JTextField txtVat;
    private JTextField txtDiscount;
    private JTextField txtTotal;
    private JTextField txtCash;
    private JTextField txtChange;
    private JComboBox<String> cmbPayment;

    private JButton btnCheckout;
    private JButton btnPrintReceipt;

    private JTextArea txtReceipt;

    // ===================== STATE =====================
    private String invoiceNumber = "";

    public PointOfSales() {
        initComponents();       // sets layout
        buildPOSUI();           // responsive UI
        startClock();
        newInvoice();
        wireEvents();
        recalcTotals();
    }

    /**
     * Call this after login
     */
    public void setLoggedInUser(int userId, String username) {
        this.currentUserId = userId;
        this.currentUsername = (username == null || username.isBlank()) ? "Cashier" : username;
        if (lblUser != null) lblUser.setText("User: " + currentUsername);
        buildReceiptPreview();
    }

    // ===================== BUILD UI (RESPONSIVE) =====================
    private void buildPOSUI() {
        setOpaque(false); // since POSGradient likely paints background
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // ---------- TOP (info + scan) ----------
        JPanel topContainer = new JPanel();
        topContainer.setOpaque(false);
        topContainer.setLayout(new BorderLayout(12, 12));

        topContainer.add(buildTopInfoCard(), BorderLayout.NORTH);
        topContainer.add(buildScanCard(), BorderLayout.CENTER);

        add(topContainer, BorderLayout.NORTH);

        // ---------- CENTER (cart left, compute+receipt right) ----------
        JPanel leftCart = buildCartCard();
        JPanel rightSide = buildRightSide();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCart, rightSide);
        split.setResizeWeight(0.68);         // left gets more space
        split.setOneTouchExpandable(false);
        split.setBorder(null);
        split.setOpaque(false);

        add(split, BorderLayout.CENTER);
    }

    private JPanel buildTopInfoCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(new Color(250, 250, 250));
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 10);
        c.anchor = GridBagConstraints.WEST;

        lblDateTime = new JLabel("Date/Time: --");
        lblDateTime.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

        lblUser = new JLabel("User: " + currentUsername);
        lblUser.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

        lblInvoice = new JLabel("Invoice: --");
        lblInvoice.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 12));

        JButton btnNewInvoice = new JButton("New Invoice");
        styleButton(btnNewInvoice, BTN_MAIN, BTN_HOVER);
        btnNewInvoice.addActionListener(e -> {
            if (cartModel != null && cartModel.getRowCount() > 0) {
                int ans = JOptionPane.showConfirmDialog(
                        this,
                        "Cart is not empty. Clear and start new invoice?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );
                if (ans != JOptionPane.YES_OPTION) return;
                clearCart();
            }
            newInvoice();
        });

        // row layout
        c.gridx = 0; c.gridy = 0; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        card.add(lblDateTime, c);

        c.gridx = 1;
        card.add(lblUser, c);

        c.gridx = 2;
        card.add(lblInvoice, c);

        c.gridx = 3; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        card.add(Box.createHorizontalGlue(), c);

        c.gridx = 4; c.weightx = 0; c.fill = GridBagConstraints.NONE; c.insets = new Insets(0, 10, 0, 0);
        card.add(btnNewInvoice, c);

        return card;
    }

    private JPanel buildScanCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new TitledBorder(new LineBorder(BORDER, 1, true), "Product Scan"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 10, 8, 10);
        c.anchor = GridBagConstraints.WEST;

        JLabel lblBarcode = new JLabel("Barcode:");
        lblBarcode.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

        txtBarcode = new JTextField();
        txtBarcode.setFont(new Font("Yu Gothic UI", Font.PLAIN, 15));

        JLabel hint = new JLabel("Scanner-ready: scan/type then press ENTER");
        hint.setFont(new Font("Yu Gothic UI", Font.ITALIC, 11));
        hint.setForeground(TXT_MUTED);

        btnAddBarcode = new JButton("Add");
        styleButton(btnAddBarcode, BTN_MAIN, BTN_HOVER);

        JButton btnFocus = new JButton("Focus Barcode");
        styleButton(btnFocus, BTN_DARK, BTN_MAIN);
        btnFocus.addActionListener(e -> txtBarcode.requestFocusInWindow());

        // layout
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        card.add(lblBarcode, c);

        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        card.add(txtBarcode, c);

        c.gridx = 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        card.add(hint, c);

        c.gridx = 3;
        card.add(btnAddBarcode, c);

        c.gridx = 4;
        card.add(btnFocus, c);

        return card;
    }

    private JPanel buildCartCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(BG_CARD);
        card.setBorder(new TitledBorder(new LineBorder(BORDER, 1, true), "Cart Items"));

        cartModel = new DefaultTableModel(
                new Object[]{"Product", "Qty", "Price", "Discount", "Subtotal", "product_id", "barcode", "stock"},
                0
        ) {
            @Override public boolean isCellEditable(int row, int col) {
                return col == 1 || col == 3; // Qty and Discount editable
            }
        };

        tblCart = new JTable(cartModel);
        tblCart.setRowHeight(28);
        tblCart.getTableHeader().setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 12));
        tblCart.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

        // Hide technical columns
        hideCol(tblCart, 5);
        hideCol(tblCart, 6);
        hideCol(tblCart, 7);

        card.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnBar.setBackground(BG_CARD);

        btnRemove = new JButton("Remove Selected");
        styleButton(btnRemove, BTN_DARK, BTN_MAIN);

        btnClear = new JButton("Clear Cart");
        styleButton(btnClear, BTN_DANGER, BTN_DANGER_HOVER);

        btnBar.add(btnRemove);
        btnBar.add(btnClear);

        card.add(btnBar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildRightSide() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 12, 0);
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;

        JPanel compute = buildComputeCard();
        JPanel receipt = buildReceiptCard();

        c.gridy = 0;
        c.weighty = 0.62;
        right.add(compute, c);

        c.gridy = 1;
        c.weighty = 0.38;
        c.insets = new Insets(0, 0, 0, 0);
        right.add(receipt, c);

        return right;
    }

    private JPanel buildComputeCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new TitledBorder(new LineBorder(BORDER, 1, true), "Computation / Payment"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 10, 6, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        txtSubtotal = makeReadOnly("0.00");
        chkVat = new JCheckBox("Apply VAT (12%)");
        chkVat.setBackground(BG_CARD);
        chkVat.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
        txtVat = makeReadOnly("0.00");

        txtDiscount = new JTextField("0");
        txtDiscount.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

        txtTotal = makeReadOnly("0.00");

        cmbPayment = new JComboBox<>(new String[]{"CASH", "GCASH / E-WALLET (SIM)", "CARD (SIM)"});
        cmbPayment.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

        txtCash = new JTextField("0");
        txtCash.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));

        txtChange = makeReadOnly("0.00");

        // row helper
        int row = 0;

        addRow(card, c, row++, "Subtotal:", txtSubtotal);
        // VAT row (checkbox + field)
        c.gridy = row; c.gridx = 0; c.weightx = 0.6;
        card.add(chkVat, c);
        c.gridx = 1; c.weightx = 0.4;
        card.add(txtVat, c);
        row++;

        addRow(card, c, row++, "Discount:", txtDiscount);
        addRow(card, c, row++, "Total:", txtTotal);

        // payment row
        c.gridy = row; c.gridx = 0; c.weightx = 0;
        card.add(makeLabel("Payment:"), c);
        c.gridx = 1; c.weightx = 1;
        card.add(cmbPayment, c);
        row++;

        addRow(card, c, row++, "Cash Tendered:", txtCash);
        addRow(card, c, row++, "Change:", txtChange);

        // buttons row
        btnCheckout = new JButton("Checkout");
        styleButton(btnCheckout, BTN_MAIN, BTN_HOVER);

        btnPrintReceipt = new JButton("Print Receipt");
        styleButton(btnPrintReceipt, BTN_DARK, BTN_MAIN);

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setBackground(BG_CARD);
        btnRow.add(btnCheckout);
        btnRow.add(btnPrintReceipt);

        c.gridy = row; c.gridx = 0; c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        card.add(btnRow, c);

        return card;
    }

    private JPanel buildReceiptCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(BG_CARD);
        card.setBorder(new TitledBorder(new LineBorder(BORDER, 1, true), "Receipt Preview"));

        txtReceipt = new JTextArea();
        txtReceipt.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtReceipt.setEditable(false);

        card.add(new JScrollPane(txtReceipt), BorderLayout.CENTER);
        buildReceiptPreview();

        return card;
    }

    private void addRow(JPanel parent, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridy = row;

        c.gridx = 0; c.weightx = 0;
        parent.add(makeLabel(label), c);

        c.gridx = 1; c.weightx = 1;
        parent.add(field, c);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
        return lbl;
    }

    private JTextField makeReadOnly(String v) {
        JTextField t = new JTextField(v);
        t.setEditable(false);
        t.setBackground(new Color(245, 245, 245));
        t.setFont(new Font("Yu Gothic UI", Font.PLAIN, 12));
        return t;
    }

    private void hideCol(JTable table, int index) {
        table.getColumnModel().getColumn(index).setMinWidth(0);
        table.getColumnModel().getColumn(index).setMaxWidth(0);
        table.getColumnModel().getColumn(index).setWidth(0);
    }

    // ===================== EVENTS =====================
    private void wireEvents() {
        txtBarcode.addActionListener(e -> addByBarcode());
        btnAddBarcode.addActionListener(e -> addByBarcode());

        btnRemove.addActionListener(e -> removeSelected());
        btnClear.addActionListener(e -> {
            int ans = JOptionPane.showConfirmDialog(this, "Clear cart?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (ans == JOptionPane.YES_OPTION) clearCart();
        });

        cartModel.addTableModelListener((TableModelEvent e) -> {
    if (recalcLock) return;

    int col = e.getColumn();
    // only respond to Qty(1) or Discount(3) changes, or inserts/deletes
    if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE) {
        recalcTotals();
        return;
    }
    if (col == 1 || col == 3) {
        recalcTotals();
    }
});

        txtDiscount.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { recalcTotals(); }
        });
        txtCash.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { recalcTotals(); }
        });

        chkVat.addActionListener(e -> recalcTotals());

        btnCheckout.addActionListener(e -> checkout());

        btnPrintReceipt.addActionListener(e -> {
            try {
                txtReceipt.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Print error: " + ex.getMessage());
            }
        });
    }

    // ===================== CLOCK / INVOICE =====================
    private void startClock() {
        Timer t = new Timer(1000, e -> {
            if (lblDateTime != null) lblDateTime.setText("Date/Time: " + dtFmt.format(new Date()));
        });
        t.start();
    }

private void newInvoice() {
    do {
        invoiceNumber = "INV-" + new SimpleDateFormat("yyyyMMdd-HHmmssSSS").format(new Date());
    } while (invoiceExists(invoiceNumber));

    if (lblInvoice != null) lblInvoice.setText("Invoice: " + invoiceNumber);
    buildReceiptPreview();
}
private boolean invoiceExists(String invoice) {
    String sql = "SELECT 1 FROM sales WHERE invoice_number = ? LIMIT 1";
    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, invoice);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException e) {
        return false; // if error, just assume not exists
    }
}

    // ===================== POS LOGIC =====================
    private void addByBarcode() {
        String barcode = txtBarcode.getText().trim();
        if (barcode.isEmpty()) return;

        try {
            Product p = fetchProductByBarcode(barcode);

            if (p == null) {
                JOptionPane.showMessageDialog(this, "Product not found for barcode: " + barcode,
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                txtBarcode.selectAll();
                return;
            }

            if (p.stockQty <= 0) {
                JOptionPane.showMessageDialog(this, "Out of stock: " + p.name,
                        "Stock Warning", JOptionPane.WARNING_MESSAGE);
                txtBarcode.selectAll();
                return;
            }

            int row = findCartRowByProductId(p.productId);
            if (row >= 0) {
                int currentQty = toInt(cartModel.getValueAt(row, 1), 1);
                int newQty = currentQty + 1;

                if (newQty > p.stockQty) {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient stock for " + p.name + "\nAvailable: " + p.stockQty,
                            "Stock Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                cartModel.setValueAt(newQty, row, 1);
            } else {
                int qty = 1;
                double price = p.sellingPrice;
                double disc = 0.00;
                double sub = (qty * price) - disc;

                cartModel.addRow(new Object[]{
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
            JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int findCartRowByProductId(int productId) {
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            int pid = toInt(cartModel.getValueAt(i, 5), -1);
            if (pid == productId) return i;
        }
        return -1;
    }

    private void removeSelected() {
        int viewRow = tblCart.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to remove.");
            return;
        }
        int modelRow = tblCart.convertRowIndexToModel(viewRow);
        cartModel.removeRow(modelRow);
        recalcTotals();
    }

    private void clearCart() {
        cartModel.setRowCount(0);
        txtDiscount.setText("0");
        txtCash.setText("0");
        recalcTotals();
        buildReceiptPreview();
    }
private boolean recalcLock = false;
private void recalcTotals() {
    if (recalcLock) return;           // ✅ stop recursion
    recalcLock = true;

    try {
        double subtotal = 0;

        for (int i = 0; i < cartModel.getRowCount(); i++) {

            // Qty
            int qty = Math.max(1, toInt(cartModel.getValueAt(i, 1), 1));
            // only write if changed (less events)
            if (!String.valueOf(cartModel.getValueAt(i, 1)).equals(String.valueOf(qty))) {
                cartModel.setValueAt(qty, i, 1);
            }

            double price = toDouble(cartModel.getValueAt(i, 2), 0);
            double disc = Math.max(0, toDouble(cartModel.getValueAt(i, 3), 0));

            double lineSub = (qty * price) - disc;
            if (lineSub < 0) lineSub = 0;

            String newLineSub = df.format(lineSub);
            if (!String.valueOf(cartModel.getValueAt(i, 4)).equals(newLineSub)) {
                cartModel.setValueAt(newLineSub, i, 4);
            }

            subtotal += lineSub;
        }

        double vat = chkVat.isSelected() ? subtotal * 0.12 : 0.0;
        double discount = Math.max(0, parseSafeDouble(txtDiscount.getText()));

        double total = (subtotal + vat) - discount;
        if (total < 0) total = 0;

        double cash = Math.max(0, parseSafeDouble(txtCash.getText()));
        double change = cash - total;
        if (change < 0) change = 0;

        txtSubtotal.setText(df.format(subtotal));
        txtVat.setText(df.format(vat));
        txtTotal.setText(df.format(total));
        txtChange.setText(df.format(change));

        buildReceiptPreview();

    } finally {
        recalcLock = false;           // ✅ always unlock
    }
}

    private void checkout() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add items first.",
                    "Checkout Blocked", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Validate stock first
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                int productId = toInt(cartModel.getValueAt(i, 5), 0);
                int qty = toInt(cartModel.getValueAt(i, 1), 1);

                int currentStock = fetchStockByProductId(productId);
                if (qty > currentStock) {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient stock for item in cart.\nNeeded: " + qty + "\nAvailable: " + currentStock,
                            "Stock Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            double subtotal = parseSafeDouble(txtSubtotal.getText());
            double vat = parseSafeDouble(txtVat.getText());
            double discount = parseSafeDouble(txtDiscount.getText());
            double total = parseSafeDouble(txtTotal.getText());
            double cash = parseSafeDouble(txtCash.getText());
            double change = parseSafeDouble(txtChange.getText());

            String paymentMethod = String.valueOf(cmbPayment.getSelectedItem());

            if (paymentMethod.startsWith("CASH") && cash < total) {
                JOptionPane.showMessageDialog(this,
                        "Insufficient cash.\nTotal: " + df.format(total),
                        "Payment Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection con = getConnection()) {
                if (con == null) {
                    JOptionPane.showMessageDialog(this, "Database connection is null.");
                    return;
                }

                con.setAutoCommit(false);
                try {
                    int saleId = insertSale(con, invoiceNumber, currentUserId, subtotal, vat, discount, total,
                            paymentMethod, cash, change);

                    for (int i = 0; i < cartModel.getRowCount(); i++) {
                        int productId = toInt(cartModel.getValueAt(i, 5), 0);
                        int qty = toInt(cartModel.getValueAt(i, 1), 1);
                        double price = toDouble(cartModel.getValueAt(i, 2), 0);
                        double lineDisc = toDouble(cartModel.getValueAt(i, 3), 0);
                        double lineSub = toDouble(cartModel.getValueAt(i, 4), 0);

                        insertSaleItem(con, saleId, productId, qty, price, lineDisc, lineSub);

                        if (!deductStock(con, productId, qty)) {
                            throw new SQLException("Stock update failed for product_id: " + productId);
                        }

                        insertInventoryTransaction(con, productId, "Stock Out", qty, invoiceNumber, "Sold via POS");
                    }

                    insertTransactionsLog(con, currentUserId, "SALE",
                            "Completed sale: " + invoiceNumber + " | Total: " + df.format(total));

                    con.commit();

                    JOptionPane.showMessageDialog(this,
                            "Payment successful!\nInvoice: " + invoiceNumber,
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    clearCart();
                    newInvoice();
                    txtBarcode.requestFocusInWindow();

                } catch (Exception ex) {
                    con.rollback();
                    JOptionPane.showMessageDialog(this, "Checkout failed: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    con.setAutoCommit(true);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Checkout error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== RECEIPT =====================
    private void buildReceiptPreview() {
        if (txtReceipt == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("SULIBAGAKENT POS\n");
        sb.append("Invoice: ").append(invoiceNumber).append("\n");
        sb.append("User: ").append(currentUsername).append("\n");
        sb.append("Date: ").append(dtFmt.format(new Date())).append("\n");
        sb.append("----------------------------------------\n");

        if (cartModel != null) {
            for (int i = 0; i < cartModel.getRowCount(); i++) {
                String name = String.valueOf(cartModel.getValueAt(i, 0));
                int qty = toInt(cartModel.getValueAt(i, 1), 1);
                double lineSub = toDouble(cartModel.getValueAt(i, 4), 0);

                sb.append(name).append("\n");
                sb.append("  ").append(qty).append(" x ").append(cartModel.getValueAt(i, 2))
                        .append("  = ").append(df.format(lineSub)).append("\n");
            }
        }

        sb.append("----------------------------------------\n");
        sb.append("Subtotal: ").append(txtSubtotal != null ? txtSubtotal.getText() : "0.00").append("\n");
        sb.append("VAT:      ").append(txtVat != null ? txtVat.getText() : "0.00").append("\n");
        sb.append("Discount: ").append(txtDiscount != null ? txtDiscount.getText() : "0").append("\n");
        sb.append("TOTAL:    ").append(txtTotal != null ? txtTotal.getText() : "0.00").append("\n");
        sb.append("Payment:  ").append(cmbPayment != null ? cmbPayment.getSelectedItem() : "CASH").append("\n");
        sb.append("Cash:     ").append(txtCash != null ? txtCash.getText() : "0").append("\n");
        sb.append("Change:   ").append(txtChange != null ? txtChange.getText() : "0.00").append("\n");
        sb.append("\nThank you!\n");

        txtReceipt.setText(sb.toString());
    }

    // ===================== DB: PRODUCTS =====================
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

    // ===================== DB: SALES =====================
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

    // ===================== DB: INVENTORY TRANSACTIONS =====================
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

    // ===================== DB: ACTION LOG =====================
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

    // ===================== DB CONNECTION =====================
    private Connection getConnection() throws SQLException {
        // Use YOUR existing connection class
        return DbConnection.DBConnection.getConnection();
    }

    // ===================== HELPERS =====================
    private void styleButton(JButton btn, Color base, Color hover) {
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(base.darker(), 1, true));
        btn.setFont(new Font("Yu Gothic UI Semibold", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(base); }
        });
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

    // ===================== INNER MODEL =====================
    private static class Product {
        int productId;
        String barcode;
        String name;
        double sellingPrice;
        int stockQty;
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setPreferredSize(new java.awt.Dimension(1250, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
    }// </editor-fold>//GEN-END:initComponents
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
