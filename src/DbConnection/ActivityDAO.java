package DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAO {

    // insert a log
    public static void log(String action, String details, String username) throws SQLException {
        String sql = "INSERT INTO activity_logs (action, details, username) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, action);
            ps.setString(2, details);
            ps.setString(3, username);
            ps.executeUpdate();
        }
    }

    // fetch latest logs (example: latest 10)
    public static List<Object[]> fetchLatest(int limit) throws SQLException {
        String sql = "SELECT action, details, username, created_at " +
                     "FROM activity_logs ORDER BY created_at DESC LIMIT ?";
        List<Object[]> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getString("username"),
                        rs.getTimestamp("created_at")
                    });
                }
            }
        }
        return list;
    }
}
