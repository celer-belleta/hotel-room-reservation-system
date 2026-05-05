package dao;

import db.DBConnection;
import model.Package;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// PackageDB handles...
//

public class PackageDB {

    public ArrayList<Package> getAllPackages() {
        ArrayList<Package> list = new ArrayList<>();
        String sql = "SELECT * FROM packages";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Package(
                        rs.getInt("package_id"),
                        rs.getString("package_name"),
                        rs.getDouble("price"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getPackagePrice(int packageId) {
        String sql = "SELECT price FROM packages WHERE package_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, packageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Map<String, Integer> getPackageMap() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT package_id, package_name FROM packages";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                map.put(rs.getString("package_name"), rs.getInt("package_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getPackageName(int packageId) {
        String sql = "SELECT package_name FROM packages WHERE package_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, packageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("package_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Room Only";
    }

    public boolean updatePackage(int id, String name, double price, String desc) {
        String sql = "UPDATE packages SET package_name = ?, price = ?, description = ? WHERE package_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, desc);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}