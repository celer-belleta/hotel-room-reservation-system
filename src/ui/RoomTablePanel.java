package ui;

import dao.RoomDB;
import model.Room;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class RoomTablePanel extends JPanel {
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private RoomDB roomDB;
    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color BG_COLOR = new Color(245, 246, 247);

    public RoomTablePanel(String role) {
        this.roomDB = new RoomDB();
        setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(getBackground());
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Rooms");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        header.add(title, BorderLayout.WEST);

        if (role.equalsIgnoreCase("Admin")) {
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
            actions.setBackground(getBackground());
            actions.add(createActionLabel("Add Room", "ADD"));
            actions.add(createActionLabel("Edit Room", "EDIT"));
            actions.add(createActionLabel("Delete Room", "DELETE"));
            header.add(actions, BorderLayout.EAST);
        }
        add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Room #", "Type", "Max # of Guest", "Package", "Price (Total)", "Status", "Room Inclusions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(110);
        roomTable.setSelectionBackground(new Color(245, 230, 200));
        roomTable.setShowGrid(false);
        roomTable.setFocusable(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        TableColumnModel columnModel = roomTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);  // ID
        columnModel.getColumn(1).setPreferredWidth(70);  // Room #
        columnModel.getColumn(2).setPreferredWidth(120); // Type
        columnModel.getColumn(3).setPreferredWidth(120);  // Max Guest
        columnModel.getColumn(4).setPreferredWidth(130); // Package
        columnModel.getColumn(5).setPreferredWidth(100); // Price
        columnModel.getColumn(6).setPreferredWidth(90);  // Status
        columnModel.getColumn(7).setPreferredWidth(350); // Inclusions

        for (int i = 0; i < 7; i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }

        roomTable.getTableHeader().setBackground(Color.WHITE);
        roomTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        roomTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        loadRooms();

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(Color.WHITE);
        tableWrapper.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        add(tableWrapper, BorderLayout.CENTER);
    }

    public void loadRooms() {
        tableModel.setRowCount(0);
        ArrayList<Room> roomList = roomDB.getAllRooms();
        for (Room r : roomList) {
            String htmlInclusions = "<html><div style='padding-left: 15px; padding-top: 10px;'>" +
                    r.getAmenities().replace(", ", "<br>") +
                    "</div></html>";

            double base = r.getPrice();
            String pkg = r.getPackageType();
            double fee = pkg.equals("Room + Breakfast") ? 500 : pkg.equals("Room + Amenities") ? 1000 : 0;
            double totalDisplayPrice = base + fee;

            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getRoomNumber(),
                    r.getType(),
                    r.getMaxGuest(),
                    pkg,
                    "₱" + String.format("%,.2f", totalDisplayPrice),
                    r.getStatus(),
                    htmlInclusions
            });
        }
    }

    private JLabel createActionLabel(String text, String action) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (action.equals("ADD")) new AddRoomFrame(RoomTablePanel.this).setVisible(true);
                else if (action.equals("EDIT")) handleEdit(roomTable.getSelectedRow());
                else if (action.equals("DELETE")) handleDelete(roomTable.getSelectedRow());
            }
            @Override
            public void mouseEntered(MouseEvent e) { label.setForeground(AURELIA_GOLD); }
            @Override
            public void mouseExited(MouseEvent e) { label.setForeground(Color.BLACK); }
        });
        return label;
    }

    private void handleEdit(int row) {
        if (row == -1) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Please select a room!");
            return;
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        int id = (int) tableModel.getValueAt(row, 0);

        Room currentRoom = null;
        for(Room r : roomDB.getAllRooms()) {
            if(r.getId() == id) { currentRoom = r; break; }
        }
        if (currentRoom == null) return;

        JTextField txtNum = new JTextField(currentRoom.getRoomNumber());
        JTextField txtBasePrice = new JTextField(String.valueOf(currentRoom.getPrice()));

        Integer[] guestOptions = {1, 2, 3, 4, 5, 6};
        JComboBox<Integer> cbGuest = new JComboBox<>(guestOptions);
        cbGuest.setSelectedItem(currentRoom.getMaxGuest());

        String[] types = {"Standard Room", "Deluxe Room", "Family Room", "Specialty Room"};
        JComboBox<String> cbType = new JComboBox<>(types);
        cbType.setSelectedItem(currentRoom.getType());

        String[] packages = {"Room Only", "Room + Breakfast", "Room + Amenities"};
        JComboBox<String> cbPkg = new JComboBox<>(packages);
        cbPkg.setSelectedItem(currentRoom.getPackageType());

        String[] statuses = {"Available", "Occupied", "Maintenance"};
        JComboBox<String> cbStatus = new JComboBox<>(statuses);
        cbStatus.setSelectedItem(currentRoom.getStatus());

        Object[] message = {
                "Room Number:", txtNum,
                "Category:", cbType,
                "Max Guests (Capacity):", cbGuest,
                "Package Type:", cbPkg,
                "Base Room Price (₱):", txtBasePrice,
                "Status:", cbStatus
        };

        int option = JOptionPane.showConfirmDialog(parentWindow, message, "Update Room Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String selectedType = cbType.getSelectedItem().toString();
                String selectedPkg = cbPkg.getSelectedItem().toString();
                int selectedGuest = (int) cbGuest.getSelectedItem();
                String inclusions = getInclusions(selectedType);

                if (roomDB.updateRoom(id, txtNum.getText().trim(), selectedType,
                        Double.parseDouble(txtBasePrice.getText().trim()),
                        cbStatus.getSelectedItem().toString(), inclusions, selectedPkg, selectedGuest)) {
                    loadRooms();
                    JOptionPane.showMessageDialog(parentWindow, "Room updated successfully!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentWindow, "Invalid input. Please check your data.");
            }
        }
    }

    private void handleDelete(int row) {
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);

        int choice = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Delete ID: " + id + "?",
                "Select an Option",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            if (roomDB.deleteRoom(id)) {
                loadRooms();
            }
        }
    }

    private String getInclusions(String type) {
        switch (type) {
            case "Standard Room": return "• 1 Queen Bed, • Hot and Cold Shower, • LED TV & High-Speed Wi-Fi";
            case "Deluxe Room": return "• 1 King Bed, • City View, • Mini-Fridge, • LED TV & High-Speed Wi-Fi";
            case "Family Room": return "• 2 Queen Beds, • Pantry Area with Microwave, • Mini-Bar, • LED TV & High-Speed Wi-Fi";
            case "Specialty Room": return "• 1 California King Bed, • Small Balcony, • Bathtub, • LED TV & High-Speed Wi-Fi";
            default: return "";
        }
    }
}