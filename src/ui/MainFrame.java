package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContent;

    public MainFrame() {
        setTitle("Hotel Room Reservation System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 20));
        navBar.setBackground(Color.WHITE);

        JButton btnAbout = new JButton("ABOUT");
        JButton btnRooms = new JButton("ROOMS");
        JButton btnLogin = new JButton("LOGIN");

        styleNavButton(btnAbout);
        styleNavButton(btnRooms);
        styleNavButton(btnLogin);

        navBar.add(btnAbout);
        navBar.add(btnRooms);
        navBar.add(btnLogin);
        add(navBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(Color.WHITE);

        mainContent.add(createAboutPage(), "ABOUT_PAGE");
        mainContent.add(createRoomsPage(), "ROOMS_PAGE");
        mainContent.add(new LoginFormPanel(this), "LOGIN_PAGE");
        mainContent.add(new SignUpPanel(this), "SIGNUP_PAGE");
        mainContent.add(new ForgotPasswordPanel(this), "FORGOT_PASSWORD_PAGE");

        add(mainContent, BorderLayout.CENTER);

        // BUTTON ACTIONS
        btnAbout.addActionListener(e -> cardLayout.show(mainContent, "ABOUT_PAGE"));
        btnRooms.addActionListener(e -> cardLayout.show(mainContent, "ROOMS_PAGE"));
        btnLogin.addActionListener(e -> cardLayout.show(mainContent, "LOGIN_PAGE"));
    }

    private void styleNavButton(JButton btn) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.BLACK);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(197, 160, 89));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.BLACK);            }
        });
    }

    private JPanel createAboutPage() {
        return new AboutPanel();
    }

    private JPanel createRoomsPage() {
        return new RoomPanel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

    public void showCard(String cardName) {
        cardLayout.show(mainContent, cardName);
    }
}