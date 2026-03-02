package sulibagakent.Screens;

import sulibagakent.Screens.Gradients.PanelGradient;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import DbConnection.ProductDAO;
import java.sql.SQLException;
import java.util.List;
import DbConnection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public final class ProductsManagement extends PanelGradient{
    private Dashboard dashboard;
    public ProductsManagement(Dashboard dashboard) {
    this.dashboard = dashboard;
    initComponents();
    refreshProducts();
}

public void refreshProducts() {
    try {
        DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
        model.setRowCount(0);

        List<Object[]> rows = ProductDAO.fetchAll();
        for (Object[] r : rows) {
            model.addRow(r);
        }

        refreshStats(); // ✅ update the cards too

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Load products failed: " + e.getMessage());
    }
}
private void refreshStats() {

    String sqlTotal    = "SELECT COUNT(*) FROM products";
    String sqlLowStock = "SELECT COUNT(*) FROM products WHERE stock_quantity <= reorder_level AND stock_quantity > 0";
    String sqlOutStock = "SELECT COUNT(*) FROM products WHERE stock_quantity = 0";
    String sqlActive   = "SELECT COUNT(*) FROM products WHERE stock_quantity > 0";

    try (Connection con = DBConnection.getConnection()) {

        lblTotalProducts.setText(String.valueOf(getCount(con, sqlTotal)));     // Total Products
        lblLowStock.setText(String.valueOf(getCount(con, sqlLowStock))); // Low Stock
        lblOutOfStock.setText(String.valueOf(getCount(con, sqlOutStock))); // Out of Stock
        lblActiveProducts.setText(String.valueOf(getCount(con, sqlActive)));   // Active Products

    } catch (Exception e) {
        lblTotalProducts.setText("0");
        lblLowStock.setText("0");
        lblOutOfStock.setText("0");
        lblActiveProducts.setText("0");

        JOptionPane.showMessageDialog(this, "Stats load failed: " + e.getMessage());
    }
}

private int getCount(Connection con, String sql) throws Exception {
    try (PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getInt(1) : 0;
    }
}
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnRefresh = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblTotalProducts = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblLowStock = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblOutOfStock = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblActiveProducts = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1250, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/reload.png"))); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });
        add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 170, -1, -1));

        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Barcode", "Product ID", "Name", "Category", "Description", "Cost Price", "Selling Price", "Quantity Stock", "Reoder lvl", "Supplier"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProducts);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 1480, 420));
        add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 160, 150, 30));

        jLabel2.setText("Search: ");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, -1));

        jLabel1.setText("Category:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 170, -1, -1));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 160, 150, 30));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/received.png"))); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setText("Total Products");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        lblTotalProducts.setText("{}");
        jPanel1.add(lblTotalProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 200, 70));

        btnAdd.setBackground(new java.awt.Color(109, 213, 180));
        btnAdd.setText("Add Product");
        btnAdd.addActionListener(this::btnAddActionPerformed);
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 160, 120, 30));

        btnUpdate.setBackground(new java.awt.Color(109, 213, 180));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 160, 120, 30));

        btnDelete.setBackground(new java.awt.Color(109, 213, 180));
        btnDelete.setText("Delete");
        add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 160, 120, 30));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/warning.png"))); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("Low Stock");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        lblLowStock.setText("{}");
        jPanel2.add(lblLowStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, -1, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 200, 70));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/close.png"))); // NOI18N
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setText("Out of Stock");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, -1));

        lblOutOfStock.setText("{}");
        jPanel3.add(lblOutOfStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, -1, -1));

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 200, 70));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/check.png"))); // NOI18N
        jPanel4.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("Active Products");
        jPanel4.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, -1));

        lblActiveProducts.setText("{}");
        jPanel4.add(lblActiveProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));

        add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 80, 220, 70));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/download (2).png"))); // NOI18N
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 160, -1, -1));

        jLabel3.setText("Status:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 170, -1, -1));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        add(jComboBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 160, 120, 30));

        jLabel5.setText("Showing 1-5 of 120 products");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 630, -1, -1));

        jLabel7.setText("Manage and update your product inventory");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 28)); // NOI18N
        jLabel6.setText("PRODUCTS MANAGEMENT");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/box.png"))); // NOI18N
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked
        refreshProducts();
    }//GEN-LAST:event_btnRefreshMouseClicked

    private void tblProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductsMouseClicked
      
    }//GEN-LAST:event_tblProductsMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
    ProductScreen ps = new ProductScreen(this);
    ps.setVisible(true);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
      int viewRow = tblProducts.getSelectedRow();
        if (viewRow == -1) return;

        // ✅ important if you later add sorting/filtering
        int row = tblProducts.convertRowIndexToModel(viewRow);

        String barcode      = tblProducts.getModel().getValueAt(row, 0).toString();
        String productName  = tblProducts.getModel().getValueAt(row, 2).toString();
        String category     = tblProducts.getModel().getValueAt(row, 3).toString();
        String description  = tblProducts.getModel().getValueAt(row, 4).toString();
        String costPrice    = tblProducts.getModel().getValueAt(row, 5).toString();
        String sellingPrice = tblProducts.getModel().getValueAt(row, 6).toString();
        String quantity     = tblProducts.getModel().getValueAt(row, 7).toString();
        String reorderLevel = tblProducts.getModel().getValueAt(row, 8).toString();
        String supplier     = tblProducts.getModel().getValueAt(row, 9).toString();

        ProductScreen ps = new ProductScreen(
            this,
            row,
            barcode,
            productName,
            category,
            description,
            costPrice,
            sellingPrice,
            quantity,
            reorderLevel,
            supplier

        );

        ps.setVisible(true);
    }//GEN-LAST:event_btnUpdateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JLabel btnRefresh;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblActiveProducts;
    private javax.swing.JLabel lblLowStock;
    private javax.swing.JLabel lblOutOfStock;
    private javax.swing.JLabel lblTotalProducts;
    private javax.swing.JTable tblProducts;
    // End of variables declaration//GEN-END:variables
}
