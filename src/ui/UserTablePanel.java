package ui;

import dao.UserDB;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class UserTablePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private UserDB userDB;
    private final Color AURELIA_GOLD = new Color(197, 160, 89);
    private final Color BG_COLOR = new Color(245, 246, 247);

    public UserTablePanel() {
        this.userDB = new UserDB();

        setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Users");
        title.setFont(new Font("Serif", Font.BOLD, 30));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        actions.setBackground(BG_COLOR);
        actions.add(createActionLabel("Add User", "ADD"));
        actions.add(createActionLabel("Edit User", "EDIT"));
        actions.add(createActionLabel("Delete User", "DELETE"));
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "First Name", "Last Name", "Username", "Password", "Role"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(245, 230, 200));
        table.setFocusable(false);

        // Password Masking Renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : "••••••••");
                setHorizontalAlignment(JLabel.CENTER);
            }
        });

        // Table Header Styling
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        loadUsers();

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);
    }

    public void loadUsers() {
        model.setRowCount(0);
        ArrayList<User> users = userDB.getAllUsers();
        for (User u : users) {
            model.addRow(new Object[]{
                    u.getId(), u.getFirstName(), u.getLastName(),
                    u.getUsername(), u.getPassword(), u.getRole()
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
                int row = table.getSelectedRow();
                Window parentWindow = SwingUtilities.getWindowAncestor(UserTablePanel.this);

                if (action.equals("ADD")) {
                    new AddUserFrame(UserTablePanel.this).setVisible(true);
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
            JOptionPane.showMessageDialog(parent, "Please select a user to edit.");
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        User currentUser = null;
        ArrayList<User> allUsers = userDB.getAllUsers();
        for (User u : allUsers) {
            if (u.getId() == id) { currentUser = u; break; }
        }
        if (currentUser == null) return;

        JTextField fNameField = new JTextField(currentUser.getFirstName());
        JTextField lNameField = new JTextField(currentUser.getLastName());
        JTextField userField = new JTextField(currentUser.getUsername());
        JTextField passField = new JTextField(currentUser.getPassword());

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Clerk"});
        roleBox.setSelectedItem(currentUser.getRole());

        Object[] message = {
                "First Name:", fNameField,
                "Last Name:", lNameField,
                "Username:", userField,
                "Password:", passField,
                "Role:", roleBox
        };

        // Added JOptionPane.PLAIN_MESSAGE for cleaner UI[cite: 2]
        int option = JOptionPane.showConfirmDialog(parent, message, "Edit User: " + id,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            if (userDB.updateUser(id, fNameField.getText().trim(), lNameField.getText().trim(),
                    userField.getText().trim(), passField.getText().trim(), (String)roleBox.getSelectedItem())) {
                loadUsers();
                JOptionPane.showMessageDialog(parent, "User updated successfully!");
            }
        }
    }

    private void handleDelete(int row, Window parent) {
        if (row == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a user to delete.");
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        String role = (String) table.getValueAt(row, 5);

        if (role.equalsIgnoreCase("Admin")) {
            JOptionPane.showMessageDialog(parent, "Error: You cannot delete an Admin account!", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(parent, "Delete this user?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (userDB.deleteUser(id)) {
                loadUsers();
                JOptionPane.showMessageDialog(parent, "User deleted!");
            }
        }
    }
}