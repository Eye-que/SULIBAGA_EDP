package sulibagakent.Screens;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import DbConnection.ProductDAO;
import static com.mysql.cj.conf.PropertyKey.logger;
import java.sql.SQLException;
import java.util.List;

public final class ProductsManagement extends javax.swing.JFrame {

    public ProductsManagement() {
        initComponents();
        refreshProducts();
    }

    public void refreshProducts() {
    try {
        DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
        model.setRowCount(0);

        List<Object[]> rows = ProductDAO.fetchAll();
        for (Object[] r : rows) {
            model.addRow(r); // ✅ expects 12 values
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Load products failed: " + e.getMessage());
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        logoutBtn = new javax.swing.JButton();
        Products = new javax.swing.JLabel();
        btnBack = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JLabel();
        btnAddProduct = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1250, 850));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(94, 197, 168));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("PRODUCT MANAGEMENT");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, -1, -1));

        logoutBtn.setBackground(new java.awt.Color(109, 213, 180));
        logoutBtn.setText("Logout");
        logoutBtn.addActionListener(this::logoutBtnActionPerformed);
        jPanel1.add(logoutBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 40, -1, -1));

        Products.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/products.png"))); // NOI18N
        Products.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ProductsMouseClicked(evt);
            }
        });
        Products.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProductsKeyPressed(evt);
            }
        });
        jPanel1.add(Products, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1240, 90));

        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/turn-back.png"))); // NOI18N
        btnBack.setText("Back to Dashboard");
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBackMouseClicked(evt);
            }
        });
        getContentPane().add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 750, -1, -1));

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/reload.png"))); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });
        getContentPane().add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 160, -1, -1));

        btnAddProduct.setBackground(new java.awt.Color(109, 213, 180));
        btnAddProduct.setText("Add Product");
        btnAddProduct.addActionListener(this::btnAddProductActionPerformed);
        getContentPane().add(btnAddProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 140, -1, 30));

        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Barcode", "Product ID", "Name", "Category", "Description", "Cost Price", "Selling Price", "Quantity Stock", "Reoder lvl", "Supplier", "Units", "Status"
            }
        ));
        tblProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProducts);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 1140, 540));
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 140, 150, 30));

        jLabel2.setText("Search: ");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 150, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Logout",JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(confirm == JOptionPane.YES_OPTION){
            LoginScreen l = new LoginScreen();
            l.setVisible(true);
            this.dispose();
        }else{
        }
    }//GEN-LAST:event_logoutBtnActionPerformed

    private void btnBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBackMouseClicked
            Dashboard d = new Dashboard();
            d.setVisible(true);
            this.setVisible(false);// TODO add your handling code here:
    }//GEN-LAST:event_btnBackMouseClicked

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked
    refreshProducts();
    }//GEN-LAST:event_btnRefreshMouseClicked

    private void btnAddProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddProductActionPerformed
     ProductScreen ps = new ProductScreen(this);
        ps.setVisible(true);
        this.dispose();       
    }//GEN-LAST:event_btnAddProductActionPerformed

    private void ProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProductsMouseClicked
   
    }//GEN-LAST:event_ProductsMouseClicked

    private void ProductsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProductsKeyPressed

    }//GEN-LAST:event_ProductsKeyPressed

    private void tblProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductsMouseClicked
   int viewRow = tblProducts.getSelectedRow();
    if (viewRow == -1) return;

    // ✅ important if you later add sorting/filtering
    int row = tblProducts.convertRowIndexToModel(viewRow);

    String barcode      = tblProducts.getModel().getValueAt(row, 0).toString();
    String productID    = tblProducts.getModel().getValueAt(row, 1).toString();
    String productName  = tblProducts.getModel().getValueAt(row, 2).toString();
    String category     = tblProducts.getModel().getValueAt(row, 3).toString();
    String description  = tblProducts.getModel().getValueAt(row, 4).toString();
    String costPrice    = tblProducts.getModel().getValueAt(row, 5).toString();
    String sellingPrice = tblProducts.getModel().getValueAt(row, 6).toString();
    String quantity     = tblProducts.getModel().getValueAt(row, 7).toString();
    String reorderLevel = tblProducts.getModel().getValueAt(row, 8).toString();
    String supplier     = tblProducts.getModel().getValueAt(row, 9).toString();
    String unit         = tblProducts.getModel().getValueAt(row, 10).toString();
    String status       = tblProducts.getModel().getValueAt(row, 11).toString();

    ProductScreen ps = new ProductScreen(
            this,
            row,
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

    ps.setVisible(true);
    this.dispose();
    }//GEN-LAST:event_tblProductsMouseClicked

    /**
     * @param args the command line arguments
     */
public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (Exception ex) {
        java.util.logging.Logger.getLogger(ProductsManagement.class.getName())
                .log(java.util.logging.Level.SEVERE, null, ex);
    }

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(() -> new ProductsManagement().setVisible(true));
}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Products;
    private javax.swing.JButton btnAddProduct;
    private javax.swing.JLabel btnBack;
    private javax.swing.JLabel btnRefresh;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JTable tblProducts;
    // End of variables declaration//GEN-END:variables
}
