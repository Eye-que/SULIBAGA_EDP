package DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ----------------------------
    // 1) CHECK IF USERNAME EXISTS
    // ----------------------------
    public static boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("usernameExists error: " + e.getMessage(), e);
        }
    }

    // ----------------------------
    // 2) ADD USER (NO EMAIL/CONTACT)
    // ----------------------------
    public static boolean addUser(String role,
                                  String firstName,
                                  String middleName,
                                  String lastName,
                                  String username,
                                  String password) {

        String sql = "INSERT INTO users " +
                     "(role, first_name, middle_name, last_name, username, password, status) " +
                     "VALUES (?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role);
            ps.setString(2, firstName);
            ps.setString(3, middleName);      // can be null/empty
            ps.setString(4, lastName);
            ps.setString(5, username);
            ps.setString(6, password);        // NOTE: hash this in real systems
            ps.setString(7, "Active");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("addUser error: " + e.getMessage(), e);
        }
    }

    // ----------------------------
    // 3) DELETE BY USERNAME
    // ----------------------------
    public static boolean deleteByUsername(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("deleteByUsername error: " + e.getMessage(), e);
        }
    }

    // ----------------------------
    // 4) GET ALL USERS FOR JTable
    // Columns returned:
    // role, first_name, middle_name, last_name, status, username
    // ----------------------------
    public static List<Object[]> getAllUsersForTable() {
        String sql = "SELECT role, first_name, middle_name, last_name, status, username " +
                     "FROM users ORDER BY user_id DESC";

        List<Object[]> rows = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getString("role"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("status"),
                        rs.getString("username")
                });
            }

        } catch (SQLException e) {
            throw new RuntimeException("getAllUsersForTable error: " + e.getMessage(), e);
        }

        return rows;
    }

    // ----------------------------
    // 5) UPDATE STATUS BY USERNAME
    // ----------------------------
    public static boolean updateStatusByUsername(String username, String newStatus) {
        String sql = "UPDATE users SET status = ? WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setString(2, username);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("updateStatusByUsername error: " + e.getMessage(), e);
        }
    }

    // ----------------------------
    // 6) UPDATE USER BY USERNAME
    // (role, names, status only)
    // ----------------------------
    public static boolean updateUserByUsername(String username,
                                               String role,
                                               String firstName,
                                               String middleName,
                                               String lastName,
                                               String status) {

        String sql = "UPDATE users SET role=?, first_name=?, middle_name=?, last_name=?, status=? " +
                     "WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role);
            ps.setString(2, firstName);
            ps.setString(3, middleName);
            ps.setString(4, lastName);
            ps.setString(5, status);
            ps.setString(6, username);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("updateUserByUsername error: " + e.getMessage(), e);
        }
    }

    // ----------------------------
    // 7) GET FIRST NAME BY USERNAME
    // ----------------------------
    public static String getFirstNameByUsername(String username) {
        String sql = "SELECT first_name FROM users WHERE username = ? LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("first_name");
            }

        } catch (SQLException e) {
            throw new RuntimeException("getFirstNameByUsername error: " + e.getMessage(), e);
        }

        return "";
    }
}