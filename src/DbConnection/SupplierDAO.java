package DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public static class Supplier {
        public int id;
        public String name;
        public String contactPerson;
        public String contactNumber;
        public String email;
        public String address;

        public Supplier(int id, String name, String contactPerson, String contactNumber, String email, String address) {
            this.id = id;
            this.name = name;
            this.contactPerson = contactPerson;
            this.contactNumber = contactNumber;
            this.email = email;
            this.address = address;
        }
    }

    public static List<Supplier> getAll() throws SQLException {
        String sql = "SELECT supplier_id, name, contact_person, contact_number, email, address " +
                     "FROM suppliers ORDER BY supplier_id DESC";

        List<Supplier> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_person"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getString("address")
                ));
            }
        }
        return list;
    }

    public static void add(String name, String contactPerson, String contactNumber, String email, String address) throws SQLException {
        String sql = "INSERT INTO suppliers (name, contact_person, contact_number, email, address) VALUES (?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contactPerson);
            ps.setString(3, contactNumber);
            ps.setString(4, email);
            ps.setString(5, address);
            ps.executeUpdate();
        }
    }

    public static void update(int supplierId, String name, String contactPerson, String contactNumber, String email, String address) throws SQLException {
        String sql = "UPDATE suppliers SET name=?, contact_person=?, contact_number=?, email=?, address=? WHERE supplier_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, contactPerson);
            ps.setString(3, contactNumber);
            ps.setString(4, email);
            ps.setString(5, address);
            ps.setInt(6, supplierId);
            ps.executeUpdate();
        }
    }

    public static void delete(int supplierId) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, supplierId);
            ps.executeUpdate();
        }
    }
}