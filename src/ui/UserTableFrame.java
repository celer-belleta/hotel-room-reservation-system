package ui;

import dao.UserDB;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class UserTableFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private UserDB userDB;

    public UserTableFrame() {

        userDB = new UserDB();

        setTitle("User List");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("ID");
        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Role");

        loadUsers();

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton deleteBtn = new JButton("Delete Selected User");
        JButton editBtn = new JButton("Edit Selected User");

        deleteBtn.addActionListener(e -> deleteUser());
        editBtn.addActionListener(e -> editUser());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(deleteBtn);
        bottomPanel.add(editBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadUsers() {

        model.setRowCount(0);

        ArrayList<User> users = userDB.getAllUsers();

        for (User u : users) {
            model.addRow(new Object[]{
                    u.getId(),
                    u.getUsername(),
                    u.getPassword(),
                    u.getRole()
            });
        }
    }

    private void deleteUser() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user first!");
            return;
        }

        String role = (String) table.getValueAt(row, 3); // role column

        if (role.equalsIgnoreCase("Admin")) {
            JOptionPane.showMessageDialog(this, "Cannot delete Admin!");
            return;
        }

        int id = (int) table.getValueAt(row, 0);

        userDB.deleteUser(id);

        loadUsers();
    }

    private void editUser() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user first!");
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        String username = (String) table.getValueAt(row, 1);
        String password = (String) table.getValueAt(row, 2);
        String role = (String) table.getValueAt(row, 3);

        JTextField usernameField = new JTextField(username);
        JTextField passwordField = new JTextField(password);
        JTextField roleField = new JTextField(role);

        Object[] fields = {
                "Username:", usernameField,
                "Password:", passwordField,
                "Role:", roleField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            if (usernameField.getText().isEmpty() ||
                    passwordField.getText().isEmpty() ||
                    roleField.getText().isEmpty()) {

                JOptionPane.showMessageDialog(this, "All fields required!");
                return;
            }

            userDB.updateUser(
                    id,
                    usernameField.getText(),
                    passwordField.getText(),
                    roleField.getText()
            );

            loadUsers();
        }
    }
}