package sulibagakent.Screens;

import DbConnection.UserDAO;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.awt.Frame;
/**
 *
 * @author USER
 */
public class UsersManagement extends javax.swing.JPanel {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UsersManagement.class.getName());

    /**
     * Creates new form Users
     */
private Dashboard dashboard;

public UsersManagement(Dashboard dashboard) {
    this.dashboard = dashboard;
    initComponents();
    hideUsernameColumn();
    loadUsersToTable();
}


private void hideUsernameColumn() {
    // Username column index = 1 (Role=0, Username=1)
    tblUsers.getColumnModel().getColumn(1).setMinWidth(0);
    tblUsers.getColumnModel().getColumn(1).setMaxWidth(0);
    tblUsers.getColumnModel().getColumn(1).setWidth(0);
}
private String getSelectedUsername() {
    int row = tblUsers.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user first.");
        return null;
    }
    return String.valueOf(tblUsers.getValueAt(row, 1)); // hidden Username column
}

private void loadUsersToTable() {
    DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
    model.setRowCount(0);

    for (Object[] row : UserDAO.getAllUsersForTable()) {
        // role, first_name, last_name, email, contact_no, status, username
        model.addRow(new Object[]{
            row[0], // role
            row[6], // username
            row[1], // first name
            row[2], // last name
            row[3], // email
            row[4], // contact
            row[5]  // status
        });
    }
}

public void refreshUsers() {
    loadUsersToTable();
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnUsers = new javax.swing.JLabel();
        logoutBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        lblBack = new javax.swing.JLabel();
        lblRefresh = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnAddUser = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1250, 850));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(94, 197, 168));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("USERS MANAGEMENT");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));

        btnUsers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/group.png"))); // NOI18N
        btnUsers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUsersMouseClicked(evt);
            }
        });
        jPanel1.add(btnUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        logoutBtn.setBackground(new java.awt.Color(109, 213, 180));
        logoutBtn.setText("Logout");
        logoutBtn.addActionListener(this::logoutBtnActionPerformed);
        jPanel1.add(logoutBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 50, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1250, 90));

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Role", "Username", "First Name", "Last Name", "Email", "Contact No.", "Status"
            }
        ));
        jScrollPane1.setViewportView(tblUsers);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 1150, 540));

        lblBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/turn-back.png"))); // NOI18N
        lblBack.setText("Back to Dashboard");
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblBackMouseClicked(evt);
            }
        });
        add(lblBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 750, -1, -1));

        lblRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/reload.png"))); // NOI18N
        lblRefresh.setText("Refresh");
        lblRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblRefreshMouseClicked(evt);
            }
        });
        add(lblRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 150, -1, -1));
        add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 140, 150, 30));

        jLabel1.setText("Search: ");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, -1, -1));

        btnAddUser.setBackground(new java.awt.Color(109, 213, 180));
        btnAddUser.setText("Add Users");
        btnAddUser.addActionListener(this::btnAddUserActionPerformed);
        add(btnAddUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 140, 100, 30));

        btnUpdate.setBackground(new java.awt.Color(109, 213, 180));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 140, 100, 30));

        btnDelete.setBackground(new java.awt.Color(109, 213, 180));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 140, 100, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void btnUsersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUsersMouseClicked

    }//GEN-LAST:event_btnUsersMouseClicked

    private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
   int confirm = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to logout?",
        "Logout",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if(confirm == JOptionPane.YES_OPTION){
        // close the main Dashboard window
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) w.dispose();

        new LoginScreen().setVisible(true);
    }
    }//GEN-LAST:event_logoutBtnActionPerformed

    private void lblBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBackMouseClicked
    if (dashboard != null) {
        dashboard.showPage("HOME");
    }
    }//GEN-LAST:event_lblBackMouseClicked

    private void lblRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblRefreshMouseClicked
        loadUsersToTable();
    }//GEN-LAST:event_lblRefreshMouseClicked

    private void btnAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddUserActionPerformed
    Window w = SwingUtilities.getWindowAncestor(this);
    Frame owner = (w instanceof Frame) ? (Frame) w : null;

    AddUser dialog = new AddUser(owner, this);
    dialog.setVisible(true);
    }//GEN-LAST:event_btnAddUserActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
         int row = tblUsers.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user first.");
        return;
    }

    String role     = String.valueOf(tblUsers.getValueAt(row, 0));
    String username = String.valueOf(tblUsers.getValueAt(row, 1));
    String first    = String.valueOf(tblUsers.getValueAt(row, 2));
    String last     = String.valueOf(tblUsers.getValueAt(row, 3));
    String email    = String.valueOf(tblUsers.getValueAt(row, 4));
    String contact  = String.valueOf(tblUsers.getValueAt(row, 5));
    String status   = String.valueOf(tblUsers.getValueAt(row, 6));

    Window w = SwingUtilities.getWindowAncestor(this);
    Frame owner = (w instanceof Frame) ? (Frame) w : null;

    AddUser dialog = new AddUser(owner, this);
    dialog.setEditMode(role, username, first, last, email, contact, status);
    dialog.setVisible(true);
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        String username = getSelectedUsername();
        if (username == null) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete user '" + username + "'?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = UserDAO.deleteByUsername(username); // make DAO return boolean if possible
        if (ok) {
            JOptionPane.showMessageDialog(this, "User deleted successfully.");
            loadUsersToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed. Please try again.");
        }
    }//GEN-LAST:event_btnDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUser;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel btnUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblRefresh;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JTable tblUsers;
    // End of variables declaration//GEN-END:variables
}
