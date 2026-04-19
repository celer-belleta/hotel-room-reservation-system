package ui;

import dao.RoomDB;
import model.Room;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class RoomManagementFrame extends JFrame {
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private RoomDB roomDB;

    private ArrayList<Room> roomList = new ArrayList<>();

    public RoomManagementFrame() {
        roomDB = new RoomDB();

        setTitle("Room Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // TABLE SETUP
        String[] columns = {"ID", "Room #", "Type", "Price", "Status", "Amenities"};
        tableModel = new DefaultTableModel(columns, 0);
        roomTable = new JTable(tableModel);
        loadRooms();

        add(new JScrollPane(roomTable), BorderLayout.CENTER);

        // BOTTOM PANEL (BUTTONS)
        JPanel buttonPanel = new JPanel();

        JButton addBtn = new JButton("Add New Room");
        JButton editBtn = new JButton("Edit Selected Room");
        JButton deleteBtn = new JButton("Delete Room");

        // SYMBOL REFRESH BUTTON
        JButton refreshBtn = new JButton("\u21BB");
        refreshBtn.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        refreshBtn.setToolTipText("Refresh Table");

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // BUTTON ACTIONS
        refreshBtn.addActionListener(e -> loadRooms());

        addBtn.addActionListener(e -> new AddRoomFrame(this).setVisible(true));

        editBtn.addActionListener(e -> {
            int row = roomTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a room to edit!");
                return;
            }

            int id = (int) tableModel.getValueAt(row, 0);
            String roomNum = (String) tableModel.getValueAt(row, 1);
            String type = (String) tableModel.getValueAt(row, 2);
            double price = (double) tableModel.getValueAt(row, 3);
            String amenities = (String) tableModel.getValueAt(row, 5);

            // Create Fields for the Popup
            JTextField txtNum = new JTextField(roomNum);
            JTextField txtPrice = new JTextField(String.valueOf(price));
            JTextField txtAmenities = new JTextField(amenities);
            String[] types = {"Superior", "Deluxe", "Family", "Suite"};
            JComboBox<String> cbType = new JComboBox<>(types);
            cbType.setSelectedItem(type);

            Object[] message = {
                    "Room Number:", txtNum,
                    "Room Type:", cbType,
                    "Price:", txtPrice,
                    "Amenities:", txtAmenities
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Room Details", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    String newNum = txtNum.getText().trim();
                    String newType = cbType.getSelectedItem().toString();
                    double newPrice = Double.parseDouble(txtPrice.getText().trim());
                    String newAmen = txtAmenities.getText().trim();

                    if (roomDB.updateRoom(id, newNum, newType, newPrice, newAmen)) {

                        // update row
                        tableModel.setValueAt(newNum, row, 1);
                        tableModel.setValueAt(newType, row, 2);
                        tableModel.setValueAt(newPrice, row, 3);
                        tableModel.setValueAt(newAmen, row, 5);

                        JOptionPane.showMessageDialog(this, "Room " + id + " updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error updating database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid price.");
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = roomTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a room to delete first!");
                return;
            }

            String status = (String) tableModel.getValueAt(row, 4); // Status column
            if (status.equalsIgnoreCase("Occupied")) {
                JOptionPane.showMessageDialog(this, "Cannot delete an occupied room!");
                return;
            }

            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete Room ID: " + id + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                if (roomDB.deleteRoom(id)) {

                    tableModel.removeRow(row);
                    JOptionPane.showMessageDialog(this, "Room deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Could not delete room from database.");
                }
            }
        });
    }

    // LOAD ROOMS
    public void loadRooms() {
        tableModel.setRowCount(0);

        roomList = roomDB.getAllRooms();

        for (Room r : roomList) {
            tableModel.addRow(new Object[]{
                    r.getId(), r.getRoomNumber(), r.getType(),
                    r.getPrice(), r.getStatus(), r.getAmenities()
            });
        }
    }
}