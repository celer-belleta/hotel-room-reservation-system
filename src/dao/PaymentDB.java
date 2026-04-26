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

    // Save the payment to the database
    public boolean processPayment(int resId, double amountPaid, double totalDue, double discount, String method, String type, String invoice) {
        String sql = "INSERT INTO payments (res_id, amount_paid, total_amount_due, discount_amount, payment_method, payment_type, invoice_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resId);
            ps.setDouble(2, amountPaid); // This is the Downpayment or Full payment
            ps.setDouble(3, totalDue);   // The original price after promo/discount
            ps.setDouble(4, discount);   // The amount saved
            ps.setString(5, method);     // Cash, Card, etc.
            ps.setString(6, type);       // "Downpayment" or "Full"
            ps.setString(7, invoice);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // REVENUE REPORT
    public double getTotalRevenue(int daysBack) {
        double total = 0;

        // SQL: "Add up all amount_paid where the date is within the last X days"
        String sql = "SELECT SUM(amount_paid) as total FROM payments " +
                "WHERE payment_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)";

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, daysBack); // Set how many days to look back (7 for week, 30 for month)
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    // Promo Codes and Senior/PWD Discounts
    public double calculateFinalDiscount(double originalTotal, String promoCode, boolean isSeniorOrPWD) {
        double discountAmount = 0.0;

        // PROMO CODE
        if (promoCode.equalsIgnoreCase("MAAYO2026")) {
            discountAmount = originalTotal * 0.10;
        }
        // SENIOR/PWD DISCOUNT
        else if (isSeniorOrPWD) {
            discountAmount = originalTotal * 0.20;
        }

        return discountAmount;
    }
}