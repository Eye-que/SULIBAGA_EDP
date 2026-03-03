package sulibagakent.Screens;

import DbConnection.ProductDAO;
import DbConnection.StockDAO;

import com.toedter.calendar.JDateChooser;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;


public class StockInFrame extends javax.swing.JFrame {

    private final InventoryManagement inventory;

    // display -> product data cache
    private final Map<String, ProductInfo> productMap = new HashMap<>();

    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    // calendar component replacing lblDate (JTextField)
    private JDateChooser dcDate;

    public StockInFrame(InventoryManagement inventory) {
        this.inventory = inventory;
        initComponents();
        setLocationRelativeTo(null);

        setupDateChooser();
        loadProductsToCombo();

        // when select product -> auto fill stock & supplier
        cmbProduct.addActionListener(e -> fillSelectedProductInfo());
    }

    // =========================
    // PRODUCT INFO CONTAINER
    // =========================
    private static class ProductInfo {
        int productId;
        String barcode;
        String name;
        String supplierName;
        int stockQty;

        ProductInfo(int productId, String barcode, String name, String supplierName, int stockQty) {
            this.productId = productId;
            this.barcode = barcode;
            this.name = name;
            this.supplierName = supplierName;
            this.stockQty = stockQty;
        }
    }
private String generateReference(String prefix) {
    // Example: SIN-20260303-141233-582
    return prefix + "-" + new SimpleDateFormat("yyyyMMdd-HHmmss-SSS").format(new Date());
}
    // =========================
    // DATE PICKER (Calendar Icon)
    // =========================
    private ImageIcon safeIcon(String path) {
    java.net.URL url = getClass().getResource(path);
    if (url == null) {
        System.out.println("Icon not found: " + path);
        return null;
    }
    return new ImageIcon(url);
}
private void setupDateChooser() {
    dcDate = new JDateChooser();
    dcDate.setDateFormatString("yyyy-MM-dd");
    dcDate.setDate(new java.util.Date());

    ImageIcon cal = safeIcon("/sulibagakent/Icons/calendar.png");
    if (cal != null) dcDate.setIcon(cal);

    // bounds of the old textbox (lblDate)
    java.awt.Rectangle b = lblDate.getBounds();

    // hide old textfield (keep it, do not delete)
    lblDate.setVisible(false);

    // IMPORTANT: AbsoluteLayout needs AbsoluteConstraints
    jPanel5.add(dcDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(b.x, b.y, b.width, b.height));

    jPanel5.revalidate();
    jPanel5.repaint();
}

    // =========================
    // LOAD PRODUCTS TO COMBO
    // =========================
    private void loadProductsToCombo() {
        try {
            productMap.clear();
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbProduct.getModel();
            model.removeAllElements();
            model.addElement("Select Product");

            // ProductDAO.fetchAll() expected indexes:
            // 0 product_id, 1 barcode, 2 name, 3 category, 4 supplier, 5 desc, 6 cost, 7 selling, 8 stock_qty, 9 reorder
            List<Object[]> rows = ProductDAO.fetchAll();

            for (Object[] r : rows) {
                int productId = Integer.parseInt(String.valueOf(r[0]));
                String barcode = String.valueOf(r[1]);
                String name = String.valueOf(r[2]);
                String supplier = String.valueOf(r[4]);
                int stockQty = Integer.parseInt(String.valueOf(r[8]));

                String display = barcode + " - " + name;
                productMap.put(display, new ProductInfo(productId, barcode, name, supplier, stockQty));
                model.addElement(display);
            }

            cmbProduct.setSelectedIndex(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage());
        }
    }

    // =========================
    // AUTO-FILL WHEN PRODUCT SELECTED
    // =========================
    private void fillSelectedProductInfo() {
        String selected = String.valueOf(cmbProduct.getSelectedItem());
        if (selected == null || selected.equals("Select Product")) {
            txtCurrentStock.setText("");
            txtCurrentStock.setEditable(false);
            setComboSingleValue(cmbSupplierName, "Select Supplier");
            return;
        }

        ProductInfo info = productMap.get(selected);
        if (info == null) return;

        txtCurrentStock.setText(String.valueOf(info.stockQty));
        txtCurrentStock.setEditable(false);

        setComboSingleValue(cmbSupplierName, info.supplierName);
        cmbSupplierName.setEnabled(false);
    }

    private void setComboSingleValue(javax.swing.JComboBox<String> combo, String value) {
        DefaultComboBoxModel<String> m = new DefaultComboBoxModel<>();
        m.addElement(value);
        combo.setModel(m);
        combo.setSelectedIndex(0);
    }

    // =========================
    // CLEAR
    // =========================
    private void clearFields() {
        cmbProduct.setSelectedIndex(0);
        txtCurrentStock.setText("");
        txtQuantityAdded.setText("");
        txtReferenceNo.setText("");
        txtRemarks.setText("");

        // reset date to today
        if (dcDate != null) dcDate.setDate(new Date());
        setComboSingleValue(cmbSupplierName, "Select Supplier");
         txtReferenceNo.setText(generateReference("SIN"));
    }

