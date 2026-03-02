package sulibagakent.Screens;

import sulibagakent.Screens.Gradients.HomeGradient;
import DbConnection.DBConnection;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class HomePanel extends HomeGradient {
    // Chart containers
private JPanel pnlDailySales;
private JPanel pnlMonthlySales;
private JPanel pnlTopProducts;

// Chart canvases
private SimpleLineChart chartDaily;
private SimpleBarChart chartMonthly;
private SimpleBarChart chartTop;

private Dashboard mainFrame;
public HomePanel(Dashboard mainFrame) {
    this.mainFrame = mainFrame;
    initComponents();

    setupCharts();   // add 3 charts to your panel
    loadCharts();    // fill them with DB data (or fallback data)
}
private void setupCharts() {
    // === Daily Sales Graph (Line) ===
    pnlDailySales = createChartContainer("Daily Sales (Last 7 Days)");
    chartDaily = new SimpleLineChart();
    pnlDailySales.add(chartDaily, BorderLayout.CENTER);

    // === Monthly Sales Chart (Bar) ===
    pnlMonthlySales = createChartContainer("Monthly Sales (Last 12 Months)");
    chartMonthly = new SimpleBarChart();
    pnlMonthlySales.add(chartMonthly, BorderLayout.CENTER);

    // === Top Selling Products (Bar) ===
    pnlTopProducts = createChartContainer("Top Selling Products (Qty)");
    chartTop = new SimpleBarChart();
    pnlTopProducts.add(chartTop, BorderLayout.CENTER);

    // Position under your cards (adjust coordinates if needed)
    // Your cards are around y=40..120, so charts start below them.
    add(pnlDailySales,  new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 520, 260));
    add(pnlMonthlySales,new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 140, 520, 260));
    add(pnlTopProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 140, 500, 260));

    revalidate();
    repaint();
}

private JPanel createChartContainer(String title) {
    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(Color.WHITE);
    p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(60, 60, 60)
    ));
    return p;
}

private void loadCharts() {
    // Try DB first; if fails, fallback demo data so it stays functional.
    try {
        loadDailySalesFromDB();
        loadMonthlySalesFromDB();
        loadTopProductsFromDB();
    } catch (Exception e) {
        // fallback demo data
        chartDaily.setData(
                Arrays.asList("Mon","Tue","Wed","Thu","Fri","Sat","Sun"),
                Arrays.asList(1200.0, 1800.0, 900.0, 2500.0, 3100.0, 2200.0, 1500.0)
        );

        chartMonthly.setData(
                Arrays.asList("Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","Jan","Feb","Mar"),
                Arrays.asList(15000.0,18000.0,12000.0,22000.0,24000.0,21000.0,26000.0,23000.0,28000.0,30000.0,27000.0,32000.0)
        );

        chartTop.setData(
                Arrays.asList("Coke","Bread","Eggs","Noodles","Coffee"),
                Arrays.asList(140.0, 110.0, 95.0, 80.0, 70.0)
        );
    }
}
private void loadDailySalesFromDB() throws Exception {
    // last 7 days totals
    String sql =
            "SELECT DATE(sale_date) d, COALESCE(SUM(total_amount),0) total " +
            "FROM sales " +
            "WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE(sale_date) " +
            "ORDER BY d";

    Map<String, Double> map = new LinkedHashMap<>();
    // prefill last 7 days = 0 so chart always shows 7 points
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_MONTH, -6);
    for (int i = 0; i < 7; i++) {
        java.sql.Date dt = new java.sql.Date(cal.getTimeInMillis());
        map.put(dt.toString(), 0.0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
    }

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            String d = rs.getDate("d").toString();
            map.put(d, rs.getDouble("total"));
        }
    }

    List<String> labels = new ArrayList<>();
    List<Double> values = new ArrayList<>();
    for (Map.Entry<String, Double> e : map.entrySet()) {
        // show only MM-dd on label
        String s = e.getKey();
        labels.add(s.substring(5));
        values.add(e.getValue());
    }

    chartDaily.setData(labels, values);
}

private void loadMonthlySalesFromDB() throws Exception {
    // last 12 months totals
    String sql =
            "SELECT DATE_FORMAT(sale_date, '%Y-%m') ym, COALESCE(SUM(total_amount),0) total " +
            "FROM sales " +
            "WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL 11 MONTH) " +
            "GROUP BY ym " +
            "ORDER BY ym";

    Map<String, Double> map = new LinkedHashMap<>();
    // prefill last 12 months
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.add(Calendar.MONTH, -11);
    for (int i = 0; i < 12; i++) {
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        String ym = String.format("%04d-%02d", y, m);
        map.put(ym, 0.0);
        cal.add(Calendar.MONTH, 1);
    }

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            String ym = rs.getString("ym");
            map.put(ym, rs.getDouble("total"));
        }
    }

    List<String> labels = new ArrayList<>();
    List<Double> values = new ArrayList<>();
    for (Map.Entry<String, Double> e : map.entrySet()) {
        // show only month (MM)
        labels.add(e.getKey().substring(5));
        values.add(e.getValue());
    }

    chartMonthly.setData(labels, values);
}

