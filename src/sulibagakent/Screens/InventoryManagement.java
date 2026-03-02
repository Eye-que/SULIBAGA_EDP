package sulibagakent.Screens;

import DbConnection.StockDAO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InventoryManagement extends Gradient {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(InventoryManagement.class.getName());

    private Dashboard dashboard;

    // For filtering Stock Monitoring table
    private TableRowSorter<DefaultTableModel> stockSorter;

    public InventoryManagement(Dashboard dashboard) {
        this.dashboard = dashboard;
        initComponents();

        setupTables();
        setupSearchAndCategoryFilter();

        refreshTables();
    }

    // ==========================
    // SETUP: table models + sorter + renderer (low stock highlight)
    // ==========================
    private void setupTables() {
        // STOCK MONITORING TABLE (jTable2)
        DefaultTableModel stockModel = (DefaultTableModel) jTable2.getModel();
        stockSorter = new TableRowSorter<>(stockModel);
        jTable2.setRowSorter(stockSorter);

        // Highlight low stock rows
        jTable2.setDefaultRenderer(Object.class, new LowStockRenderer());

        // TRANSACTIONS TABLE (jTable1)
        // No special sorter needed, but ok if you want.
    }

    // ==========================
    // SETUP: Search + Category Filter (Stock Monitoring only)
    // ==========================
    private void setupSearchAndCategoryFilter() {
        // Fix category combo default items
        jComboBox1.removeAllItems();
        jComboBox1.addItem("All Categories");

        // Search listener
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        // Category filter listener
        jComboBox1.addActionListener(e -> applyFilters());
    }

    private void applyFilters() {
        String search = jTextField1.getText().trim().toLowerCase();
        String category = String.valueOf(jComboBox1.getSelectedItem());

        stockSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {

                // columns: Barcode(0), Product Name(1), Category(2), Supplier(3), Stock Qty(4), Reorder(5), Status(6)
                String barcode = entry.getStringValue(0).toLowerCase();
                String name = entry.getStringValue(1).toLowerCase();
                String cat = entry.getStringValue(2);

                boolean matchesSearch = search.isEmpty()
                        || barcode.contains(search)
                        || name.contains(search);

                boolean matchesCategory = category == null
                        || category.equals("All Categories")
                        || cat.equalsIgnoreCase(category);

                return matchesSearch && matchesCategory;
            }
        });
    }

    // ==========================
    // MAIN REFRESH (Requirement: JTable display)
    // ==========================
    public final void refreshTables() {
        try {
            loadStockMonitoring();
            loadTransactionHistory();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
        }
    }

    // ==========================
    // STOCK MONITORING (products table)
    // ==========================
    private void loadStockMonitoring() throws SQLException {
        List<Object[]> rows = StockDAO.fetchStockMonitoring();

        // jTable2 columns:
        // "Barcode", "Product Name", "Category", "Supplier", "Stock Qty.", "Reorder Level", "Stock Status"
        DefaultTableModel m = (DefaultTableModel) jTable2.getModel();
        m.setRowCount(0);

        // Build category list from current data
        Set<String> categorySet = new HashSet<>();

        int totalUnits = 0;
        int lowStockCount = 0;
        int outOfStockCount = 0;

        for (Object[] r : rows) {
            // fetchStockMonitoring returns:
            // product_id(0), barcode(1), name(2), category_name(3), supplier_name(4), stock_quantity(5), reorder_level(6)

            String barcode = String.valueOf(r[1]);
            String name = String.valueOf(r[2]);
            String category = String.valueOf(r[3]);
            String supplier = String.valueOf(r[4]);
            int stockQty = Integer.parseInt(String.valueOf(r[5]));
            int reorder = Integer.parseInt(String.valueOf(r[6]));

            String status;
            if (stockQty <= 0) status = "OUT OF STOCK";
            else if (stockQty <= reorder) status = "LOW STOCK";
            else status = "IN STOCK";

            totalUnits += stockQty;
            if (stockQty <= 0) outOfStockCount++;
            if (stockQty > 0 && stockQty <= reorder) lowStockCount++;

            categorySet.add(category);

            m.addRow(new Object[]{
                    barcode, name, category, supplier, stockQty, reorder, status
            });
        }

        // Update cards (labels)
        jLabel13.setText("Total Stock Units: " + totalUnits);
        jLabel14.setText("Low Stock Items: " + lowStockCount);
        jLabel15.setText("Out of Stock Items: " + outOfStockCount);

        // Expiring soon not in requirement -> show 0 (or remove panel)
        jLabel16.setText("Expiring Soon: 0");

        // Refresh category filter items
        Object selected = jComboBox1.getSelectedItem();
        jComboBox1.removeAllItems();
        jComboBox1.addItem("All Categories");
        for (String c : categorySet) jComboBox1.addItem(c);

        // Keep selected if still exists
        if (selected != null) {
            for (int i = 0; i < jComboBox1.getItemCount(); i++) {
                if (String.valueOf(jComboBox1.getItemAt(i)).equalsIgnoreCase(String.valueOf(selected))) {
                    jComboBox1.setSelectedIndex(i);
                    break;
                }
            }
        }

        applyFilters(); // keep filters after refresh
    }

    // ==========================
    // TRANSACTION HISTORY (inventory_transactions table)
    // ==========================
    private void loadTransactionHistory() throws SQLException {
        List<Object[]> rows = StockDAO.fetchInventoryTransactions();

        // jTable1 columns:
        // "Date", "Product", "Transaction Type", "Quantity", "Reference No.", "Remarks"
        DefaultTableModel m = (DefaultTableModel) jTable1.getModel();
        m.setRowCount(0);

        for (Object[] r : rows) {
            // fetchInventoryTransactions returns:
            // transaction_id(0), product_id(1), barcode(2), product_name(3),
            // transaction_type(4), quantity(5), reference_number(6), reason(7), transaction_date(8)

            String product = r[2] + " - " + r[3];
            m.addRow(new Object[]{
                    r[8],         // Date/Time
                    product,      // Product
                    r[4],         // Type
                    r[5],         // Qty
                    r[6],         // Reference No.
                    r[7]          // Remarks/Reason
            });
        }
    }

    // ==========================
    // RENDERER: highlight low stock rows
    // ==========================
    private class LowStockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) return c;

            int modelRow = table.convertRowIndexToModel(row);

            // stock qty = col 4, reorder = col 5
            int stockQty = Integer.parseInt(String.valueOf(table.getModel().getValueAt(modelRow, 4)));
            int reorder = Integer.parseInt(String.valueOf(table.getModel().getValueAt(modelRow, 5)));

            if (stockQty <= 0) {
                c.setBackground(new Color(255, 220, 220)); // light red
            } else if (stockQty <= reorder) {
                c.setBackground(new Color(255, 245, 200)); // light yellow
            } else {
                c.setBackground(Color.WHITE);
            }

            return c;
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnRefresh = new javax.swing.JLabel();
        btnStock = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblTotalStocks = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblLowStocks = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblOutOfStock = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblExpiringSoon = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1250, 850));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/reload.png"))); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });
        add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 170, -1, -1));

        btnStock.setBackground(new java.awt.Color(109, 213, 180));
        btnStock.setText("Add Stock");
        btnStock.addActionListener(this::btnStockActionPerformed);
        add(btnStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 160, 120, 30));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/turn-back.png"))); // NOI18N
        jLabel1.setText("Back to Dashboard");
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 750, -1, -1));

        jTabbedPane1.setPreferredSize(new java.awt.Dimension(900, 80));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Barcode", "Product Name", "Category", "Supplier", "Stock Qty.", "Reorder Level", "Stock Satus"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jTabbedPane1.addTab("Stock Monitoring", jScrollPane2);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Date", "Product", "Transaction Type", "Quantity", "Reference No.", "Remarks"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.addTab("Stock Transaction", jScrollPane1);

        add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 1480, 440));

        jLabel2.setText("Search: ");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, -1));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 28)); // NOI18N
        jLabel6.setText("INVENTORY MANAGEMENT");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jLabel7.setText("Manage Stock, Batches, and Product Availability");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/checklist.png"))); // NOI18N
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));
        add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 160, 150, 30));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 160, 150, 30));

        jLabel3.setText("Category:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 170, -1, -1));

        btnUpdate.setBackground(new java.awt.Color(109, 213, 180));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(this::btnUpdateActionPerformed);
        add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 160, 120, 30));

        btnDelete.setBackground(new java.awt.Color(109, 213, 180));
        btnDelete.setText("Delete");
        add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 160, 120, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/download (2).png"))); // NOI18N
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 160, -1, -1));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/package.png"))); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setText("Total Stock Units");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        lblTotalStocks.setText("{}");
        jPanel1.add(lblTotalStocks, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 200, 70));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/alarm.png"))); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("Low Stock Items");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        lblLowStocks.setText("{}");
        jPanel2.add(lblLowStocks, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, -1, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 200, 70));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/close.png"))); // NOI18N
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setText("Out of Stock Items");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        lblOutOfStock.setText("{}");
        jPanel3.add(lblOutOfStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 80, 220, 70));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/clock.png"))); // NOI18N
        jPanel4.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("Expiring Soon");
        jPanel4.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, -1));

        lblExpiringSoon.setText("{}");
        jPanel4.add(lblExpiringSoon, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, -1, -1));

        add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 220, 70));
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked
        refreshTables();
    }//GEN-LAST:event_btnRefreshMouseClicked

    private void btnStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStockActionPerformed
    ProductStockingFrame psf = new ProductStockingFrame(this);
    psf.setVisible(true);
    }//GEN-LAST:event_btnStockActionPerformed

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
    if (dashboard != null) {
        dashboard.showPage("HOME");
    }
    }//GEN-LAST:event_jLabel1MouseClicked

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
     JOptionPane.showMessageDialog(this,
                "Update is not recommended for inventory transactions.\n" +
                "Use Stock In / Stock Out to adjust properly.");
    }//GEN-LAST:event_btnUpdateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JLabel btnRefresh;
    private javax.swing.JButton btnStock;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> jComboBox1;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblExpiringSoon;
    private javax.swing.JLabel lblLowStocks;
    private javax.swing.JLabel lblOutOfStock;
    private javax.swing.JLabel lblTotalStocks;
    // End of variables declaration//GEN-END:variables
}
