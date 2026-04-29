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

    public GuestTablePanel() {
        this.guestDB = new GuestDB();

        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Guests");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        actions.setBackground(Color.WHITE);
        actions.add(createActionLabel("Register Guest", "ADD"));
        actions.add(createActionLabel("Edit Guest", "EDIT"));
        actions.add(createActionLabel("Delete Guest", "DELETE"));
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table Implementation
        String[] columns = {"ID", "Name", "Contact", "ID Number", "Username", "Password"};        tableModel = new DefaultTableModel(columns, 0);
        guestTable = new JTable(tableModel);

        guestTable.setRowHeight(40);
        guestTable.setShowGrid(false);
        guestTable.setIntercellSpacing(new Dimension(0, 0));
        guestTable.setSelectionBackground(new Color(230, 220, 200));
        guestTable.setFocusable(false);

        guestTable.getTableHeader().setBackground(Color.WHITE);
        guestTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        guestTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        ((DefaultTableCellRenderer)guestTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        loadGuests();

        JScrollPane scrollPane = new JScrollPane(guestTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadGuests() {
        tableModel.setRowCount(0);
        ArrayList<Guest> guestList = guestDB.getAllGuests();
        for (Guest g : guestList) {
            tableModel.addRow(new Object[]{
                    g.getGuestId(),
                    g.getName(),
                    g.getContact(),
                    g.getIdNumber(),
                    g.getUsername(),
                    g.getPassword()
            });
        }
    }

    private JLabel createActionLabel(String text, String action) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
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
            public void mouseEntered(MouseEvent e) {
                label.setForeground(new Color(197, 160, 89));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(Color.BLACK);
            }
        });
        return label;
    }

    private void handleEdit(int row, Window parent) {
        if (row == -1) {
            JOptionPane.showMessageDialog(parent, "Select a guest to edit!");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String contact = (String) tableModel.getValueAt(row, 2);
        String idNum = (String) tableModel.getValueAt(row, 3);

        String username = (String) tableModel.getValueAt(row, 4);
        String password = (String) tableModel.getValueAt(row, 5);

        JTextField nameField = new JTextField(name);
        JTextField contactField = new JTextField(contact);
        JTextField idField = new JTextField(idNum);

        Object[] message = { "Guest Name:", nameField, "Contact:", contactField, "ID Number:", idField };

        int option = JOptionPane.showConfirmDialog(parent, message, "Edit Guest", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            if (guestDB.updateGuest(id, nameField.getText(), contactField.getText(), idField.getText(), username, password)) {
                loadGuests();
                JOptionPane.showMessageDialog(parent, "Guest updated!");
            }
        }
    }

    private void handleDelete(int row, Window parent) {
        if (row == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a guest to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(parent, "Delete this guest?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (guestDB.deleteGuest(id)) {
                loadGuests();
                JOptionPane.showMessageDialog(parent, "Guest deleted!");
            } else {
                JOptionPane.showMessageDialog(parent, "Error: Cannot delete guest.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}