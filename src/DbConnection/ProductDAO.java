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

    // ============================
    // INSERT PRODUCT
    // ============================
    public static boolean insertProduct(
            String barcode,
            String name,
            int categoryId,
            int supplierId,
            String description,
            double costPrice,
            double sellingPrice,
            int stockQty,
            int reorderLevel,
            String productImageBase64 // can be null
    ) throws SQLException {

        String sql = """
            INSERT INTO products
            (barcode, name, category_id, supplier_id, description,
             cost_price, selling_price, stock_quantity, reorder_level, product_image)
            VALUES (?,?,?,?,?,?,?,?,?,?)
        """;

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, barcode);
            ps.setString(2, name);
            ps.setInt(3, categoryId);
            ps.setInt(4, supplierId);
            ps.setString(5, description);
            ps.setDouble(6, costPrice);
            ps.setDouble(7, sellingPrice);
            ps.setInt(8, stockQty);
            ps.setInt(9, reorderLevel);
            ps.setString(10, productImageBase64);

            return ps.executeUpdate() > 0;
        }
    }

    // ============================
    // UPDATE PRODUCT (BY product_id)
    // ============================
    public static boolean updateProduct(
            int productId,
            String barcode,
            String name,
            int categoryId,
            int supplierId,
            String description,
            double costPrice,
            double sellingPrice,
            int stockQty,
            int reorderLevel,
            String productImageBase64 // can be null
    ) throws SQLException {

        String sql = """
            UPDATE products SET
              barcode=?,
              name=?,
              category_id=?,
              supplier_id=?,
              description=?,
              cost_price=?,
              selling_price=?,
              stock_quantity=?,
              reorder_level=?,
              product_image=?
            WHERE product_id=?
        """;

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, barcode);
            ps.setString(2, name);
            ps.setInt(3, categoryId);
            ps.setInt(4, supplierId);
            ps.setString(5, description);
            ps.setDouble(6, costPrice);
            ps.setDouble(7, sellingPrice);
            ps.setInt(8, stockQty);
            ps.setInt(9, reorderLevel);
            ps.setString(10, productImageBase64);
            ps.setInt(11, productId);

            return ps.executeUpdate() > 0;
        }
    }

    // ============================
    // DELETE PRODUCT (BY product_id)
    // ============================
    public static boolean deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id=?";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        }
    }

    // ============================
    // FETCH ALL PRODUCTS (FOR JTable)
    // Returns:
    // product_id, barcode, name, category_name, supplier_name,
    // description, cost_price, selling_price, stock_quantity, reorder_level
    // ============================
    public static List<Object[]> fetchAll() throws SQLException {

        String sql = """
            SELECT
              p.product_id,
              p.barcode,
              p.name,
              c.name AS category_name,
              s.name AS supplier_name,
              p.description,
              p.cost_price,
              p.selling_price,
              p.stock_quantity,
              p.reorder_level
            FROM products p
            JOIN categories c ON p.category_id = c.category_id
            JOIN suppliers s ON p.supplier_id = s.supplier_id
            ORDER BY p.product_id DESC
        """;

        List<Object[]> rows = new ArrayList<>();

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("barcode"),
                        rs.getString("name"),
                        rs.getString("category_name"),
                        rs.getString("supplier_name"),
                        rs.getString("description"),
                        rs.getDouble("cost_price"),
                        rs.getDouble("selling_price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("reorder_level")
                });
            }
        }

        return rows;
    }

    // ============================
    // FETCH CATEGORY LIST (FOR COMBOBOX)
    // returns: [id, name]
    // ============================
    public static List<Object[]> fetchCategories() throws SQLException {
        String sql = "SELECT category_id, name FROM categories ORDER BY name";
        List<Object[]> list = new ArrayList<>();

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("category_id"),
                        rs.getString("name")
                });
            }
        }
        return list;
    }

    // ============================
    // FETCH SUPPLIER LIST (FOR COMBOBOX)
    // returns: [id, name]
    // ============================
    public static List<Object[]> fetchSuppliers() throws SQLException {
        String sql = "SELECT supplier_id, name FROM suppliers ORDER BY name";
        List<Object[]> list = new ArrayList<>();

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("supplier_id"),
                        rs.getString("name")
                });
            }
        }
        return list;
    }
    public static boolean barcodeExists(String barcode) throws SQLException {
    String sql = "SELECT 1 FROM products WHERE barcode = ? LIMIT 1";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, barcode);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}
}