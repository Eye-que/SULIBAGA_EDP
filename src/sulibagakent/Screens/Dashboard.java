package sulibagakent.Screens;

import sulibagakent.Screens.Gradients.GradientFrame;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import sulibagakent.CurrentUser;

public class Dashboard extends GradientFrame{
    
    private CardLayout card;


// hover colors
private final Color HOVER_BG  = new Color(109, 213, 180);
private final Color ACTIVE_BG = new Color(70, 170, 145);

private JLabel activeNav = null; // track which nav is selected

public Dashboard() {
    initComponents();
    setupCards();
    setupNavEffects();

    jLabel5.setText("Welcome, " + CurrentUser.firstName);
    showHome();
}

public Dashboard(String firstName) {
    initComponents();
    setupCards();
    setupNavEffects();

    jLabel5.setText("Welcome, " + firstName);
    showHome();
}
private void clearActive() {
    if (activeNav != null) {
        activeNav.setOpaque(false);
        activeNav.repaint();
        activeNav = null;
    }
}

private void showHome() {
    clearActive();       // ✅ remove active highlight
    showPage("HOME");
}
public void showPage(String name) {
    if (card == null) return;
    card.show(pnlContent, name);
}
private void setupCards() {
    card = new CardLayout();
    pnlContent.setLayout(card);

    pnlContent.add(new HomePanel(this), "HOME");

    pnlContent.add(new ProductsManagement(this), "PRODUCTS");
    pnlContent.add(new InventoryManagement(this), "INVENTORY");
    pnlContent.add(new PointOfSales(), "POS");
    pnlContent.add(new Reports(), "REPORTS");
    pnlContent.add(new UsersManagement(this), "USERS");

    // ✅ ADD these two panels (create these classes)
    pnlContent.add(new Categories(this), "CATEGORIES");
    pnlContent.add(new Suppliers(this), "SUPPLIERS");
    pnlContent.add(new SalesHistory(this), "SALES");
}
private void setupNavEffects() {
    wireNav(btnProducts, "PRODUCTS");
    wireNav(btnInventory, "INVENTORY");
    wireNav(btnController, "POS");
    wireNav(btnReports, "REPORTS");
    wireNav(btnUsers, "USERS");
    wireNav(btnSales, "SALES");
    wireNav(btnSuppliers, "USERS");

    // ✅ new nav icons
    wireNav(jLabel3, "CATEGORIES");
    wireNav(jLabel4, "SUPPLIERS");
    wireNav(jLabel2, "PRODUCTS");
    wireNav(jlabel, "INVENTORY");
    wireNav(jLabel6, "POS");
    wireNav(jLabel8, "REPORTS");
    wireNav(jLabel10, "USERS");
    wireNav(btnCategories, "CATEGORIES");
    wireNav(btnSuppliers, "SUPPLIERS");
    wireNav(sales, "SALES");

    // Home logo click = go home
    Home.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    Home.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            showHome();
        }
    });
}
private void wireNav(JLabel label, String pageName) {
    label.setOpaque(false);
    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    label.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    label.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            if (label != activeNav) {
                label.setOpaque(true);
                label.setBackground(HOVER_BG);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (label != activeNav) {
                label.setOpaque(false);
                label.repaint();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            setActive(label);
            showPage(pageName);
        }
    });
}
private void setActive(JLabel clickedLabel) {
    // remove highlight from previous active
    if (activeNav != null) {
        activeNav.setOpaque(false);
        activeNav.repaint();
    }

    // set new active
    activeNav = clickedLabel;
    activeNav.setOpaque(true);
    activeNav.setBackground(ACTIVE_BG);
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        logoutBtn = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        Home = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnProducts = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnInventory = new javax.swing.JLabel();
        jlabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnController = new javax.swing.JLabel();
        btnReports = new javax.swing.JLabel();
        btnUsers = new javax.swing.JLabel();
        pnlContent = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        btnCategories = new javax.swing.JLabel();
        btnSuppliers = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnSales = new javax.swing.JLabel();
        sales = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(247, 252, 250));
        setMinimumSize(new java.awt.Dimension(740, 700));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(94, 197, 168));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logoutBtn.setBackground(new java.awt.Color(109, 213, 180));
        logoutBtn.setText("Logout");
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtnActionPerformed(evt);
            }
        });
        jPanel1.add(logoutBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1410, 40, 80, 30));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Welcome, ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1200, 40, -1, -1));
        jPanel1.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 180, -1));

        Home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/POS (1).png"))); // NOI18N
        Home.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                HomeMouseClicked(evt);
            }
        });
        jPanel1.add(Home, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semilight", 1, 24)); // NOI18N
        jLabel1.setText("SwiftSell POS");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 30, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1580, 90));

        btnProducts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/products.png"))); // NOI18N
        btnProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnProductsMouseClicked(evt);
            }
        });
        btnProducts.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnProductsKeyPressed(evt);
            }
        });
        getContentPane().add(btnProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        jLabel2.setText("PRODUCTS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 120, -1, -1));

        btnInventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/inventory.png"))); // NOI18N
        btnInventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnInventoryMouseClicked(evt);
            }
        });
        getContentPane().add(btnInventory, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 100, -1, -1));

        jlabel.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        jlabel.setText("INVENTORY");
        getContentPane().add(jlabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 120, -1, -1));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        jLabel6.setText("POS CONTROLLER");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 120, -1, -1));

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        jLabel8.setText("REPORTS");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 120, -1, -1));

        btnController.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/management.png"))); // NOI18N
        btnController.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnControllerMouseClicked(evt);
            }
        });
        getContentPane().add(btnController, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 100, -1, -1));

        btnReports.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/seo-report.png"))); // NOI18N
        btnReports.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnReportsMouseClicked(evt);
            }
        });
        getContentPane().add(btnReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 100, -1, -1));

        btnUsers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/group_1.png"))); // NOI18N
        btnUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUsersMouseClicked(evt);
            }
        });
        getContentPane().add(btnUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 100, -1, -1));
        getContentPane().add(pnlContent, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 1600, 700));

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        jLabel10.setText("USERS");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 120, -1, -1));

        btnCategories.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        btnCategories.setText("CATEGORIES");
        getContentPane().add(btnCategories, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 120, -1, -1));

        btnSuppliers.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        btnSuppliers.setText("SUPPLIERS");
        getContentPane().add(btnSuppliers, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 120, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/categories.png"))); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 100, -1, -1));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/supplier.png"))); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 100, -1, -1));

        btnSales.setFont(new java.awt.Font("Segoe UI Semibold", 1, 16)); // NOI18N
        btnSales.setText("SALES HISTORY");
        getContentPane().add(btnSales, new org.netbeans.lib.awtextra.AbsoluteConstraints(1410, 120, -1, -1));

        sales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/revenue.png"))); // NOI18N
        getContentPane().add(sales, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 100, -1, -1));

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

    private void btnProductsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnProductsKeyPressed

   
    }//GEN-LAST:event_btnProductsKeyPressed

    private void btnProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnProductsMouseClicked

    }//GEN-LAST:event_btnProductsMouseClicked

    private void btnInventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInventoryMouseClicked

    }//GEN-LAST:event_btnInventoryMouseClicked

    private void btnControllerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnControllerMouseClicked

    }//GEN-LAST:event_btnControllerMouseClicked

    private void HomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeMouseClicked
        showPage("HOME");
    }//GEN-LAST:event_HomeMouseClicked

    private void btnUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUsersMouseClicked
        showPage("USERS");
    }//GEN-LAST:event_btnUsersMouseClicked

    private void btnReportsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReportsMouseClicked

    }//GEN-LAST:event_btnReportsMouseClicked

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
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Home;
    private javax.swing.JLabel btnCategories;
    private javax.swing.JLabel btnController;
    private javax.swing.JLabel btnInventory;
    private javax.swing.JLabel btnProducts;
    private javax.swing.JLabel btnReports;
    private javax.swing.JLabel btnSales;
    private javax.swing.JLabel btnSuppliers;
    private javax.swing.JLabel btnUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel jlabel;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JLabel sales;
    // End of variables declaration//GEN-END:variables

}