    // =========================
    // SAVE STOCK IN
    // =========================
    private void saveStockEntry() {
        String selected = String.valueOf(cmbProduct.getSelectedItem());
        if (selected == null || selected.equals("Select Product")) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            return;
        }

        ProductInfo info = productMap.get(selected);
        if (info == null) {
            JOptionPane.showMessageDialog(this, "Invalid product selection.");
            return;
        }

        String qtyStr = txtQuantityAdded.getText().trim();
        String referenceNo = txtReferenceNo.getText().trim();
        String remarks = txtRemarks.getText().trim();

        if (qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quantity.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a whole number.");
            return;
        }

        // Reference Number required for stock in (based on your requirement)
        if (referenceNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reference Number is required for Stock In.");
            return;
        }

        Date d = (dcDate == null) ? null : dcDate.getDate();
        if (d == null) {
            JOptionPane.showMessageDialog(this, "Please select a date.");
            return;
        }
        String date = ymd.format(d); // YYYY-MM-DD

        try {
            // If your StockDAO doesn't accept date yet, remove date here.
            boolean ok = StockDAO.stockIn(info.productId, qty, referenceNo, remarks);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Stock entry saved!");

                // refresh inventory table if you have it
                if (inventory != null) inventory.refreshTables();

                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Save failed. Please try again.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
        }
    }
private void loadSuppliersToCombo() {
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("Select Supplier");

        String sql = "SELECT name FROM suppliers ORDER BY name ASC";

        // 🔥 IMPORTANT: change this if your connection class is different
        con = (Connection) DbConnection.DBConnection.getConnection();

        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();

        while (rs.next()) {
            model.addElement(rs.getString("name"));
        }

        cmbSupplierName.setModel(model);
        cmbSupplierName.setSelectedIndex(0);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Failed to load suppliers: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
        try { if (ps != null) ps.close(); } catch (Exception ignored) {}
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField12 = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCurrentStock = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbSupplierName = new javax.swing.JComboBox<>();
        cmbProduct = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtQuantityAdded = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lblDate = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtReferenceNo = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtRemarks = new javax.swing.JTextArea();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        btnView = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(94, 197, 168));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setText("Select Product:");
        jPanel5.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, -1, -1));

        jLabel4.setText("Current Stock:");
        jPanel5.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, -1, -1));

        txtCurrentStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCurrentStockActionPerformed(evt);
            }
        });
        jPanel5.add(txtCurrentStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 110, 240, 40));

        jLabel6.setText("Supplier Name: ");
        jPanel5.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 300, -1, -1));

        cmbSupplierName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Supplier" }));
        jPanel5.add(cmbSupplierName, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 290, 240, 40));

        jPanel5.add(cmbProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 240, 40));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel1.setText("POS - PRODUCT STOCKING MODULE");
        jPanel5.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, -1, -1));

        jLabel12.setText("Quantity Added:  ");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, -1, -1));
        jPanel5.add(txtQuantityAdded, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 170, 240, 40));

        jLabel7.setText("Date:");
        jPanel5.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, -1, -1));
        jPanel5.add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 230, 240, 40));

        jLabel8.setText("Reference Number:");
        jPanel5.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 240, -1, -1));

        txtReferenceNo.setEditable(false);
        jPanel5.add(txtReferenceNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 230, 240, 40));

        jLabel19.setText("Remaks: ");
        jPanel5.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 60, -1, -1));

        txtRemarks.setColumns(20);
        txtRemarks.setRows(5);
        jScrollPane1.setViewportView(txtRemarks);

        jPanel5.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 50, 240, 160));

        btnSave.setText("Save Stock Entry");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel5.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 380, -1, -1));

        btnClear.setText("Clear Fields");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel5.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 380, -1, -1));

        btnExit.setText("Exit / Back");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        jPanel5.add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 380, 90, -1));

        btnView.setText("View Stock List");
        jPanel5.add(btnView, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 380, -1, -1));

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 820, 420));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
    int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_btnExitActionPerformed

    private void txtCurrentStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCurrentStockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCurrentStockActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
    saveStockEntry();;
    }//GEN-LAST:event_btnSaveActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        loadSuppliersToCombo();
         txtReferenceNo.setText(generateReference("SIN"));
    }//GEN-LAST:event_formWindowActivated

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
    clearFields();
    }//GEN-LAST:event_btnClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox<String> cmbProduct;
    private javax.swing.JComboBox<String> cmbSupplierName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField lblDate;
    private javax.swing.JTextField txtCurrentStock;
    private javax.swing.JTextField txtQuantityAdded;
    private javax.swing.JTextField txtReferenceNo;
    private javax.swing.JTextArea txtRemarks;
    // End of variables declaration//GEN-END:variables
}
