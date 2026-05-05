package ui;

import dao.RoomDB;
import javax.swing.*;
import java.awt.*;

public class AddRoomFrame extends JFrame {

    private JTextField roomNumberField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> packageComboBox;
    private JComboBox<Integer> paxComboBox;
    private JTextField priceField;
    private RoomTablePanel parentPanel;

    public AddRoomFrame(RoomTablePanel parent) {
        this.parentPanel = parent;

        setTitle("Aurelia Grand - Add New Room");
        setSize(450, 450);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 15, 15));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        getContentPane().setBackground(Color.WHITE);

        add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        add(roomNumberField);

        add(new JLabel("Room Category:"));
        String[] types = {"Standard Room", "Deluxe Room", "Family Room", "Specialty Room"};
        typeComboBox = new JComboBox<>(types);
        add(typeComboBox);

        add(new JLabel("Package Type:"));
        String[] packages = {"Room Only", "Room + Breakfast", "Room + Amenities"};
        packageComboBox = new JComboBox<>(packages);
        add(packageComboBox);

        add(new JLabel("Max Pax (Capacity):"));
        Integer[] paxOptions = {1, 2, 3, 4, 5, 6};
        paxComboBox = new JComboBox<>(paxOptions);
        add(paxComboBox);

        add(new JLabel("Base Price (₱):"));
        priceField = new JTextField();
        add(priceField);

        JButton addBtn = new JButton("Confirm");
        JButton cancelBtn = new JButton("Cancel");

        addBtn.setForeground(Color.BLACK);
        addBtn.setFocusPainted(false);

        addBtn.addActionListener(e -> addRoom());
        cancelBtn.addActionListener(e -> dispose());

        add(addBtn);
        add(cancelBtn);

        setVisible(true);
    }

    private void addRoom() {
        String roomNumber = roomNumberField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String packageType = (String) packageComboBox.getSelectedItem();
        int maxPax = (int) paxComboBox.getSelectedItem(); // Get selected pax
        String priceStr = priceField.getText().trim();

        if (roomNumber.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            String highlights = getSpecsForType(type);

            RoomDB db = new RoomDB();
            if (db.addRoom(roomNumber, type, price, highlights, packageType, maxPax)) {
                JOptionPane.showMessageDialog(this, "Room added successfully!");
                if (parentPanel != null) {
                    parentPanel.loadRooms();
                }
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.");
        }
    }

    private String getSpecsForType(String type) {
        switch (type) {
            case "Standard Room": return "• 1 Queen Bed, • Hot and Cold Shower, • LED TV & High-Speed Wi-Fi";
            case "Deluxe Room": return "• 1 King Bed, • City View, • Mini-Fridge, • LED TV & High-Speed Wi-Fi";
            case "Family Room": return "• 2 Queen Beds, • Pantry Area with Microwave, • Mini-Bar, • LED TV & High-Speed Wi-Fi";
            case "Specialty Room": return "• 1 California King Bed, • Small Balcony, • Bathtub, • LED TV & High-Speed Wi-Fi";
            default: return "";
        }
    }
}