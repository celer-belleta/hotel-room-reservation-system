package ui;

import dao.ReservationDB;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class AddReservationFrame extends JFrame {
    private JTextField guestIdField, roomIdField, checkInField, checkOutField;
    private JComboBox<String> packageCombo;
    private ReservationDB resDB = new ReservationDB();

    public AddReservationFrame(ReservationManagementFrame parent) {
        setTitle("New Booking");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 10, 10));

        // Components
        add(new JLabel("Guest ID:"));
        guestIdField = new JTextField();
        add(guestIdField);

        add(new JLabel("Room ID:"));
        roomIdField = new JTextField();
        add(roomIdField);

        add(new JLabel("Check-In (YYYY-MM-DD):"));
        checkInField = new JTextField();
        add(checkInField);

        add(new JLabel("Check-Out (YYYY-MM-DD):"));
        checkOutField = new JTextField();
        add(checkOutField);

        add(new JLabel("Select Package:"));
        String[] packages = {"Room Only", "Room + Breakfast", "Room + Amenities"};
        packageCombo = new JComboBox<>(packages);
        add(packageCombo);

        JButton saveBtn = new JButton("Confirm Booking");
        saveBtn.addActionListener(e -> bookRoom(parent));

        add(new JLabel());
        add(saveBtn);
    }

    private void bookRoom(ReservationManagementFrame parent) {
        // check if any fields are actually empty
        if (guestIdField.getText().trim().isEmpty() ||
                roomIdField.getText().trim().isEmpty() ||
                checkInField.getText().trim().isEmpty() ||
                checkOutField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int gId = Integer.parseInt(guestIdField.getText().trim());
            int rId = Integer.parseInt(roomIdField.getText().trim());
            Date start = Date.valueOf(checkInField.getText().trim());
            Date end = Date.valueOf(checkOutField.getText().trim());

            String selectedName = (String) packageCombo.getSelectedItem();
            int pkgId = 1;
            if (selectedName.equals("Room + Breakfast")) pkgId = 2;
            else if (selectedName.equals("Room + Amenities")) pkgId = 3;

            int resId = resDB.createReservation(gId, rId, start, end, pkgId);

            if (resId != -1) {
                JOptionPane.showMessageDialog(this, "Booking Confirmed!");
                int choice = JOptionPane.showConfirmDialog(this, "Proceed to Payment?", "Billing", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    double realPackagePrice = new dao.PackageDB().getPackagePrice(pkgId);
                    new PaymentFrame(resId, "Guest #" + gId, "Room #" + rId, realPackagePrice, start, end, selectedName).setVisible(true);
                }

                parent.refreshTable();
                dispose();
            }
        } catch (NumberFormatException ex) {
            // If the user types "ABC" instead of a number
            JOptionPane.showMessageDialog(this, "Guest ID and Room ID must be numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            // If the date format is wrong
            JOptionPane.showMessageDialog(this, "Please use the correct date format: YYYY-MM-DD", "Date Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Any other unexpected database errors
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}