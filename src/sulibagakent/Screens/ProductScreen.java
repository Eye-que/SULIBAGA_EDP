package sulibagakent.Screens;

import DbConnection.ActivityDAO;
import DbConnection.ProductDAO;
import DbConnection.DBConnection;

import java.sql.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class ProductScreen extends javax.swing.JFrame {

    private ProductsManagement products;
    private int selectedRow = -1;

    public ProductScreen(ProductsManagement products) {
        this.products = products;
        initComponents();

        // ✅ IMPORTANT: override NetBeans default "Item 1..4"
        cmbCategory.setModel(new DefaultComboBoxModel<>());
        loadCategories();

        cmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{"ACTIVE", "INACTIVE"}));

        btnUpdate.setEnabled(false);
    }

    // ✅ EDIT MODE constructor (barcode included)
    public ProductScreen(ProductsManagement products, int row,
                         String barcode, String productID, String productName, String category,
                         String description, String costPrice, String sellingPrice,
                         String quantity, String reorderLevel, String supplier,
                         String unit, String status) {

        this.products = products;
        this.selectedRow = row;

        initComponents();

        cmbCategory.setModel(new DefaultComboBoxModel<>());
        loadCategories();

        cmbStatus.setModel(new DefaultComboBoxModel<>(new String[]{"ACTIVE", "INACTIVE"}));

        txtBarcode.setText(barcode);
        txtProductID.setText(productID);
        txtProductName.setText(productName);
        selectCategoryByName(category);

        txtDescription.setText(description);
        txtCostPrice.setText(costPrice);
        txtSellingPrice.setText(sellingPrice);
        txtQuantity.setText(quantity);
        txtReorderLevel.setText(reorderLevel);
        txtSupplier.setText(supplier);

        cmbUnit.setSelectedItem(unit);
        cmbStatus.setSelectedItem(status);

        btnAddProduct.setEnabled(false);
        btnUpdate.setEnabled(true);
    }

    private void loadCategories() {
        try {
            cmbCategory.removeAllItems();

            String sql = "SELECT category_name FROM categories ORDER BY category_name";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cmbCategory.addItem(rs.getString("category_name"));
                }
            }

            // ✅ if still empty, show warning (means categories table has no rows)
            if (cmbCategory.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No categories found in the database.\nPlease insert category data first.",
                        "Categories Empty",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage());
        }
    }

    private void selectCategoryByName(String categoryName) {
        if (categoryName == null) return;

        for (int i = 0; i < cmbCategory.getItemCount(); i++) {
            String item = cmbCategory.getItemAt(i);
            if (item != null && item.equalsIgnoreCase(categoryName)) {
                cmbCategory.setSelectedIndex(i);
                return;
            }
        }
        // if not found, keep index 0
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtProductID = new javax.swing.JTextField();
        txtProductName = new javax.swing.JTextField();
        cmbCategory = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel18 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtCostPrice = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtSellingPrice = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtReorderLevel = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        cmbUnit = new javax.swing.JComboBox<>();
        cmbStatus = new javax.swing.JComboBox<>();
        btnBack = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnAddProduct = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator9 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(700, 700));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(94, 197, 168));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setText("Category: ");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 70, -1, -1));
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 180, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("PRODUCT INFORMATION");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, -1, -1));

        jLabel1.setText("Product ID: ");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        jLabel2.setText("Product Name:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, -1, -1));
        jPanel2.add(txtProductID, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 180, 30));

        txtProductName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProductNameActionPerformed(evt);
            }
        });
        jPanel2.add(txtProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 180, 31));

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Beverages", "Snacks", "Canned Goods", "Rice & Grains", "Dairy", "Bread & Bakery", "Frozen", "Personal Care", "Household", "School Supplies", "Electronics" }));
        jPanel2.add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 60, 180, 32));

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane2.setViewportView(txtDescription);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 240, 110));

        jLabel3.setText("Description:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 110, -1, -1));
        jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 223, 640, 10));
        jPanel2.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 33, 220, 10));

        jLabel18.setText("Barcode:");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, -1, -1));
        jPanel2.add(txtBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 62, 180, 30));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 660, 230));

        jPanel1.setBackground(new java.awt.Color(94, 197, 168));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setText("Cost Price:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 79, -1, -1));
        jPanel1.add(txtCostPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 71, 185, 33));

        jLabel9.setText("Selling Price:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 129, -1, -1));
        jPanel1.add(txtSellingPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 121, 185, 32));

        jLabel10.setText("Quantity in Stock:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, -1, -1));
        jPanel1.add(txtQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 70, 185, 32));

        jLabel11.setText("Reoder Level:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, -1, -1));
        jPanel1.add(txtReorderLevel, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 120, 185, 32));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("PRICING & INVENTORY");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 310, -1, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("PRICING & INVENTORY");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, -1, -1));
        jPanel1.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 33, 200, 10));
        jPanel1.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 640, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 660, 170));

        jPanel3.setBackground(new java.awt.Color(94, 197, 168));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setText("Supplier Name:");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 77, -1, -1));
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(116, 80, -1, -1));

        jLabel14.setText("Products Status:");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 77, -1, -1));

        jLabel15.setText("Units of Measure:");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 122, -1, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("ADDITIONAL DETAILS");
        jPanel3.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 0, -1, -1));
        jPanel3.add(txtSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 68, 185, 34));

        cmbUnit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PCS", "BOX", "KG", "LITER" }));
        jPanel3.add(cmbUnit, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 114, 185, 32));

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available", "Out of Stock" }));
        jPanel3.add(cmbStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(439, 69, 185, 32));

        btnBack.setText("Exit");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        jPanel3.add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 180, -1, -1));

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jPanel3.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 180, -1, -1));

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel3.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 180, -1, -1));

        btnAddProduct.setText("Add Product");
        btnAddProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddProductActionPerformed(evt);
            }
        });
        jPanel3.add(btnAddProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 180, -1, -1));
        jPanel3.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, -1, -1));
        jPanel3.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 23, 190, 10));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, 660, 240));
        getContentPane().add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 240, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed

    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "exit",JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        ProductsManagement pr = new ProductsManagement();
        pr.show();
        this.dispose();// return to dashboard
    
    }

    }//GEN-LAST:event_btnBackActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
         txtBarcode.setText("");
        txtProductID.setText("");
        txtProductName.setText("");
        txtCostPrice.setText("");
        txtSellingPrice.setText("");
        txtQuantity.setText("");
        txtReorderLevel.setText("");
        txtSupplier.setText("");
        txtDescription.setText("");

        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        cmbUnit.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnAddProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProductActionPerformed
        String barcode = txtBarcode.getText().trim();
        String productID = txtProductID.getText().trim();
        String productName = txtProductName.getText().trim();
        String category = (String) cmbCategory.getSelectedItem();
        String description = txtDescription.getText().trim();
        String supplier = txtSupplier.getText().trim();
        String unit = (String) cmbUnit.getSelectedItem();
        String status = (String) cmbStatus.getSelectedItem();

        if (barcode.isEmpty() || productID.isEmpty() || productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barcode, Product ID, and Product Name are required.");
            return;
        }

        try {
            double costPrice = Double.parseDouble(txtCostPrice.getText().trim());
            double sellingPrice = Double.parseDouble(txtSellingPrice.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            int reorderLevel = Integer.parseInt(txtReorderLevel.getText().trim());

            ProductDAO.insertProduct(
                    barcode,
                    productID,
                    productName,
                    category,
                    description,
                    costPrice,
                    sellingPrice,
                    quantity,
                    reorderLevel,
                    supplier,
                    unit,
                    status
            );

            JOptionPane.showMessageDialog(this, "Product saved to database!");
            if (products != null) products.refreshProducts();
            btnClearActionPerformed(null);

            try { ActivityDAO.log("Added product", "Product: " + productName, "admin"); }
            catch (SQLException ex) {}

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cost/Selling must be decimal. Quantity/Reorder must be whole number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
        }
    }//GEN-LAST:event_btnAddProductActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
    
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No product selected");
            return;
        }

        String barcode = txtBarcode.getText().trim();
        String productID = txtProductID.getText().trim();
        String productName = txtProductName.getText().trim();
        String category = (String) cmbCategory.getSelectedItem();
        String description = txtDescription.getText().trim();
        String supplier = txtSupplier.getText().trim();
        String unit = (String) cmbUnit.getSelectedItem();
        String status = (String) cmbStatus.getSelectedItem();

        if (barcode.isEmpty() || productID.isEmpty() || productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barcode, Product ID, and Product Name are required.");
            return;
        }

        try {
            double costPrice = Double.parseDouble(txtCostPrice.getText().trim());
            double sellingPrice = Double.parseDouble(txtSellingPrice.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            int reorderLevel = Integer.parseInt(txtReorderLevel.getText().trim());

            ProductDAO.updateProduct(
                    barcode,
                    productID,
                    productName,
                    category,
                    description,
                    costPrice,
                    sellingPrice,
                    quantity,
                    reorderLevel,
                    supplier,
                    unit,
                    status
            );

            JOptionPane.showMessageDialog(this, "Product updated in database!");
            if (products != null) {
                products.refreshProducts();
                products.setVisible(true);
            }
            this.dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cost/Selling must be decimal. Quantity/Reorder must be whole number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage());
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtProductNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProductNameActionPerformed

    /**
     * @param args the command line arguments
     */
  public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ProductScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProductScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProductScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProductScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
        ProductsManagement pro = new ProductsManagement(); // example
        new ProductScreen(pro).setVisible(true);
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddProduct;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<String> cmbUnit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtCostPrice;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtProductID;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtReorderLevel;
    private javax.swing.JTextField txtSellingPrice;
    private javax.swing.JTextField txtSupplier;
    // End of variables declaration//GEN-END:variables
}
