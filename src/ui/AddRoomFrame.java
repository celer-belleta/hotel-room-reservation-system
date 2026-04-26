package ui;

import dao.RoomDB;
import javax.swing.*;
import java.awt.*;

public class AddRoomFrame extends JFrame {

    private JTextField txtRoomNumber, txtPrice, txtAmenities;
    private JComboBox<String> cbType;
    private RoomDB roomDB;
    private RoomManagementFrame parent;

    public AddRoomFrame(RoomManagementFrame parent) {
        this.parent = parent;
        roomDB = new RoomDB();

        // FRAME SETTINGS
        setTitle("Add New Room");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 15, 15));

        add(new JLabel(" Room Number:"));
        txtRoomNumber = new JTextField();
        add(txtRoomNumber);

        add(new JLabel(" Room Type:"));
        String[] types = {"Superior", "Deluxe", "Family", "Suite"};
        cbType = new JComboBox<>(types);
        add(cbType);

        add(new JLabel(" Price per Night:"));
        txtPrice = new JTextField();
        add(txtPrice);

        add(new JLabel(" Amenities (e.g. WiFi, AC, TV):"));
        txtAmenities = new JTextField();
        add(txtAmenities);

        JButton saveBtn = new JButton("CONFIRM");
        JButton cancelBtn = new JButton("CANCEL");

        add(saveBtn);
        add(cancelBtn);

        // SAVE ROOM
        saveBtn.addActionListener(e -> {
            try {
                String num = txtRoomNumber.getText().trim();
                String type = cbType.getSelectedItem().toString();
                double price = Double.parseDouble(txtPrice.getText().trim());
                String amenities = txtAmenities.getText().trim();

                if (num.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a room number.");
                    return;
                }

                if (roomDB.addRoom(num, type, price, amenities)) {
                    JOptionPane.showMessageDialog(this, "Room " + num + " added successfully!");

                    if (parent != null) {
                        parent.loadRooms(); // Refresh the table
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Could not save room to database.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the price.");
            }
        });

        cancelBtn.addActionListener(e -> dispose());
    }
}