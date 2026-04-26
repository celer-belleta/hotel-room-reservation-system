package dao;

import db.DBConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

// PackageDB handles...
//

public class PackageDB {

    // This method retrieves the price of a package based on its ID.
    public double getPackagePrice(int packageId) {
        String sql = "SELECT price FROM packages WHERE package_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, packageId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("price");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // This method retrieves a map of package names to their corresponding IDs.
    public Map<String, Integer> getPackageMap() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT package_id, package_name FROM packages";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString("package_name"), rs.getInt("package_id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // This method retrieves the name of a package based on its ID.
    public String getPackageName(int packageId) {
        String sql = "SELECT package_name FROM packages WHERE package_id = ?";
        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, packageId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("package_name");
        } catch (Exception e) { e.printStackTrace(); }
        return "Room Only"; // Default if not found
    }
}