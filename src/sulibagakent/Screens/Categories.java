/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package sulibagakent.Screens;

import sulibagakent.Screens.Gradients.categoriesGradient;
import DbConnection.CategoryDAO;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Categories extends categoriesGradient{
    
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private int selectedCategoryId = -1;
    private Dashboard dashboard;
public Categories(Dashboard dashboard) {
    this.dashboard = dashboard;
    initComponents();

    setupTable();
    loadCategories();

    btnUpdate.setEnabled(false);
    btnDelete.setEnabled(false);

    setupSearchFilter();
    setupRowClickFillForm();
}
private void setupTable() {
    model = (DefaultTableModel) tblCategories.getModel();

    // Make sure headers are correct
    model.setColumnIdentifiers(new Object[]{"ID", "Name", "Description"});

    // Remove default empty rows from NetBeans
    model.setRowCount(0);

    sorter = new TableRowSorter<>(model);
    tblCategories.setRowSorter(sorter);
}

private void loadCategories() {
    try {
        model.setRowCount(0);
        for (CategoryDAO.Category c : CategoryDAO.getAllCategories()) {
            model.addRow(new Object[]{c.id, c.name, c.description});
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Load failed: " + e.getMessage());
    }
}

private void setupSearchFilter() {
    txtSearch.getDocument().addDocumentListener(new DocumentListener() {
        private void filter() {
            String text = txtSearch.getText().trim();
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                // filter by Category Name (col 1) OR Description (col 2)
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text), 1, 2));
            }
        }
        @Override public void insertUpdate(DocumentEvent e) { filter(); }
        @Override public void removeUpdate(DocumentEvent e) { filter(); }
        @Override public void changedUpdate(DocumentEvent e) { filter(); }
    });
}

private void setupRowClickFillForm() {
    tblCategories.getSelectionModel().addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) return;

        int viewRow = tblCategories.getSelectedRow();
        if (viewRow < 0) return;

        int row = tblCategories.convertRowIndexToModel(viewRow);

        selectedCategoryId = (int) model.getValueAt(row, 0);
        txtCategoryName.setText(String.valueOf(model.getValueAt(row, 1)));
        txtDescription.setText(String.valueOf(model.getValueAt(row, 2)));

        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnAdd.setEnabled(false);
    });
}
private void clearForm() {
    selectedCategoryId = -1;
    txtCategoryName.setText("");
    txtDescription.setText("");

    btnAdd.setEnabled(true);
    btnUpdate.setEnabled(false);
    btnDelete.setEnabled(false);
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCategories = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCategoryName = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnRefresh = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1250, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/group_2.png"))); // NOI18N
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 28)); // NOI18N
        jLabel6.setText("CATEGORIES");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jLabel7.setText("Manage product categories");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        tblCategories.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Name", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblCategories);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 1470, 230));

        jLabel1.setText("Search:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 100, -1, -1));
        add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, 210, 30));
        add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 1580, 10));

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel3.setText("Category Details");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 430, -1, -1));

        jLabel4.setText("Category Name: ");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 480, -1, -1));

        jLabel5.setText("Category Description:");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 530, -1, -1));
        add(txtCategoryName, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 462, 250, 30));

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane2.setViewportView(txtDescription);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 510, 250, -1));

        btnAdd.setBackground(new java.awt.Color(109, 213, 180));
        btnAdd.setText("Add");
        btnAdd.addActionListener(this::btnAddActionPerformed);
        add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 610, 100, 30));

        btnUpdate.setBackground(new java.awt.Color(109, 213, 180));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 610, 100, 30));

        btnDelete.setBackground(new java.awt.Color(109, 213, 180));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(this::btnDeleteActionPerformed);
        add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 610, 100, 30));

        btnClear.setBackground(new java.awt.Color(109, 213, 180));
        btnClear.setText("Clear");
        btnClear.addActionListener(this::btnClearActionPerformed);
        add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 610, 100, 30));

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
    String name = txtCategoryName.getText().trim();
    String desc = txtDescription.getText().trim();

    if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Category Name is required.");
        return;
    }

    try {
        CategoryDAO.addCategory(name, desc);
        JOptionPane.showMessageDialog(this, "Category saved!");
        clearForm();
        loadCategories();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
    }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
            if (selectedCategoryId == -1) {
            JOptionPane.showMessageDialog(this, "Select a category first.");
            return;
        }

        String name = txtCategoryName.getText().trim();
        String desc = txtDescription.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Category Name is required.");
            return;
        }

        try {
            CategoryDAO.updateCategory(selectedCategoryId, name, desc);
            JOptionPane.showMessageDialog(this, "Category updated!");
            clearForm();
            loadCategories();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
      if (selectedCategoryId == -1) {
            JOptionPane.showMessageDialog(this, "Select a category first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this category?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            CategoryDAO.deleteCategory(selectedCategoryId);
            JOptionPane.showMessageDialog(this, "Category deleted!");
            clearForm();
            loadCategories();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
      clearForm();
        tblCategories.clearSelection();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked

    }//GEN-LAST:event_btnRefreshMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JLabel btnRefresh;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblCategories;
    private javax.swing.JTextField txtCategoryName;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
