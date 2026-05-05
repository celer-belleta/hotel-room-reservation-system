package ui;

import dao.GuestDB;
import model.Guest;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GuestTablePanel extends JPanel {
    private JTable guestTable;
    private DefaultTableModel tableModel;
    private GuestDB guestDB;
    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color BG_COLOR = new Color(245, 246, 247);

    public GuestTablePanel() {
        this.guestDB = new GuestDB();

        // Consistent Background with Admin Dashboard
        setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // --- HEADER SECTION ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Padding below actions

        JLabel title = new JLabel("Guests");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        header.add(title, BorderLayout.WEST);

        // Actions Panel: Kept outside the table border
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        actions.setBackground(BG_COLOR);
        actions.add(createActionLabel("Register Guest", "ADD"));
        actions.add(createActionLabel("Edit Guest", "EDIT"));
        actions.add(createActionLabel("Delete Guest", "DELETE"));
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // --- TABLE SECTION ---
        String[] columns = {"ID", "First Name", "Last Name", "Contact", "ID Type", "ID Number", "Username", "Password"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        guestTable = new JTable(tableModel);
        guestTable.setRowHeight(40);
        guestTable.setShowGrid(false);
        guestTable.setIntercellSpacing(new Dimension(0, 0));
        guestTable.setSelectionBackground(new Color(245, 230, 200));
        guestTable.setFocusable(false);

        // Table Header Styling
        guestTable.getTableHeader().setBackground(Color.WHITE);
        guestTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        guestTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        ((DefaultTableCellRenderer)guestTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        // Password Masking Renderer
        guestTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : "••••••••");
                setHorizontalAlignment(JLabel.CENTER);
            }
        });

        loadGuests();

        // WRAPPING THE TABLE: Adding border lines only around the table area
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // The Table Border

        JScrollPane scrollPane = new JScrollPane(guestTable);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove internal scroll border

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);
    }

    public void loadGuests() {
        tableModel.setRowCount(0);
        ArrayList<Guest> guestList = guestDB.getAllGuests();
        for (Guest g : guestList) {
            tableModel.addRow(new Object[]{
                    g.getGuestId(), g.getFirstName(), g.getLastName(),
                    g.getContact(), g.getIdType(), g.getIdNumber(),
                    g.getUsername(), g.getPassword()
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
                int row = guestTable.getSelectedRow();
                Window parentWindow = SwingUtilities.getWindowAncestor(GuestTablePanel.this);

                if (action.equals("ADD")) {
                    new AddGuestFrame(GuestTablePanel.this).setVisible(true);
                } else if (action.equals("EDIT")) {
                    handleEdit(row, parentWindow);
                } else if (action.equals("DELETE")) {
                    handleDelete(row, parentWindow);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) { label.setForeground(AURELIA_GOLD); }
            @Override
            public void mouseExited(MouseEvent e) { label.setForeground(Color.BLACK); }
        });
        return label;
    }

    private void handleEdit(int row, Window parent) {
        if (row == -1) {
            JOptionPane.showMessageDialog(parent, "Select a guest to edit!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        JTextField fNameField = new JTextField((String) tableModel.getValueAt(row, 1));
        JTextField lNameField = new JTextField((String) tableModel.getValueAt(row, 2));
        JTextField contactField = new JTextField((String) tableModel.getValueAt(row, 3));

        JComboBox<String> idTypeBox = new JComboBox<>(new String[]{"Passport", "Drivers License", "National ID", "Student ID"});
        idTypeBox.setSelectedItem((String) tableModel.getValueAt(row, 4));

        JTextField idNumField = new JTextField((String) tableModel.getValueAt(row, 5));
        JTextField userField = new JTextField((String) tableModel.getValueAt(row, 6));
        JTextField passField = new JTextField((String) tableModel.getValueAt(row, 7));

        Object[] message = {
                "First Name:", fNameField, "Last Name:", lNameField,
                "Contact:", contactField, "ID Type:", idTypeBox,
                "ID Number:", idNumField, "Username:", userField, "Password:", passField
        };

        int option = JOptionPane.showConfirmDialog(parent, message, "Edit Guest Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            if (guestDB.updateGuest(id, fNameField.getText().trim(), lNameField.getText().trim(),
                    contactField.getText().trim(), (String)idTypeBox.getSelectedItem(),
                    idNumField.getText().trim(), userField.getText().trim(), passField.getText().trim())) {
                loadGuests();
                JOptionPane.showMessageDialog(parent, "Guest updated successfully!");
            }
        }
    }

    private void handleDelete(int row, Window parent) {
        if (row == -1) {
            JOptionPane.showMessageDialog(parent, "Select a guest to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(parent, "Delete guest ID: " + id + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (guestDB.deleteGuest(id)) {
                loadGuests();
                JOptionPane.showMessageDialog(parent, "Guest deleted!");
            }
        }
    }
}