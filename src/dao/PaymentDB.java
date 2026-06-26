package dao;

import db.DBConnection;
import java.sql.*;

// PaymentDB handles...
//

public class PaymentDB {

    // Calculate Total Cost: (Nights * Room Price)
    public double calculateTotal(Date checkIn, Date checkOut, double roomPrice) {
        long difference = checkOut.getTime() - checkIn.getTime();
        int nights = (int) (difference / (1000 * 60 * 60 * 24));

        // If they check out the same day, charge at least 1 night
        if (nights <= 0) nights = 1;

        return nights * roomPrice;
    }

    public boolean processPayment(int resId, double amountPaid, double totalDue, double discount, String method, String type, String invoice) {
        String sqlInsert = "INSERT INTO payments (res_id, amount_paid, total_amount_due, discount_amount, payment_method, payment_type, invoice_number) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlUpdate = "UPDATE reservations SET amount_paid = amount_paid + ?, remaining_balance = remaining_balance - (? + ?) WHERE res_id = ?";

        try (Connection conn = db.DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction for data safety

            try (PreparedStatement ps1 = conn.prepareStatement(sqlInsert)) {
                ps1.setInt(1, resId);
                ps1.setDouble(2, amountPaid);
                ps1.setDouble(3, totalDue);
                ps1.setDouble(4, discount);
                ps1.setString(5, method);
                ps1.setString(6, type);
                ps1.setString(7, invoice);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdate)) {
                ps2.setDouble(1, amountPaid); // Add to the 'amount_paid' running total
                ps2.setDouble(2, amountPaid); // Deduct cash from 'remaining_balance'
                ps2.setDouble(3, discount);   // Deduct discount from 'remaining_balance'
                ps2.setInt(4, resId);
                ps2.executeUpdate();
            }

            conn.commit(); // Finalize changes[cite: 12]
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // REVENUE REPORT
    public double getTotalRevenue(int days) {
        double total = 0;
        String sql = "SELECT SUM(amount_paid) FROM payments " +
                "WHERE payment_date >= CURDATE() - INTERVAL ? DAY";

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, days);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    // Promo Codes and Senior/PWD Discounts
    public double calculateFinalDiscount(double originalTotal, String promoCode, boolean isSeniorOrPWD) {
        double discountAmount = 0.0;

        // PROMO CODE: CTU2026 for a 15% discount
        if (promoCode.equalsIgnoreCase("CTU2026")) {
            discountAmount = originalTotal * 0.15;
        }
        // AURELIA10 - 10%
        else if (promoCode.equalsIgnoreCase("AURELIA10")) {
            discountAmount = originalTotal * 0.10;
        }
        // SENIOR/PWD DISCOUNT
        else if (isSeniorOrPWD) {
            discountAmount = originalTotal * 0.20;
        }

        return discountAmount;
    }

    public String getDownpaymentDate(int resId) {
        String sql = "SELECT created_at FROM reservations WHERE res_id = ? LIMIT 1";
        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(ts);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }}