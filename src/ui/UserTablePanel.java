package ui;

import dao.UserDB;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class UserTablePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private UserDB userDB;

    public UserTablePanel() {
        this.userDB = new UserDB();

        setBackground(Color.WHITE); //
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // Header Section
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Users");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        actions.setBackground(Color.WHITE);

        actions.add(createActionLabel("Add User", "ADD"));
        actions.add(createActionLabel("Edit User", "EDIT"));
        actions.add(createActionLabel("Delete User", "DELETE"));
        header.add(actions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Username", "Password", "Role"}, 0);
        table = new JTable(model);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.setSelectionBackground(new Color(230, 220, 200));
        table.setSelectionForeground(Color.BLACK);
        table.setFocusable(false);

        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));

        loadUsers();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadUsers() {
        model.setRowCount(0);
        ArrayList<User> users = userDB.getAllUsers();
        for (User u : users) {
            model.addRow(new Object[]{u.getId(), u.getUsername(), u.getPassword(), u.getRole()});
        }
    }

    private JLabel createActionLabel(String text, String action) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();

                if (action.equals("ADD")) {
                    new AddUserFrame(UserTablePanel.this).setVisible(true);
                } else if (action.equals("EDIT")) {
                    if (row != -1) {
                        int id = (int) table.getValueAt(row, 0);
                        String currentUsername = (String) table.getValueAt(row, 1);
                        String currentRole = (String) table.getValueAt(row, 3);

                        // Create the input fields
                        JTextField userField = new JTextField(currentUsername);
                        JTextField passField = new JTextField(); // Leave password blank for security
                        String[] roles = {"Admin", "Clerk"};
                        JComboBox<String> roleBox = new JComboBox<>(roles);
                        roleBox.setSelectedItem(currentRole);

                        Object[] message = {
                                "Username:", userField,
                                "New Password:", passField,
                                "Role:", roleBox
                        };

                        int option = JOptionPane.showConfirmDialog(null, message, "Edit User: " + id, JOptionPane.OK_CANCEL_OPTION);

                        if (option == JOptionPane.OK_OPTION) {
                            String newUsername = userField.getText();
                            String newPassword = passField.getText();
                            String newRole = (String) roleBox.getSelectedItem();

                            if (userDB.updateUser(id, newUsername, newPassword, newRole)) {
                                loadUsers(); // Refresh the table
                                JOptionPane.showMessageDialog(null, "User updated successfully!");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please select a user to edit.");
                    }
                } else if (action.equals("DELETE")) {
                    if (row != -1) {
                        int id = (int) table.getValueAt(row, 0);
                        String role = (String) table.getValueAt(row, 3);

                        // SAFETY CHECK: Prevent deleting Admins
                        if (role.equalsIgnoreCase("Admin")) {
                            JOptionPane.showMessageDialog(null, "Error: You cannot delete an Admin account for security reasons!", "Access Denied", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            if (userDB.deleteUser(id)) {
                                loadUsers();
                                JOptionPane.showMessageDialog(null, "User deleted successfully.");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please select a user to delete.");
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(new Color(197, 160, 89)); // Gold highlight on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(Color.BLACK);
            }
        });

        return label;
    }
}