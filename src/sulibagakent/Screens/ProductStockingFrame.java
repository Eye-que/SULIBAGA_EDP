package sulibagakent.Screens;

import DbConnection.ProductDAO;
import DbConnection.StockDAO;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductStockingFrame extends javax.swing.JFrame {

    private InventoryManagement inventory;

    // display -> product_id
    private final Map<String, Integer> productMap = new HashMap<>();

    private boolean productsLoaded = false;

    public ProductStockingFrame(InventoryManagement inventory) {
        this.inventory = inventory;
        initComponents();
        setLocationRelativeTo(null);

        // Auto date (YYYY-MM-DD)
        lblDate.setText(LocalDate.now().toString());
        lblDate.setEditable(false);

        // Category + Supplier should be display-only (auto-filled)
        cmbCategory.setEnabled(false);
        cmbSupplierName.setEnabled(false);

        loadProductsToCombo();

        // Default transaction type
        jComboBox1.setSelectedIndex(0); // Stock in
    }

    // =========================
    // LOAD PRODUCTS FOR "Select Product"
    // =========================
    private void loadProductsToCombo() {
        try {
            productMap.clear();
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbProduct.getModel();
            model.removeAllElements();
            model.addElement("Select Product");

            // ProductDAO.fetchAll() returns:
            // product_id, barcode, name, category_name, supplier_name, description, cost_price, selling_price, stock_quantity, reorder_level
            List<Object[]> rows = ProductDAO.fetchAll();

            for (Object[] r : rows) {
                int productId = (int) r[0];
                String barcode = String.valueOf(r[1]);
                String name = String.valueOf(r[2]);

                String display = barcode + " - " + name;
                productMap.put(display, productId);
                model.addElement(display);
            }

            cmbProduct.setSelectedIndex(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + e.getMessage());
        }
    }

    // =========================
    // AUTO-FILL product info when selecting product
    // =========================
    private void fillSelectedProductInfo() {
        String selected = (String) cmbProduct.getSelectedItem();
        if (selected == null || selected.equals("Select Product")) {
            txtProductName.setText("");
            setComboSingleValue(cmbCategory, "Select Category");
            setComboSingleValue(cmbSupplierName, "Select Supplier");
            return;
        }

        try {
            int productId = productMap.get(selected);

            // Find the product row again (simple approach using fetchAll)
            List<Object[]> rows = ProductDAO.fetchAll();
            for (Object[] r : rows) {
                int id = (int) r[0];
                if (id == productId) {
                    String name = String.valueOf(r[2]);
                    String categoryName = String.valueOf(r[3]);
                    String supplierName = String.valueOf(r[4]);

                    txtProductName.setText(name);
                    txtProductName.setEditable(false);

                    setComboSingleValue(cmbCategory, categoryName);
                    setComboSingleValue(cmbSupplierName, supplierName);
                    break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to fill product info: " + e.getMessage());
        }
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
        txtProductName.setText("");
        txtQuantityAdded.setText("");
        jTextField1.setText("");     // reference number field (rename to txtReferenceNo if you want)
        txtRemarks.setText("");

        lblDate.setText(LocalDate.now().toString());

        setComboSingleValue(cmbCategory, "Select Category");
        setComboSingleValue(cmbSupplierName, "Select Supplier");
    }

    // =========================
    // SAVE (Stock In / Stock Out)
    // =========================
    private void saveStockEntry() {
        String selectedProduct = (String) cmbProduct.getSelectedItem();
        if (selectedProduct == null || selectedProduct.equals("Select Product")) {
            JOptionPane.showMessageDialog(this, "Please select a product.");
            return;
        }

        Integer productId = productMap.get(selectedProduct);
        if (productId == null) {
            JOptionPane.showMessageDialog(this, "Invalid product selection.");
            return;
        }

        String type = String.valueOf(jComboBox1.getSelectedItem()); // "Stock in" or "Stock Out"
        String qtyStr = txtQuantityAdded.getText().trim();
        String referenceNo = jTextField1.getText().trim(); // rename later if you want
        String remarksOrReason = txtRemarks.getText().trim();

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

        // Requirement validations:
        // Stock In -> Reference Number required
        // Stock Out -> Reason required
        boolean isStockIn = type.equalsIgnoreCase("Stock in");
        boolean isStockOut = type.equalsIgnoreCase("Stock Out") || type.equalsIgnoreCase("Stock out");

        if (isStockIn && referenceNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reference Number is required for Stock In.");
            return;
        }
        if (isStockOut && remarksOrReason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reason is required for Stock Out.");
            return;
        }

        try {
            boolean ok;

            if (isStockIn) {
                ok = StockDAO.stockIn(productId, qty, referenceNo, remarksOrReason);
            } else {
                // For Stock Out, reference can be optional; pass referenceNo anyway
                ok = StockDAO.stockOut(productId, qty, referenceNo, remarksOrReason);
            }

            if (ok) {
                JOptionPane.showMessageDialog(this, "Stock entry saved!");

                if (inventory != null) {
                    inventory.refreshTables();
                }

                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Save failed. Please try again.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField12 = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        cmbCategory = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbSupplierName = new javax.swing.JComboBox<>();
        cmbProduct = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        txtQuantityAdded = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lblDate = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
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

        jLabel4.setText("Product Name: ");
        jPanel5.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, -1, -1));

        txtProductName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProductNameActionPerformed(evt);
            }
        });
        jPanel5.add(txtProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 100, 240, 30));

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Category" }));
        cmbCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoryActionPerformed(evt);
            }
        });
        jPanel5.add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 50, 240, 30));

        jLabel5.setText("Category: ");
        jPanel5.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 60, -1, -1));

        jLabel6.setText("Supplier Name: ");
        jPanel5.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 110, -1, -1));

        cmbSupplierName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel5.add(cmbSupplierName, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 100, 240, 30));

        jPanel5.add(cmbProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 240, 30));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel1.setText("POS - PRODUCT STOCKING MODULE");
        jPanel5.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, -1, -1));

        jLabel3.setText("Transaction Type:");
        jPanel5.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, -1, -1));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Stock in", "Stock Out" }));
        jPanel5.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, 240, 30));

        jLabel12.setText("Quantity Added:  ");
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, -1, -1));
        jPanel5.add(txtQuantityAdded, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 200, 240, 30));

        jLabel7.setText("Date:");
        jPanel5.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 160, -1, -1));
        jPanel5.add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 150, 240, 30));

        jLabel8.setText("Reference Number");
        jPanel5.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 210, -1, -1));

        jTextField1.setText("txtReferenceNo");
        jPanel5.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 200, 240, 30));

        jLabel19.setText("Remaks: ");
        jPanel5.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, -1, -1));

        txtRemarks.setColumns(20);
        txtRemarks.setRows(5);
        jScrollPane1.setViewportView(txtRemarks);

        jPanel5.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 250, 240, 110));

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

    private void cmbCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoryActionPerformed
    fillSelectedProductInfo();
    }//GEN-LAST:event_cmbCategoryActionPerformed

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

    private void txtProductNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProductNameActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
    saveStockEntry();;
    }//GEN-LAST:event_btnSaveActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
 if (!productsLoaded) {
            loadProductsToCombo();
            productsLoaded = true;
        }
    }//GEN-LAST:event_formWindowActivated

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
    clearFields();
    }//GEN-LAST:event_btnClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbProduct;
    private javax.swing.JComboBox<String> cmbSupplierName;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField lblDate;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtQuantityAdded;
    private javax.swing.JTextArea txtRemarks;
    // End of variables declaration//GEN-END:variables
}
