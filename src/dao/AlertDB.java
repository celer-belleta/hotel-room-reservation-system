package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertDB {

    //
    public String getDailySummary() {
        StringBuilder summary = new StringBuilder("--- DAILY SYSTEM ALERTS ---\n\n");
        boolean hasAlerts = false;

        // Check for Check-outs
        List<String> outs = getCheckOutAlerts();
        if (!outs.isEmpty()) {
            summary.append("GUESTS CHECKING OUT TODAY:\n");
            for (String s : outs) summary.append("- ").append(s).append("\n");
            hasAlerts = true;
        }

        summary.append("\n");

        // Check for Maintenance
        List<String> repairs = getMaintenanceAlerts();
        if (!repairs.isEmpty()) {
            summary.append("ROOMS IN MAINTENANCE:\n");
            for (String r : repairs) summary.append("- ").append(r).append("\n");
            hasAlerts = true;
        }

        return hasAlerts ? summary.toString() : "No urgent alerts for today!";
    }

    // GET CHECK-OUT ALERTS (Who is leaving today?)
    public List<String> getCheckOutAlerts() {
        List<String> alerts = new ArrayList<>();

        String sql = "SELECT g.first_name, g.last_name, res.room_id FROM reservations res " +
                "JOIN guests g ON res.guest_id = g.guest_id " +
                "WHERE res.check_out = CURDATE()";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                alerts.add("CHECK-OUT: " + fullName + " (Room " + rs.getInt("room_id") + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alerts;
    }

    // GET MAINTENANCE ALERTS (Which rooms are broken?)
    public List<String> getMaintenanceAlerts() {
        List<String> alerts = new ArrayList<>();

        String sql = "SELECT id FROM rooms WHERE status = 'Maintenance'";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                alerts.add("MAINTENANCE: Room " + rs.getInt("id") + " needs repair.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alerts;
    }
}