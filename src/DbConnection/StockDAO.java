package DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    private static Connection getConn() throws SQLException {
        String url = "jdbc:mysql://localhost:3307/pos_db?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pass = "";
        return DriverManager.getConnection(url, user, pass);
    }

    // ============================
    // STOCK IN (adds to product stock + logs transaction)
    // ============================
    public static boolean stockIn(int productId, int qty, String referenceNo, String remarks) throws SQLException {
        if (qty <= 0) throw new SQLException("Quantity must be greater than 0.");

        String insertTx = """
            INSERT INTO inventory_transactions
            (product_id, transaction_type, quantity, reference_number, reason)
            VALUES (?, 'Stock In', ?, ?, ?)
        """;

        String updateStock = """
            UPDATE products
            SET stock_quantity = stock_quantity + ?
            WHERE product_id = ?
        """;

        try (Connection con = getConn()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(insertTx);
                 PreparedStatement ps2 = con.prepareStatement(updateStock)) {

                // log transaction
                ps1.setInt(1, productId);
                ps1.setInt(2, qty);
                ps1.setString(3, referenceNo);
                ps1.setString(4, remarks);
                ps1.executeUpdate();

                // update stock
                ps2.setInt(1, qty);
                ps2.setInt(2, productId);
                int affected = ps2.executeUpdate();

                if (affected == 0) throw new SQLException("Product not found.");

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // ============================
    // STOCK OUT (deducts product stock + logs transaction)
    // ============================
    public static boolean stockOut(int productId, int qty, String referenceNo, String reason) throws SQLException {
        if (qty <= 0) throw new SQLException("Quantity must be greater than 0.");

        String checkStock = "SELECT stock_quantity FROM products WHERE product_id = ?";
        String insertTx = """
            INSERT INTO inventory_transactions
            (product_id, transaction_type, quantity, reference_number, reason)
            VALUES (?, 'Stock Out', ?, ?, ?)
        """;
        String updateStock = """
            UPDATE products
            SET stock_quantity = stock_quantity - ?
            WHERE product_id = ?
        """;

        try (Connection con = getConn()) {
            con.setAutoCommit(false);

            try {
                // check current stock
                int currentStock = 0;
                try (PreparedStatement ps = con.prepareStatement(checkStock)) {
                    ps.setInt(1, productId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Product not found.");
                        currentStock = rs.getInt("stock_quantity");
                    }
                }

                if (currentStock < qty) {
                    throw new SQLException("Insufficient stock. Current stock: " + currentStock);
                }

                // log transaction
                try (PreparedStatement ps = con.prepareStatement(insertTx)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, qty);
                    ps.setString(3, referenceNo);
                    ps.setString(4, reason);
                    ps.executeUpdate();
                }

                // update stock
                try (PreparedStatement ps = con.prepareStatement(updateStock)) {
                    ps.setInt(1, qty);
                    ps.setInt(2, productId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // ============================
    // INVENTORY TRANSACTION HISTORY
    // for JTable:
    // transaction_id, product_id, barcode, product_name, type, qty, reference, reason, date
    // ============================
    public static List<Object[]> fetchInventoryTransactions() throws SQLException {
        String sql = """
            SELECT
              it.transaction_id,
              p.product_id,
              p.barcode,
              p.name AS product_name,
              it.transaction_type,
              it.quantity,
              it.reference_number,
              it.reason,
              it.transaction_date
            FROM inventory_transactions it
            JOIN products p ON it.product_id = p.product_id
            ORDER BY it.transaction_id DESC
        """;

        List<Object[]> rows = new ArrayList<>();

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("transaction_id"),
                        rs.getInt("product_id"),
                        rs.getString("barcode"),
                        rs.getString("product_name"),
                        rs.getString("transaction_type"),
                        rs.getInt("quantity"),
                        rs.getString("reference_number"),
                        rs.getString("reason"),
                        rs.getTimestamp("transaction_date")
                });
            }
        }

        return rows;
    }

    // ============================
    // STOCK MONITORING (Current stock)
    // for JTable:
    // product_id, barcode, name, category, supplier, stock_qty, reorder_level
    // ============================
    public static List<Object[]> fetchStockMonitoring() throws SQLException {
        String sql = """
            SELECT
              p.product_id,
              p.barcode,
              p.name,
              c.name AS category_name,
              s.name AS supplier_name,
              p.stock_quantity,
              p.reorder_level
            FROM products p
            JOIN categories c ON p.category_id = c.category_id
            JOIN suppliers s ON p.supplier_id = s.supplier_id
            ORDER BY p.name ASC
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
                        rs.getInt("stock_quantity"),
                        rs.getInt("reorder_level")
                });
            }
        }

        return rows;
    }
}