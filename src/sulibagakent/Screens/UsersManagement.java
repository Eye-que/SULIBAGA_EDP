package sulibagakent.Screens;

import sulibagakent.Screens.Gradients.UsersGradient;
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
public class UsersManagement extends UsersGradient {
    
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
    tblUsers.getColumnModel().getColumn(1).setPreferredWidth(0);

}
private String getSelectedUsername() {
    int viewRow = tblUsers.getSelectedRow();
    if (viewRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user first.");
        return null;
    }

    int row = tblUsers.convertRowIndexToModel(viewRow);
    return String.valueOf(tblUsers.getModel().getValueAt(row, 1)); // username column
}
private void loadUsersToTable() {
    DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
    model.setRowCount(0);

    try {
        for (Object[] row : UserDAO.getAllUsersForTable()) {
            // DAO returns: role, first, middle, last, status, username
            model.addRow(new Object[]{
                row[0], // Role
                row[5], // Username (hidden column)
                row[1], // First Name
                row[2], // Middle Name
                row[3], // Last Name
                row[4]  // Status
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load users: " + e.getMessage());
    }
}

public void refreshUsers() {
    loadUsersToTable();
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        lblBack = new javax.swing.JLabel();
        lblRefresh = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnAddUser = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblTotalUsers = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblStaffCashiers = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblActiveInactive = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1250, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Role", "Username", "First Name", "Middle Name", "Last Name", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblUsers);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 1480, 440));

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
        add(lblRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 170, -1, -1));
        add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 160, 150, 30));

        jLabel1.setText("Search: ");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, -1));

        btnAddUser.setBackground(new java.awt.Color(109, 213, 180));
        btnAddUser.setText("Add Users");
        btnAddUser.addActionListener(this::btnAddUserActionPerformed);
        add(btnAddUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 160, 130, 30));

        btnUpdate.setBackground(new java.awt.Color(109, 213, 180));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 160, 130, 30));

        btnDelete.setBackground(new java.awt.Color(109, 213, 180));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(970, 160, 130, 30));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 28)); // NOI18N
        jLabel6.setText("USERS MANAGEMENT");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jLabel7.setText("Manage users accounts, roles, and status");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/group_2.png"))); // NOI18N
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel3.setText("Role:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 170, -1, -1));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 160, 150, 30));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/group_2.png"))); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setText("TOTAL USERS");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        lblTotalUsers.setText("{}");
        jPanel1.add(lblTotalUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 200, 70));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/grouping.png"))); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("STAFF/CASHIERS");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, -1));

        lblStaffCashiers.setText("{}");
        jPanel2.add(lblStaffCashiers, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, -1, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 200, 70));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/check_1.png"))); // NOI18N
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setText("ACTIVE/INACTIVE");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, -1));

        lblActiveInactive.setText("{}");
        jPanel3.add(lblActiveInactive, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, -1, -1));

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 200, 70));
    }// </editor-fold>//GEN-END:initComponents

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
 int viewRow = tblUsers.getSelectedRow();
    if (viewRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a user first.");
        return;
    }

    int row = tblUsers.convertRowIndexToModel(viewRow);
    DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();

    String role     = String.valueOf(model.getValueAt(row, 0));
    String username = String.valueOf(model.getValueAt(row, 1));
    String first    = String.valueOf(model.getValueAt(row, 2));
    String middle   = String.valueOf(model.getValueAt(row, 3));
    String last     = String.valueOf(model.getValueAt(row, 4));
    String status   = String.valueOf(model.getValueAt(row, 5));

    Window w = SwingUtilities.getWindowAncestor(this);
    Frame owner = (w instanceof Frame) ? (Frame) w : null;

    AddUser dialog = new AddUser(owner, this);
    dialog.setEditMode(role, username, first, middle, last, status);
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

    boolean ok = UserDAO.deleteByUsername(username);
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
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblActiveInactive;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblRefresh;
    private javax.swing.JLabel lblStaffCashiers;
    private javax.swing.JLabel lblTotalUsers;
    private javax.swing.JTable tblUsers;
    // End of variables declaration//GEN-END:variables
}
