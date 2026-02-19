/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    // EDIT THIS: use your own connection method if you already have one
    private static Connection getConn() throws SQLException {
        // Example (change db, user, pass)
        String url = "jdbc:mysql://localhost:3307/pos_db?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pass = "";
        return DriverManager.getConnection(url, user, pass);
    }

    public static void addStockEntry(
            String barcode, String productId, String productName, String category, String supplierName,
            String batchNo, String mfgDate, String expDate, int qtyAdded, double unitCost, double sellingPrice,
            String storageLoc, String stockStatus,
            String dateStocked, String stockedBy, String remarks
    ) throws SQLException {

        String sql = """
    INSERT INTO inventory
    (barcode, product_id, product_name, category, supplier_name,
     batch_no, manufacturing_date, expiration_date, qty, unit_cost, selling_price,
     storage_location, stock_status,
     date_stocked, stocked_by, remarks)
    VALUES (?,?,?,?,?,
            ?,?,?,?,?,?,
            ?,?,
            ?,?,?)
""";

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, barcode);
            ps.setString(2, productId);
            ps.setString(3, productName);
            ps.setString(4, category);
            ps.setString(5, supplierName);

            ps.setString(6, batchNo);
            ps.setDate(7, Date.valueOf(mfgDate));       // "YYYY-MM-DD"
            ps.setDate(8, Date.valueOf(expDate));       // "YYYY-MM-DD"
            ps.setInt(9, qtyAdded);
            ps.setDouble(10, unitCost);
            ps.setDouble(11, sellingPrice);

            ps.setString(12, storageLoc);
            ps.setString(13, stockStatus);

            ps.setDate(14, Date.valueOf(dateStocked));  // "YYYY-MM-DD"
            ps.setString(15, stockedBy);
            ps.setString(16, remarks);

            ps.executeUpdate();
        }
    }

    // One query, used to fill ALL 3 tables
    public static List<Object[]> fetchAllRows() throws SQLException {
String sql = """
    SELECT barcode, product_id, product_name, category, supplier_name,
           batch_no, manufacturing_date, expiration_date, qty, unit_cost, selling_price,
           storage_location, stock_status,
           date_stocked, stocked_by, remarks
    FROM inventory
    ORDER BY id DESC
""";


        List<Object[]> rows = new ArrayList<>();

        try (Connection con = getConn();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] r = new Object[16];
                for (int i = 0; i < 16; i++) r[i] = rs.getObject(i + 1);
                rows.add(r);
            }
        }
        return rows;
    }
}
