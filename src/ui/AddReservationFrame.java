package ui;

import dao.ReservationDB;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class AddReservationFrame extends JFrame {
    private JTextField guestIdField, roomIdField, checkInField, checkOutField;
    private ReservationDB resDB = new ReservationDB();

    public AddReservationFrame(ReservationManagementFrame parent) {
        setTitle("New Booking");
        setSize(350, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        // UI Components
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

        JButton saveBtn = new JButton("Confirm Booking");
        saveBtn.addActionListener(e -> bookRoom(parent));

        add(new JLabel()); // Spacer
        add(saveBtn);
    }

    private void bookRoom(ReservationManagementFrame parent) {
        try {
            int gId = Integer.parseInt(guestIdField.getText());
            int rId = Integer.parseInt(roomIdField.getText());

            // Converting String text to SQL Date
            Date start = Date.valueOf(checkInField.getText());
            Date end = Date.valueOf(checkOutField.getText());

            boolean success = resDB.createReservation(gId, rId, start, end);

            if (success) {
                JOptionPane.showMessageDialog(this, "Booking Confirmed!");
                parent.refreshTable();
                dispose();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format! Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please check your inputs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}