package sulibagakent.Screens;

import DbConnection.ActivityDAO;
import DbConnection.ProductDAO;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductScreen extends javax.swing.JFrame {

    private ProductsManagement products;

    // ✅ for EDIT mode (product_id from DB; hidden in UI)
    private Integer editingProductId = null;

    // ✅ store name -> id mapping for combos
    private final Map<String, Integer> categoryMap = new HashMap<>();
    private final Map<String, Integer> supplierMap = new HashMap<>();

    // ✅ store Base64 image
    private String productImageBase64 = null;

    public ProductScreen(ProductsManagement products) {
        this.products = products;
        initComponents();

        loadCategories();
        loadSuppliers();

        btnUpdate.setEnabled(false);
        btnAddProduct.setEnabled(true);
    }

    // ✅ EDIT MODE constructor (use product_id instead of productID string)
    public ProductScreen(ProductsManagement products,
                         int productId,
                         String barcode,
                         String productName,
                         String categoryName,
                         String supplierName,
                         String description,
                         String costPrice,
                         String sellingPrice,
                         String quantity,
                         String reorderLevel) {

        this.products = products;
        this.editingProductId = productId;

        initComponents();
        loadCategories();
        loadSuppliers();

        txtBarcode.setText(barcode);
        txtProductName.setText(productName);
        selectCategoryByName(categoryName);
        selectSupplierByName(supplierName);

        txtDescription.setText(description);
        txtCostPrice.setText(costPrice);
        txtSellingPrice.setText(sellingPrice);
        txtQuantity.setText(quantity);
        txtReorderLevel.setText(reorderLevel);

        btnAddProduct.setEnabled(false);
        btnUpdate.setEnabled(true);
    }

    // =========================
    // LOAD CATEGORIES INTO COMBO
    // =========================
    private void loadCategories() {
        try {
            categoryMap.clear();

            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbCategory.getModel();
            model.removeAllElements();
            model.addElement("Select Category");

            List<Object[]> cats = ProductDAO.fetchCategories(); // returns [id, name]
            for (Object[] c : cats) {
                int id = (int) c[0];
                String name = String.valueOf(c[1]);

                categoryMap.put(name, id);
                model.addElement(name);
            }

            cmbCategory.setSelectedIndex(0);

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
        cmbCategory.setSelectedIndex(0);
    }

    // =========================
    // LOAD SUPPLIERS INTO COMBO
    // =========================
    private void loadSuppliers() {
        try {
            supplierMap.clear();

            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbSupplierName.getModel();
            model.removeAllElements();
            model.addElement("Select Supplier");

            List<Object[]> sups = ProductDAO.fetchSuppliers(); // returns [id, name]
            for (Object[] s : sups) {
                int id = (int) s[0];
                String name = String.valueOf(s[1]);

                supplierMap.put(name, id);
                model.addElement(name);
            }

            cmbSupplierName.setSelectedIndex(0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load suppliers: " + e.getMessage());
        }
    }

    private void selectSupplierByName(String supplierName) {
        if (supplierName == null) return;

        for (int i = 0; i < cmbSupplierName.getItemCount(); i++) {
            String item = cmbSupplierName.getItemAt(i);
            if (item != null && item.equalsIgnoreCase(supplierName)) {
                cmbSupplierName.setSelectedIndex(i);
                return;
            }
        }
        cmbSupplierName.setSelectedIndex(0);
    }

    private int getSelectedCategoryId() {
        String name = (String) cmbCategory.getSelectedItem();
        if (name == null || name.equals("Select Category")) return -1;
        return categoryMap.getOrDefault(name, -1);
    }

    private int getSelectedSupplierId() {
        String name = (String) cmbSupplierName.getSelectedItem();
        if (name == null || name.equals("Select Supplier")) return -1;
        return supplierMap.getOrDefault(name, -1);
    }

    // =========================
    // UPLOAD IMAGE -> BASE64
    // =========================
    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                byte[] bytes = Files.readAllBytes(file.toPath());
                productImageBase64 = Base64.getEncoder().encodeToString(bytes);

                JOptionPane.showMessageDialog(this, "Image uploaded successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Image upload failed: " + e.getMessage());
            }
        }
    }

    // =========================
    // CLEAR FORM
    // =========================
    private void clearForm() {
        txtBarcode.setText("");
        txtProductName.setText("");
        txtCostPrice.setText("");
        txtSellingPrice.setText("");
        txtQuantity.setText("");
        txtReorderLevel.setText("");
        txtDescription.setText("");

        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        if (cmbSupplierName.getItemCount() > 0) cmbSupplierName.setSelectedIndex(0);

        productImageBase64 = null; // reset image
    }

    // =========================
    // ADD PRODUCT
    // =========================
    private void addProduct() {
        String barcode = txtBarcode.getText().trim();
        String name = txtProductName.getText().trim();
        String description = txtDescription.getText().trim();

        int categoryId = getSelectedCategoryId();
        int supplierId = getSelectedSupplierId();

        if (barcode.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barcode and Product Name are required.");
            return;
        }
        if (categoryId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Category.");
            return;
        }
        if (supplierId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Supplier.");
            return;
        }

        try {
            double costPrice = Double.parseDouble(txtCostPrice.getText().trim());
            double sellingPrice = Double.parseDouble(txtSellingPrice.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            int reorderLevel = Integer.parseInt(txtReorderLevel.getText().trim());

            boolean ok = ProductDAO.insertProduct(
                    barcode,
                    name,
                    categoryId,
                    supplierId,
                    description,
                    costPrice,
                    sellingPrice,
                    quantity,
                    reorderLevel,
                    productImageBase64
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Product saved to database!");
                if (products != null) products.refreshProducts();
                clearForm();

                try { ActivityDAO.log("Added product", "Product: " + name, "admin"); }
                catch (SQLException ignored) {}
            } else {
                JOptionPane.showMessageDialog(this, "Save failed. Please try again.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cost/Selling must be decimal. Quantity/Reorder must be whole number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage());
        }
    }

    // =========================
    // UPDATE PRODUCT
    // =========================
    private void updateProduct() {
        if (editingProductId == null) {
            JOptionPane.showMessageDialog(this, "No product selected for update.");
            return;
        }

        String barcode = txtBarcode.getText().trim();
        String name = txtProductName.getText().trim();
        String description = txtDescription.getText().trim();

        int categoryId = getSelectedCategoryId();
        int supplierId = getSelectedSupplierId();

        if (barcode.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Barcode and Product Name are required.");
            return;
        }
        if (categoryId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Category.");
            return;
        }
        if (supplierId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Supplier.");
            return;
        }

        try {
            double costPrice = Double.parseDouble(txtCostPrice.getText().trim());
            double sellingPrice = Double.parseDouble(txtSellingPrice.getText().trim());
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            int reorderLevel = Integer.parseInt(txtReorderLevel.getText().trim());

            boolean ok = ProductDAO.updateProduct(
                    editingProductId,
                    barcode,
                    name,
                    categoryId,
                    supplierId,
                    description,
                    costPrice,
                    sellingPrice,
                    quantity,
                    reorderLevel,
                    productImageBase64
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Product updated in database!");
                if (products != null) products.refreshProducts();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Please try again.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cost/Selling must be decimal. Quantity/Reorder must be whole number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        cmbCategory = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCostPrice = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtSellingPrice = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtReorderLevel = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        btnAddProduct = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        cmbSupplierName = new javax.swing.JComboBox<>();
        btnUploadImage = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(740, 430));
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(94, 197, 168));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setText("Category: ");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("POS - ADDING PRODUCT");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, -1, -1));

        jLabel2.setText("Product Name:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, -1, -1));

        txtProductName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProductNameActionPerformed(evt);
            }
        });
        jPanel2.add(txtProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 180, 31));

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Category" }));
        cmbCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoryActionPerformed(evt);
            }
        });
        jPanel2.add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 310, 180, 32));

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane2.setViewportView(txtDescription);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 60, 240, -1));

        jLabel3.setText("Description:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 70, -1, -1));

        jLabel18.setText("Barcode:");
        jPanel2.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, -1, -1));
        jPanel2.add(txtBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 62, 180, 30));

        jLabel6.setText("Cost Price:");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, -1, -1));
        jPanel2.add(txtCostPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 160, 180, 33));

        jLabel9.setText("Selling Price:");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, -1, -1));
        jPanel2.add(txtSellingPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 210, 180, 32));

        jLabel10.setText("Quantity in Stock:");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 170, -1, -1));
        jPanel2.add(txtQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 160, 240, 32));

        jLabel11.setText("Reoder Level:");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 270, -1, -1));
        jPanel2.add(txtReorderLevel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 260, 180, 32));

        jLabel12.setText("Supplier Name:");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 220, -1, -1));

        btnAddProduct.setText("Add Product");
        btnAddProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddProductActionPerformed(evt);
            }
        });
        jPanel2.add(btnAddProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 360, -1, -1));

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jPanel2.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 360, -1, -1));

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jPanel2.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 360, -1, -1));

        btnBack.setText("Exit");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        jPanel2.add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 360, -1, -1));

        cmbSupplierName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel2.add(cmbSupplierName, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 210, 240, 30));

        btnUploadImage.setText("Upload Image");
        btnUploadImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadImageActionPerformed(evt);
            }
        });
        jPanel2.add(btnUploadImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 260, 240, 30));

        jLabel1.setText("Product Image:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 270, -1, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 730, 400));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
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
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
    clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnAddProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProductActionPerformed
    addProduct();
    }//GEN-LAST:event_btnAddProductActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
    updateProduct();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtProductNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProductNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProductNameActionPerformed

    private void cmbCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCategoryActionPerformed

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
    
    }//GEN-LAST:event_formWindowStateChanged

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
    loadCategories();
    }//GEN-LAST:event_formWindowActivated

    private void btnUploadImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadImageActionPerformed
    uploadImage();
    }//GEN-LAST:event_btnUploadImageActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddProduct;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUploadImage;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbSupplierName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField txtBarcode;
    private javax.swing.JTextField txtCostPrice;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtReorderLevel;
    private javax.swing.JTextField txtSellingPrice;
    // End of variables declaration//GEN-END:variables
}
