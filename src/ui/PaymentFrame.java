package ui;

import dao.PaymentDB;
import dao.RoomDB;
import model.Reservation;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class PaymentFrame extends JFrame {
    private JLabel lblTotal, lblGuest, lblRoom;
    private JComboBox<String> comboMethod, comboType;
    private PaymentDB payDB = new PaymentDB();
    private double calculatedTotal = 0;
    private int currentResId;

    public PaymentFrame(int resId, String guestName, String roomNum, double roomPrice, Date start, Date end, String pkg) {
        this.currentResId = resId;
        setTitle("Payment Processing - Invoice #" + (int)(Math.random() * 10000));
        setSize(400, 500);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 10, 10));

        // Calculate the money first
        double packageFee = new dao.PackageDB().getPackagePrice(currentResId);
        calculatedTotal = payDB.calculateTotal(start, end, roomPrice) + packageFee;

        // Components
        add(new JLabel("Guest:"));
        add(new JLabel(guestName));

        add(new JLabel("Room:"));
        add(new JLabel(roomNum + " (" + pkg + ")"));

        add(new JLabel("Total Amount Due:"));
        lblTotal = new JLabel("PHP " + calculatedTotal);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(Color.RED);
        add(lblTotal);

        add(new JLabel("Payment Method:"));
        comboMethod = new JComboBox<>(new String[]{"Cash", "Card"});
        add(comboMethod);

        add(new JLabel("Payment Type:"));
        comboType = new JComboBox<>(new String[]{"Full Payment", "Down Payment"});
        add(comboType);

        JButton btnPay = new JButton("Process Payment & Print Receipt");
        btnPay.setBackground(new Color(0, 153, 0));
        btnPay.setForeground(Color.WHITE);

        add(new JLabel()); // Spacer
        add(btnPay);

        btnPay.addActionListener(e -> handlePayment());
    }

    private void handlePayment() {
        JTextField promoField = new JTextField();
        JCheckBox seniorCheckBox = new JCheckBox("Is Senior Citizen or PWD? (20% Off)");

        Object[] discountPanel = {
                "Enter Promo Code (Optional):", promoField,
                seniorCheckBox
        };

        int option = JOptionPane.showConfirmDialog(this, discountPanel, "Discounts & Promos", JOptionPane.OK_CANCEL_OPTION);

        double finalDiscount = 0;
        if (option == JOptionPane.OK_OPTION) {
            String promoCode = promoField.getText().trim();
            boolean isSenior = seniorCheckBox.isSelected();

            finalDiscount = payDB.calculateFinalDiscount(calculatedTotal, promoCode, isSenior);
        }

        double finalTotalToPay = calculatedTotal - finalDiscount;
        String method = (String) comboMethod.getSelectedItem();
        String type = (String) comboType.getSelectedItem();
        String invoice = "INV-" + (int)(Math.random() * 9999);

        boolean success = payDB.processPayment(currentResId, finalTotalToPay, calculatedTotal, finalDiscount, method, type, invoice);

        if (success) {
            // Simulated Receipt Generation
            String receipt = "---------- HOTEL RECEIPT ----------\n" +
                    "Invoice: " + invoice + "\n" +
                    "Original Price: PHP " + calculatedTotal + "\n" +
                    "Discount Applied: PHP " + finalDiscount + "\n" +
                    "TOTAL PAID: PHP " + finalTotalToPay + "\n" +
                    "Method: " + method + "\n" +
                    "Type: " + type + "\n" +
                    "-----------------------------------";

            JOptionPane.showMessageDialog(this, receipt, "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Payment Failed. Check Database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}