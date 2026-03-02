package DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public static class Category {
        public int id;
        public String name;
        public String description;

        public Category(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
    }

    // ✅ CREATE
    public static void addCategory(String name, String description) throws SQLException {
        String sql = "INSERT INTO categories(name, description) VALUES(?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.executeUpdate();
        }
    }

    // ✅ READ (All)
    public static List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT category_id, name, description FROM categories ORDER BY category_id DESC";
        List<Category> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("description")
                ));
            }
        }
        return list;
    }

    // ✅ UPDATE
    public static void updateCategory(int id, String name, String description) throws SQLException {
        String sql = "UPDATE categories SET name = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE category_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    // ✅ DELETE
    public static void deleteCategory(int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}