package sulibagakent.Screens;

import DbConnection.SupplierDAO;
import sulibagakent.Screens.Gradients.SuppliersGradient;

import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Suppliers extends SuppliersGradient {

    private Dashboard dashboard;

    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private int selectedSupplierId = -1;

    // simple email check (ok for school projects)
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public Suppliers(Dashboard dashboard) {
        this.dashboard = dashboard;
        initComponents();

        setupTable();
        loadSuppliers();

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        setupRowClickFillForm();
        setupSearchFilter();
    }

    // ------------------- TABLE SETUP -------------------
    private void setupTable() {
        model = new DefaultTableModel(
                new Object[]{"ID", "Supplier Name", "Contact Person", "Contact Number", "Email Address", "Address"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSuppliers.setModel(model);
        sorter = new TableRowSorter<>(model);
        tblSuppliers.setRowSorter(sorter);

        tblSuppliers.setRowHeight(28);
        tblSuppliers.getTableHeader().setReorderingAllowed(false);
    }

    private void loadSuppliers() {
        try {
            model.setRowCount(0);

            for (SupplierDAO.Supplier s : SupplierDAO.getAll()) {
                model.addRow(new Object[]{
                        s.id,
                        s.name,
                        s.contactPerson,
                        s.contactNumber,
                        s.email,
                        s.address
                });
            }

            tblSuppliers.clearSelection();
            selectedSupplierId = -1;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load failed: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------- SEARCH -------------------
    private void setupSearchFilter() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void filterNow() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }

            @Override public void insertUpdate(DocumentEvent e) { filterNow(); }
            @Override public void removeUpdate(DocumentEvent e) { filterNow(); }
            @Override public void changedUpdate(DocumentEvent e) { filterNow(); }
        });
    }

    // ------------------- ROW CLICK -> FORM -------------------
    private void setupRowClickFillForm() {
        tblSuppliers.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            int viewRow = tblSuppliers.getSelectedRow();
            if (viewRow < 0) return;

            int row = tblSuppliers.convertRowIndexToModel(viewRow);

            selectedSupplierId = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
            txtSupplierName.setText(String.valueOf(model.getValueAt(row, 1)));
            txtContactPerson.setText(String.valueOf(model.getValueAt(row, 2)));
            txtContactNumber.setText(String.valueOf(model.getValueAt(row, 3)));
            txtEmailAddress.setText(String.valueOf(model.getValueAt(row, 4)));
            txtAddress.setText(String.valueOf(model.getValueAt(row, 5)));

            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });
    }

    // ------------------- VALIDATION -------------------
    private boolean validateInputs() {
        String name = txtSupplierName.getText().trim();
        String person = txtContactPerson.getText().trim();
        String number = txtContactNumber.getText().trim();
        String email = txtEmailAddress.getText().trim();
        String addr = txtAddress.getText().trim();

        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Supplier Name is required."); return false; }
        if (person.isEmpty()) { JOptionPane.showMessageDialog(this, "Contact Person is required."); return false; }
        if (number.isEmpty()) { JOptionPane.showMessageDialog(this, "Contact Number is required."); return false; }
        if (email.isEmpty()) { JOptionPane.showMessageDialog(this, "Email Address is required."); return false; }
        if (addr.isEmpty()) { JOptionPane.showMessageDialog(this, "Address is required."); return false; }

        // optional checks
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        selectedSupplierId = -1;
        txtSupplierName.setText("");
        txtContactPerson.setText("");
        txtContactNumber.setText("");
        txtEmailAddress.setText("");
        txtAddress.setText("");

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        tblSuppliers.clearSelection();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSuppliers = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtSupplierName = new javax.swing.JTextField();
        txtContactPerson = new javax.swing.JTextField();
        txtContactNumber = new javax.swing.JTextField();
        txtEmailAddress = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnRefresh = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1250, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 28)); // NOI18N
        jLabel6.setText("SUPPLIERS MANAGEMENT");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/group_2.png"))); // NOI18N
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel7.setText("Manage suppliers accounts and details");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel1.setText("Search:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, -1, -1));
        add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 210, 30));

        tblSuppliers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Supplier Name", "Contact Person", "Contact Number", "Email Address", "Address"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblSuppliers);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 1470, 240));
        add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 380, 1580, 10));

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel3.setText("Suplier Details");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 390, -1, -1));

        jLabel4.setText("Supplier Name:");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 430, -1, -1));

        jLabel5.setText("Contact Person:");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 470, -1, -1));

        jLabel8.setText("Contact Number:");
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 510, -1, -1));

        jLabel9.setText("Email Address:");
        add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 550, -1, -1));

        jLabel10.setText("Address:");
        add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 590, -1, -1));
        add(txtSupplierName, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 422, 310, 30));
        add(txtContactPerson, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 462, 310, 30));
        add(txtContactNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 502, 310, 30));
        add(txtEmailAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 542, 310, 30));
        add(txtAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 582, 310, 30));

        btnAdd.setBackground(new java.awt.Color(109, 213, 180));
        btnAdd.setText("Add");
        btnAdd.addActionListener(this::btnAddActionPerformed);
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 620, 100, 30));

        btnUpdate.setBackground(new java.awt.Color(109, 213, 180));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 620, 100, 30));

        btnDelete.setBackground(new java.awt.Color(109, 213, 180));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 620, 100, 30));

        btnClear.setBackground(new java.awt.Color(109, 213, 180));
        btnClear.setText("Clear");
        btnClear.addActionListener(this::btnClearActionPerformed);
        add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 620, 100, 30));

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/reload.png"))); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });
        add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(1460, 110, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
                if (!validateInputs()) return;

        String name = txtSupplierName.getText().trim();
        String person = txtContactPerson.getText().trim();
        String number = txtContactNumber.getText().trim();
        String email = txtEmailAddress.getText().trim();
        String addr = txtAddress.getText().trim();

        try {
            SupplierDAO.add(name, person, number, email, addr);
            JOptionPane.showMessageDialog(this, "Supplier added!");
            clearForm();
            loadSuppliers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Add failed: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (selectedSupplierId == -1) {
            JOptionPane.showMessageDialog(this, "Select a supplier first.");
            return;
        }
        if (!validateInputs()) return;

        String name = txtSupplierName.getText().trim();
        String person = txtContactPerson.getText().trim();
        String number = txtContactNumber.getText().trim();
        String email = txtEmailAddress.getText().trim();
        String addr = txtAddress.getText().trim();

        try {
            SupplierDAO.update(selectedSupplierId, name, person, number, email, addr);
            JOptionPane.showMessageDialog(this, "Supplier updated!");
            clearForm();
            loadSuppliers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update failed: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
                if (selectedSupplierId == -1) {
            JOptionPane.showMessageDialog(this, "Select a supplier first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this supplier?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            SupplierDAO.delete(selectedSupplierId);
            JOptionPane.showMessageDialog(this, "Supplier deleted!");
            clearForm();
            loadSuppliers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked
        loadSuppliers();
        JOptionPane.showMessageDialog(this, "Refreshed!");
    }//GEN-LAST:event_btnRefreshMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JLabel btnRefresh;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblSuppliers;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtContactNumber;
    private javax.swing.JTextField txtContactPerson;
    private javax.swing.JTextField txtEmailAddress;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSupplierName;
    // End of variables declaration//GEN-END:variables
}
