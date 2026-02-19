package DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static Connection getConn() throws SQLException {
        String url = "jdbc:mysql://localhost:3307/pos_db?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pass = "";
        return DriverManager.getConnection(url, user, pass);
    }

    // ✅ INSERT (matches your table columns)
    public static void insertProduct(
            String barcode,
            String productID,
            String productName,
            String category,
            String description,
            double costPrice,
            double sellingPrice,
            int stockQty,
            int reorderLevel,
            String supplierName,
            String unitMeasure,
            String status
    ) throws SQLException {

        String sql = """
            INSERT INTO products
            (barcode, product_id, product_name, category, description,
             cost_price, unit_price, stock_quantity, reorder_level,
             supplier_name, unit_of_measure, status)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
        """;

        try (Connection con = getConn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, barcode);
            ps.setString(2, productID);
            ps.setString(3, productName);
            ps.setString(4, category);
            ps.setString(5, description);
            ps.setDouble(6, costPrice);
            ps.setDouble(7, sellingPrice);
            ps.setInt(8, stockQty);
            ps.setInt(9, reorderLevel);
            ps.setString(10, supplierName);
            ps.setString(11, unitMeasure);
            ps.setString(12, status);
            ps.executeUpdate();
        }
    }

    // ✅ UPDATE (matches your table columns)
    public static void updateProduct(
            String barcode,
            String productID,
            String productName,
            String category,
            String description,
            double costPrice,
            double sellingPrice,
            int stockQty,
            int reorderLevel,
            String supplierName,
            String unitMeasure,
            String status
    ) throws SQLException {

        String sql = """
            UPDATE products SET
              barcode=?,
              product_name=?,
              category=?,
              description=?,
              cost_price=?,
              unit_price=?,
              stock_quantity=?,
              reorder_level=?,
              supplier_name=?,
              unit_of_measure=?,
              status=?
            WHERE product_id=?
        """;

        try (Connection con = getConn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, barcode);
            ps.setString(2, productName);
            ps.setString(3, category);
            ps.setString(4, description);
            ps.setDouble(5, costPrice);
            ps.setDouble(6, sellingPrice);
            ps.setInt(7, stockQty);
            ps.setInt(8, reorderLevel);
            ps.setString(9, supplierName);
            ps.setString(10, unitMeasure);
            ps.setString(11, status);
            ps.setString(12, productID);
            ps.executeUpdate();
        }
    }

    // ✅ FETCH ALL (order matches your JTable: Barcode, Product ID, Name, Category, ...)
    public static List<Object[]> fetchAll() throws SQLException {

        String sql = """
            SELECT barcode, product_id, product_name, category, description,
                   cost_price, unit_price, stock_quantity, reorder_level,
                   supplier_name, unit_of_measure, status
            FROM products
            ORDER BY date_added DESC
        """;

        List<Object[]> rows = new ArrayList<>();

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] r = new Object[12];
                for (int i = 0; i < 12; i++) r[i] = rs.getObject(i + 1);
                rows.add(r);
            }
        }
        return rows;
    }
}