private void loadTopProductsFromDB() throws Exception {
    // top 5 by quantity (all time). You can filter by month if you want.
    String sql =
            "SELECT p.product_name name, COALESCE(SUM(si.quantity),0) qty " +
            "FROM sale_items si " +
            "JOIN products p ON p.product_id = si.product_id " +
            "GROUP BY p.product_id, p.product_name " +
            "ORDER BY qty DESC " +
            "LIMIT 5";

    List<String> labels = new ArrayList<>();
    List<Double> values = new ArrayList<>();

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            labels.add(rs.getString("name"));
            values.add(rs.getDouble("qty"));
        }
    }

    // If empty, show something
    if (labels.isEmpty()) {
        labels = Arrays.asList("No Data");
        values = Arrays.asList(0.0);
    }

    chartTop.setData(labels, values);
}
static class SimpleLineChart extends JPanel {
    private List<String> labels = new ArrayList<>();
    private List<Double> values = new ArrayList<>();

    public SimpleLineChart() {
        setBackground(Color.WHITE);
    }

    public void setData(List<String> labels, List<Double> values) {
        this.labels = labels;
        this.values = values;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (labels == null || values == null || labels.isEmpty() || values.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int pad = 35;

        // find max
        double max = 1;
        for (double v : values) max = Math.max(max, v);

        // axes
        g2.setColor(new Color(210, 210, 210));
        g2.drawLine(pad, h - pad, w - pad, h - pad);
        g2.drawLine(pad, pad, pad, h - pad);

        int n = values.size();
        int plotW = (w - pad * 2);
        int plotH = (h - pad * 2);

        int prevX = -1, prevY = -1;

        // line
        g2.setColor(new Color(60, 120, 160));
        for (int i = 0; i < n; i++) {
            int x = pad + (int) ((i / (double) (n - 1)) * plotW);
            int y = (h - pad) - (int) ((values.get(i) / max) * plotH);

            g2.fillOval(x - 3, y - 3, 6, 6);

            if (prevX != -1) g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;

            // labels
            g2.setColor(new Color(80, 80, 80));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String lbl = labels.get(i);
            g2.drawString(lbl, x - 10, h - 12);

            g2.setColor(new Color(60, 120, 160));
        }

        g2.dispose();
    }
}
static class SimpleBarChart extends JPanel {
    private List<String> labels = new ArrayList<>();
    private List<Double> values = new ArrayList<>();

    public SimpleBarChart() {
        setBackground(Color.WHITE);
    }

    public void setData(List<String> labels, List<Double> values) {
        this.labels = labels;
        this.values = values;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (labels == null || values == null || labels.isEmpty() || values.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int pad = 35;

        double max = 1;
        for (double v : values) max = Math.max(max, v);

        // axes
        g2.setColor(new Color(210, 210, 210));
        g2.drawLine(pad, h - pad, w - pad, h - pad);
        g2.drawLine(pad, pad, pad, h - pad);

        int n = values.size();
        int plotW = (w - pad * 2);
        int plotH = (h - pad * 2);

        int barW = Math.max(12, plotW / (n * 2));
        int gap = barW;

        int x = pad + gap / 2;

        for (int i = 0; i < n; i++) {
            double v = values.get(i);
            int barH = (int) ((v / max) * plotH);
            int y = (h - pad) - barH;

            g2.setColor(new Color(90, 170, 190));
            g2.fillRoundRect(x, y, barW, barH, 8, 8);

            g2.setColor(new Color(80, 80, 80));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String lbl = labels.get(i);
            if (lbl.length() > 8) lbl = lbl.substring(0, 8) + "...";
            g2.drawString(lbl, x - 5, h - 12);

            x += barW + gap;
        }

        g2.dispose();
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTotalSalesToday = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblTotalProducts = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblLowStockItems = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblTotalSuppliers = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblTotalUsers = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblTotalCategories = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1250, 750));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setText("TOTAL SALES TODAY");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/sales.png"))); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 70, 60));

        lblTotalSalesToday.setText("{}");
        jPanel1.add(lblTotalSalesToday, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 240, 80));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel14.setText("TOTAL PRODUCTS");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, -1, -1));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/box (1).png"))); // NOI18N
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblTotalProducts.setText("{}");
        jPanel2.add(lblTotalProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, -1, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 40, 250, 80));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel15.setText("LOW STOCK ITEMS");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, -1));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/warning (1).png"))); // NOI18N
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblLowStockItems.setText("{}");
        jPanel3.add(lblLowStockItems, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, -1, -1));

        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 40, 240, 80));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setText("TOTAL SUPPLIERS");
        jPanel4.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, -1, -1));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/delivery-courier.png"))); // NOI18N
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblTotalSuppliers.setText("{}");
        jPanel4.add(lblTotalSuppliers, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, -1, -1));

        add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 40, 260, 80));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel18.setText("TOTAL USERS");
        jPanel8.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, -1, -1));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/teamwork.png"))); // NOI18N
        jPanel8.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblTotalUsers.setText("{}");
        jPanel8.add(lblTotalUsers, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, -1, -1));

        add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 40, 260, 80));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel16.setText("TOTAL CATEGORIES");
        jPanel9.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, -1, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sulibagakent/Icons/categories (1).png"))); // NOI18N
        jPanel9.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblTotalCategories.setText("{}");
        jPanel9.add(lblTotalCategories, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 50, -1, -1));

        add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1310, 40, 260, 80));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblLowStockItems;
    private javax.swing.JLabel lblTotalCategories;
    private javax.swing.JLabel lblTotalProducts;
    private javax.swing.JLabel lblTotalSalesToday;
    private javax.swing.JLabel lblTotalSuppliers;
    private javax.swing.JLabel lblTotalUsers;
    // End of variables declaration//GEN-END:variables
}
