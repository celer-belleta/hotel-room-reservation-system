package ui;

import dao.GuestDB;
import javax.swing.*;
import java.awt.*;

public class SignUpPanel extends JPanel {
    private JTextField txtFirstName = new JTextField(20);
    private JTextField txtLastName = new JTextField(20);
    private JComboBox<String> comboIdType = new JComboBox<>(new String[]{"Passport", "Drivers License", "National ID", "Student ID"});

    private JTextField txtUsername = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JTextField txtContact = new JTextField(20);
    private JTextField txtIdNum = new JTextField(20);
    private JButton btnSubmit = new JButton("Sign Up");

    private MainFrame mainFrame;

    public SignUpPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.setBackground(Color.WHITE);
        contentWrap.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        GridBagConstraints wrapGbc = new GridBagConstraints();
        wrapGbc.insets = new Insets(5, 15, 5, 15);
        wrapGbc.fill = GridBagConstraints.NONE;

        // HEADER
        JLabel lblTitle = new JLabel("Guest Sign Up", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        wrapGbc.gridy = 0; wrapGbc.gridx = 0; wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;
        wrapGbc.insets = new Insets(20, 15, 20, 15);
        contentWrap.add(lblTitle, wrapGbc);

        wrapGbc.insets = new Insets(5, 15, 5, 15);
        wrapGbc.gridwidth = 1;

        addLabelAndField("First Name:", txtFirstName, 1, wrapGbc, contentWrap);
        addLabelAndField("Last Name:", txtLastName, 2, wrapGbc, contentWrap);
        addLabelAndField("Contact:", txtContact, 3, wrapGbc, contentWrap);
        addLabelAndField("ID Type:", comboIdType, 4, wrapGbc, contentWrap);
        addLabelAndField("ID Number:", txtIdNum, 5, wrapGbc, contentWrap);
        addLabelAndField("Username:", txtUsername, 6, wrapGbc, contentWrap);
        addLabelAndField("Password:", txtPassword, 7, wrapGbc, contentWrap);

        // SUBMIT BUTTON
        wrapGbc.gridy = 8;
        wrapGbc.gridx = 0;
        wrapGbc.gridwidth = 2;
        wrapGbc.anchor = GridBagConstraints.CENTER;
        wrapGbc.insets = new Insets(15, 15, 5, 15);
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 18));
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setPreferredSize(new Dimension(200, 40));
        contentWrap.add(btnSubmit, wrapGbc);

        // BACK TO LOGIN LINK
        JLabel lblBack = new JLabel("Already have an account? Login here", SwingConstants.CENTER);
        lblBack.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wrapGbc.gridy = 9;
        wrapGbc.gridx = 0;
        wrapGbc.gridwidth = 2;
        wrapGbc.insets = new Insets(5, 15, 20, 15);
        contentWrap.add(lblBack, wrapGbc);

        add(contentWrap);

        // BUTTON ACTIONS
        btnSubmit.addActionListener(e -> {
            String fName = txtFirstName.getText().trim();
            String lName = txtLastName.getText().trim();
            String user = txtUsername.getText().trim();
            String pass = new String(txtPassword.getPassword());
            String contact = txtContact.getText().trim();
            String idType = (String) comboIdType.getSelectedItem();
            String idNum = txtIdNum.getText().trim();

            if (fName.isEmpty() || lName.isEmpty() || user.isEmpty() || pass.isEmpty() || contact.isEmpty() || idNum.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            GuestDB guestDB = new GuestDB();

            if (guestDB.addGuest(fName, lName, contact, idType, idNum, user, pass)) {
                JOptionPane.showMessageDialog(this, "Success! Your guest account is created.");
                mainFrame.showCard("LOGIN_PAGE");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Registration failed.", "Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainFrame.showCard("LOGIN_PAGE");
            }
        });
    }

    private void clearFields() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtContact.setText("");
        txtIdNum.setText("");
        comboIdType.setSelectedIndex(0);
    }

    private void addLabelAndField(String labelText, JComponent field, int row, GridBagConstraints gbc, JPanel container) {
        gbc.gridy = row;
        gbc.gridwidth = 1;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.1; // Give a small weight to help alignment
        gbc.insets = new Insets(5, 20, 5, 10);
        container.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Dimension fieldSize = new Dimension(250, 30);
        field.setPreferredSize(fieldSize);
        field.setMinimumSize(fieldSize);

        gbc.insets = new Insets(5, 0, 5, 20);
        container.add(field, gbc);

        gbc.fill = GridBagConstraints.NONE;
    }}