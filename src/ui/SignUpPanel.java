package ui;

import dao.UserDB;
import javax.swing.*;
import java.awt.*;

public class SignUpPanel extends JPanel {
    private JTextField txtName = new JTextField(20);
    private JTextField txtUsername = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JTextField txtContact = new JTextField(20);
    private JButton btnSubmit = new JButton("Sign Up");

    private MainFrame parent;

    public SignUpPanel(MainFrame parent) {
        this.parent = parent;
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        // BOX
        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setBackground(Color.WHITE);
        contentWrap.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        GridBagConstraints wrapGbc = new GridBagConstraints();
        wrapGbc.insets = new Insets(5, 15, 5, 15);
        wrapGbc.fill = GridBagConstraints.NONE;

        // HEADER
        JLabel lblTitle = new JLabel("SignUp", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        wrapGbc.gridy = 0; wrapGbc.gridx = 0; wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;
        wrapGbc.insets = new Insets(20, 15, 20, 15);
        contentWrap.add(lblTitle, wrapGbc);

        wrapGbc.insets = new Insets(5, 15, 5, 15);
        wrapGbc.gridwidth = 1;

        // FORM FIELDS
        addLabelAndField("Full Name:", txtName, 1, wrapGbc, contentWrap);
        addLabelAndField("Username:", txtUsername, 2, wrapGbc, contentWrap);
        addLabelAndField("Password:", txtPassword, 3, wrapGbc, contentWrap);
        addLabelAndField("Contact:", txtContact, 4, wrapGbc, contentWrap);

        // SUBMIT BUTTON
        wrapGbc.gridy = 5;
        wrapGbc.gridx = 0;
        wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;

        wrapGbc.insets = new Insets(15, 15, 5, 15);
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 18));
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(200, 40));
        contentWrap.add(btnSubmit, wrapGbc);

        // BACK TO LOGIN
        JLabel lblBack = new JLabel("Already have an account? Login here", SwingConstants.CENTER);
        lblBack.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wrapGbc.gridy = 6;
        wrapGbc.gridx = 0;
        wrapGbc.gridwidth = 2;

        wrapGbc.insets = new Insets(5, 15, 20, 15);
        contentWrap.add(lblBack, wrapGbc);

        add(contentWrap);

        // BUTTON ACTIONS
        btnSubmit.addActionListener(e -> {
            String name = txtName.getText().trim();
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());
            String contact = txtContact.getText().trim();

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            UserDB db = new UserDB();
            if (db.registerGuest(user, pass, name, contact)) {
                JOptionPane.showMessageDialog(this, "Success! You can now login.");
                parent.showCard("LOGIN_PAGE");
            } else {
                JOptionPane.showMessageDialog(this, "Error: Username might be taken.");
            }
        });

        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                parent.showCard("LOGIN_PAGE");
            }
        });
    }

    private void addLabelAndField(String labelText, JComponent field, int row, GridBagConstraints gbc, JPanel container) {
        gbc.gridy = row;
        gbc.gridwidth = 1;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0;
        container.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 0;
        field.setPreferredSize(new Dimension(250, 30));
        container.add(field, gbc);
    }
}