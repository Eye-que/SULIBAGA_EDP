/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sulibagakent.Screens;

import DbConnection.ActivityDAO;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import sulibagakent.CurrentUser;

public class Dashboard extends javax.swing.JFrame {
    
    private CardLayout card;
    private javax.swing.JPanel contentPanel;


// hover colors
private final Color HOVER_BG  = new Color(109, 213, 180);
private final Color ACTIVE_BG = new Color(70, 170, 145);

private JLabel activeNav = null; // track which nav is selected

public Dashboard() {
    initComponents();
    applyGradientBackground(); // ✅ must be after initComponents
    setupCards();
    setupNavEffects();

    jLabel5.setText("Welcome, " + CurrentUser.firstName);
    showHome();
}

public Dashboard(String firstName) {
    initComponents();
    applyGradientBackground(); // ✅ must be after initComponents
    setupCards();
    setupNavEffects();

    jLabel5.setText("Welcome, " + firstName);
    showHome();
}
private void applyGradientBackground() {
    GradientPanel bg = new GradientPanel();
    bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    // ✅ IMPORTANT: remove all old components from old content pane first
    getContentPane().removeAll();

    // ✅ replace content pane
    setContentPane(bg);

    // ✅ Use ONE WIDTH only (match your frame size)
    int W = 1270;  // frame width
    int H = 950;   // frame height

    // ✅ keep fixed size (no pack)
    setSize(W, H);

    // Header (match width)
    bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, W, 90));

    // Icons
    bg.add(btnProducts,   new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, -1, -1));
    bg.add(btnInventory,  new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 100, -1, -1));
    bg.add(btnController, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 100, -1, -1));
    bg.add(btnReports,    new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 100, -1, -1));
    bg.add(btnUsers,      new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 100, -1, -1));

    // Labels
    bg.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, -1, -1));
    bg.add(jlabel,  new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 130, -1, -1));
    bg.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 130, -1, -1));
    bg.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 130, -1, -1));
    bg.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010,130, -1, -1));

    // ✅ Content panel (match width)
    bg.add(pnlContent, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, W, 710));

    setLocationRelativeTo(null);
    bg.revalidate();
    bg.repaint();
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

    // ✅ HOME first
    pnlContent.add(new HomePanel(this), "HOME");

    pnlContent.add(new ProductsManagement(this), "PRODUCTS");
    pnlContent.add(new InventoryManagement(this), "INVENTORY");
    pnlContent.add(new POSController(), "POS");
    pnlContent.add(new UsersManagement(this), "USERS");
    pnlContent.add(new Reports(), "REPORTS");
}
private void setupNavEffects() {
    wireNav(btnProducts, "PRODUCTS");
    wireNav(btnInventory, "INVENTORY");
    wireNav(btnController, "POS");
    wireNav(btnReports, "REPORTS");
    wireNav(btnUsers, "USERS");

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
    // Default = transparent (no background)
    label.setOpaque(false);
    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    label.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

    label.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            // show hover only if not active
            if (label != activeNav) {
                label.setOpaque(true);
                label.setBackground(HOVER_BG);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // remove hover only if not active
            if (label != activeNav) {
                label.setOpaque(false);
                label.repaint();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            setActive(label); 
            showPage(pageName);      // switch page
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
        jLabel9 = new javax.swing.JLabel();
        btnController = new javax.swing.JLabel();
        btnReports = new javax.swing.JLabel();
        btnUsers = new javax.swing.JLabel();
        pnlContent = new javax.swing.JPanel();

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
        jPanel1.add(logoutBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 40, 80, 30));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Welcome, ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 40, -1, -1));
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

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 90));

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
        getContentPane().add(btnProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, -1, -1));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        jLabel2.setText("PRODUCTS");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, -1, -1));

        btnInventory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/inventory.png"))); // NOI18N
        btnInventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnInventoryMouseClicked(evt);
            }
        });
        getContentPane().add(btnInventory, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 100, -1, -1));

        jlabel.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 16)); // NOI18N
        jlabel.setText("INVENTORY");
        getContentPane().add(jlabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 130, -1, -1));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        jLabel6.setText("POS CONTROLLER");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 130, -1, -1));

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        jLabel8.setText("REPORTS");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 130, -1, -1));

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 16)); // NOI18N
        jLabel9.setText("USERS");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 130, -1, -1));

        btnController.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/management.png"))); // NOI18N
        btnController.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnControllerMouseClicked(evt);
            }
        });
        getContentPane().add(btnController, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 100, -1, -1));

        btnReports.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/seo-report.png"))); // NOI18N
        btnReports.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnReportsMouseClicked(evt);
            }
        });
        getContentPane().add(btnReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 100, -1, -1));

        btnUsers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/group_1.png"))); // NOI18N
        btnUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUsersMouseClicked(evt);
            }
        });
        getContentPane().add(btnUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 100, -1, -1));
        getContentPane().add(pnlContent, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 1260, 710));

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

    private void btnReportsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnReportsMouseClicked

    }//GEN-LAST:event_btnReportsMouseClicked

    private void HomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_HomeMouseClicked
        showPage("HOME");
    }//GEN-LAST:event_HomeMouseClicked

    private void btnUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUsersMouseClicked
        showPage("USERS");
    }//GEN-LAST:event_btnUsersMouseClicked

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
    private javax.swing.JLabel btnController;
    private javax.swing.JLabel btnInventory;
    private javax.swing.JLabel btnProducts;
    private javax.swing.JLabel btnReports;
    private javax.swing.JLabel btnUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel jlabel;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JPanel pnlContent;
    // End of variables declaration//GEN-END:variables

}
