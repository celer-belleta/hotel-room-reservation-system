package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDB {

    // GET CHECK-OUT ALERTS (Who is leaving today?)
    public List<String> getCheckOutAlerts() {
        List<String> alerts = new ArrayList<>();

        // SQL: Join Guest and Reservation to find anyone checking out TODAY
        String sql = "SELECT g.name, res.room_id FROM reservations res " +
                "JOIN guests g ON res.guest_id = g.guest_id " +
                "WHERE res.check_out_date = CURDATE()";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                // Add a simple message to our list
                alerts.add("CHECK-OUT: " + rs.getString("name") + " (Room " + rs.getInt("room_id") + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alerts;
    }

    // GET MAINTENANCE ALERTS (Which rooms are broken?)
    public List<String> getMaintenanceAlerts() {
        List<String> alerts = new ArrayList<>();

        // SQL: Find any room where the status is 'Maintenance'
        String sql = "SELECT room_id FROM rooms WHERE status = 'Maintenance'";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                alerts.add("MAINTENANCE: Room " + rs.getInt("room_id") + " needs repair.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alerts;
    }
}