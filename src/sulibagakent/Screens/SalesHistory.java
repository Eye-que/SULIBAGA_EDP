package sulibagakent.Screens;

import sulibagakent.Screens.Gradients.SalesGradient;
import com.toedter.calendar.JDateChooser;

import DbConnection.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SalesHistory extends SalesGradient {

    private Dashboard dashboard;

    // Replacements for txtStartDate/txtEndDate
    private JDateChooser dcStartDate;
    private JDateChooser dcEndDate;

    private DefaultTableModel salesModel;

    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    private final DecimalFormat money = new DecimalFormat("#,##0.00");

    public SalesHistory(Dashboard dashboard) {
        this.dashboard = dashboard;
        initComponents();

        setupDateChoosers();
        setupTable();
        wireEvents();
        loadSales();
    }

    private void setupDateChoosers() {
        // Remove old textfields from layout (they remain as variables, but not displayed)
        remove(txtStartDate);
        remove(txtEndDate);

        dcStartDate = new JDateChooser();
        dcEndDate = new JDateChooser();

        dcStartDate.setDateFormatString("yyyy-MM-dd");
        dcEndDate.setDateFormatString("yyyy-MM-dd");

        // icon (safe load)
        ImageIcon calIcon = null;
        try {
            calIcon = new ImageIcon(getClass().getResource("/sulibagakent/Icons/calendar.png"));
        } catch (Exception ignored) {}
        if (calIcon != null) {
            dcStartDate.setIcon(calIcon);
            dcEndDate.setIcon(calIcon);
        }

        // Add them back in same position
        add(dcStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, 150, 30));
        add(dcEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 230, 190, 30));

        revalidate();
        repaint();
    }

    private void setupTable() {
        salesModel = (DefaultTableModel) tblSalesHistory.getModel();
        salesModel.setRowCount(0);

        // right align "Total"
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblSalesHistory.getColumnModel().getColumn(5).setCellRenderer(right);

        // Action column "VIEW"
        tblSalesHistory.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());
    }

    // ✅ Only ONE renderer class (no duplicates)
    private static class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setText("VIEW");
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setForeground(new Color(0, 102, 102));
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            return lbl;
        }
    }

    private void wireEvents() {
        btnSearch.addActionListener(e -> loadSales());

        // click VIEW action
        tblSalesHistory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblSalesHistory.rowAtPoint(e.getPoint());
                int col = tblSalesHistory.columnAtPoint(e.getPoint());

                // Action column index = 7
                if (row >= 0 && col == 7) {
                    String invoice = String.valueOf(tblSalesHistory.getValueAt(row, 0));
                    showTransactionDetails(invoice);
                }
            }
        });

        // export icon
        jLabel18.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jLabel18.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showExportMenu(jLabel18);
            }
        });
    }

    private void loadSales() {
        salesModel.setRowCount(0);

        Date start = dcStartDate.getDate();
        Date end = dcEndDate.getDate();
        String cashier = txtCashier.getText().trim();
        String invoice = txtInvoiceNumber.getText().trim();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.invoice_number, s.sale_date, u.username AS cashier, ")
           .append("'Walk-in' AS customer, ")
           .append("(SELECT COALESCE(SUM(si.quantity),0) FROM sale_items si WHERE si.sale_id = s.sale_id) AS items, ")
           .append("s.total_amount, s.payment_method ")
           .append("FROM sales s ")
           .append("LEFT JOIN users u ON u.user_id = s.user_id ")
           .append("WHERE 1=1 ");

        if (start != null) sql.append(" AND DATE(s.sale_date) >= ? ");
        if (end != null) sql.append(" AND DATE(s.sale_date) <= ? ");
        if (!cashier.isEmpty()) sql.append(" AND u.username LIKE ? ");
        if (!invoice.isEmpty()) sql.append(" AND s.invoice_number LIKE ? ");

        sql.append(" ORDER BY s.sale_date DESC ");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (start != null) ps.setString(idx++, ymd.format(start));
            if (end != null) ps.setString(idx++, ymd.format(end));
            if (!cashier.isEmpty()) ps.setString(idx++, "%" + cashier + "%");
            if (!invoice.isEmpty()) ps.setString(idx++, "%" + invoice + "%");

            double totalSales = 0;
            int totalTrans = 0;
            int totalItems = 0;

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String inv = rs.getString("invoice_number");
                    Timestamp dt = rs.getTimestamp("sale_date");
                    String cash = rs.getString("cashier");
                    String customer = rs.getString("customer");
                    int items = rs.getInt("items");
                    double total = rs.getDouble("total_amount");
                    String pay = rs.getString("payment_method");

                    salesModel.addRow(new Object[]{
                            inv,
                            dt == null ? "" : dt.toString(),
                            cash == null ? "" : cash,
                            customer,
                            items,
                            money.format(total),
                            pay,
                            "VIEW"
                    });

                    totalSales += total;
                    totalTrans++;
                    totalItems += items;
                }
            }

            double totalProfit = computeProfit(con, start, end, cashier, invoice);

            lblTotalSales.setText("₱ " + money.format(totalSales));
            lblTotalTranscation.setText(String.valueOf(totalTrans));
            lblTotalSold.setText(String.valueOf(totalItems));
            lblTotalProfit.setText("₱ " + money.format(totalProfit));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Load error: " + ex.getMessage());
        }
    }

    private double computeProfit(Connection con, Date start, Date end, String cashier, String invoice) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COALESCE(SUM((p.selling_price - p.cost_price) * si.quantity),0) AS profit ")
           .append("FROM sales s ")
           .append("JOIN sale_items si ON si.sale_id = s.sale_id ")
           .append("JOIN products p ON p.product_id = si.product_id ")
           .append("LEFT JOIN users u ON u.user_id = s.user_id ")
           .append("WHERE 1=1 ");

        if (start != null) sql.append(" AND DATE(s.sale_date) >= ? ");
        if (end != null) sql.append(" AND DATE(s.sale_date) <= ? ");
        if (cashier != null && !cashier.isEmpty()) sql.append(" AND u.username LIKE ? ");
        if (invoice != null && !invoice.isEmpty()) sql.append(" AND s.invoice_number LIKE ? ");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (start != null) ps.setString(idx++, ymd.format(start));
            if (end != null) ps.setString(idx++, ymd.format(end));
            if (cashier != null && !cashier.isEmpty()) ps.setString(idx++, "%" + cashier + "%");
            if (invoice != null && !invoice.isEmpty()) ps.setString(idx++, "%" + invoice + "%");

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("profit") : 0;
            }
        }
    }

    private void showTransactionDetails(String invoiceNumber) {
        // Simple dialog (you can upgrade later)
        JOptionPane.showMessageDialog(this,
                "Transaction Details for:\n" + invoiceNumber,
                "Transaction Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showExportMenu(Component anchor) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem excel = new JMenuItem("Export to Excel (CSV)");
        JMenuItem pdf = new JMenuItem("Export to PDF (Print)");

        excel.addActionListener(e -> exportToCSV());
        pdf.addActionListener(e -> exportToPDF());

        menu.add(excel);
        menu.add(pdf);

        menu.show(anchor, 0, anchor.getHeight());
    }

    private void exportToCSV() {
        if (salesModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("sales_history.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
            for (int c = 0; c < salesModel.getColumnCount(); c++) {
                fw.write(salesModel.getColumnName(c) + (c == salesModel.getColumnCount() - 1 ? "" : ","));
            }
            fw.write("\n");

            for (int r = 0; r < salesModel.getRowCount(); r++) {
                for (int c = 0; c < salesModel.getColumnCount(); c++) {
                    String val = String.valueOf(salesModel.getValueAt(r, c)).replace(",", " ");
                    fw.write(val + (c == salesModel.getColumnCount() - 1 ? "" : ","));
                }
                fw.write("\n");
            }

            JOptionPane.showMessageDialog(this, "Exported successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "CSV export error: " + ex.getMessage());
        }
    }

    private void exportToPDF() {
        try {
            tblSalesHistory.print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "PDF export error: " + ex.getMessage());
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblTotalSales = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblTotalTranscation = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblTotalSold = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblTotalProfit = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtStartDate = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtEndDate = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCashier = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtInvoiceNumber = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSalesHistory = new javax.swing.JTable();
        jLabel21 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1250, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/sales_1.png"))); // NOI18N
        add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 28)); // NOI18N
        jLabel6.setText("SALES HISTORY");
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jLabel7.setText("Manage Stock, Batches, and Product Availability");
        add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/sales (1).png"))); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setText("Total Sales");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        lblTotalSales.setText("{}");
        jPanel1.add(lblTotalSales, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 10, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, 200, 70));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/transaction.png"))); // NOI18N
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("Total Transactions");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, -1));

        lblTotalTranscation.setText("{}");
        jPanel2.add(lblTotalTranscation, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, -1, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 120, 200, 70));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/shopping-cart.png"))); // NOI18N
        jPanel4.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("Total Item Sold");
        jPanel4.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, -1));

        lblTotalSold.setText("{}");
        jPanel4.add(lblTotalSold, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));

        add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 120, 220, 70));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/financial-profit.png"))); // NOI18N
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setText("Total Profit");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        lblTotalProfit.setText("{}");
        jPanel3.add(lblTotalProfit, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, -1, -1));

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 120, 220, 70));

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel1.setText("FILTER TRANSACTION");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel2.setText("SALES SUMMARY");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, -1, -1));

        jLabel3.setText("Start Date:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 240, -1, -1));
        add(txtStartDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, 150, 30));

        jLabel4.setText("End Date:");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 240, -1, -1));
        add(txtEndDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 230, 190, 30));

        jLabel5.setText("Cashier:");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 240, -1, -1));
        add(txtCashier, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 230, 190, 30));

        jLabel17.setText("Invoice Number: ");
        add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 240, -1, -1));
        add(txtInvoiceNumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 230, 180, 30));

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/download (2).png"))); // NOI18N
        add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(1340, 230, -1, -1));
        add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 240, -1, -1));

        jLabel20.setText("Export");
        add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 240, -1, -1));

        btnSearch.setBackground(new java.awt.Color(109, 213, 180));
        btnSearch.setText("Search");
        btnSearch.addActionListener(this::btnSearchActionPerformed);
        add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 230, 150, 30));

        tblSalesHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Invoice #", "Date/Time", "Cashier", "Customer", "Items", "Total", "Payment", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblSalesHistory);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, 1480, 340));

        jLabel21.setText("Showing 1-5 of 120 sales history");
        add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 630, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
    loadSales();
    }//GEN-LAST:event_btnSearchActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
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
    private javax.swing.JLabel lblTotalProfit;
    private javax.swing.JLabel lblTotalSales;
    private javax.swing.JLabel lblTotalSold;
    private javax.swing.JLabel lblTotalTranscation;
    private javax.swing.JTable tblSalesHistory;
    private javax.swing.JTextField txtCashier;
    private javax.swing.JTextField txtEndDate;
    private javax.swing.JTextField txtInvoiceNumber;
    private javax.swing.JTextField txtStartDate;
    // End of variables declaration//GEN-END:variables
}
