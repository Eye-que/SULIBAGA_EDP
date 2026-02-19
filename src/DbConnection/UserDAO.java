package DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addUser(String role,
                               String firstName,
                               String lastName,
                               String username,
                               String password,
                               String email,
                               String contactNumber) {

        String sql = "INSERT INTO users(role, first_name, last_name, username, password, email, contact_number, status) " +
                     "VALUES (?,?,?,?,?,?,?, 'Active')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, username);
            ps.setString(5, password); // demo only
            ps.setString(6, email);
            ps.setString(7, contactNumber);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ Use only ONE delete method (returns true if deleted)
    public static boolean deleteByUsername(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0; // true if a row was deleted
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Object[]> getAllUsersForTable() {
        String sql = "SELECT role, first_name, last_name, email, contact_number, status, username " +
                     "FROM users ORDER BY user_id DESC";

        List<Object[]> rows = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getString("role"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("contact_number"),
                    rs.getString("status"),
                    rs.getString("username")
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return rows;
    }

    public static boolean updateStatusByUsername(String username, String newStatus) {
        String sql = "UPDATE users SET status = ? WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ✅ For UPDATE button (edit details)
    public static boolean updateUserByUsername(String username,
                                               String role,
                                               String firstName,
                                               String lastName,
                                               String email,
                                               String contactNumber,
                                               String status) {

        String sql = "UPDATE users SET role=?, first_name=?, last_name=?, email=?, contact_number=?, status=? " +
                     "WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, email);
            ps.setString(5, contactNumber);
            ps.setString(6, status);
            ps.setString(7, username);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static String getFirstNameByUsername(String username) {
    String sql = "SELECT first_name FROM users WHERE username = ?";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, username);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("first_name");
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    return "";
}

}
