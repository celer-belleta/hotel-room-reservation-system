package ui;

import dao.UserDB;

import javax.swing.*;
import java.awt.*;

public class AddUserFrame extends JFrame {

    private JTextField usernameField;
    private JTextField passwordField;
    private JComboBox<String> roleComboBox;

    public AddUserFrame(UserTableFrame parent) {

        setTitle("Add User");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JTextField();
        add(passwordField);

        add(new JLabel("Role:"));
        String[] roles = {"Admin", "Clerk"};
        roleComboBox = new JComboBox<>(roles);
        add(roleComboBox);

        JButton addBtn = new JButton("Add User");

        addBtn.addActionListener(e -> addUser());

        add(new JLabel());
        add(addBtn);

        setVisible(true);
    }

    private void addUser() {

        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        UserDB db = new UserDB();
        db.addUser(username, password, role);

        JOptionPane.showMessageDialog(this, "User added!");
        dispose();
    }
}