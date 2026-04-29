package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboard extends JFrame {

    private JPanel mainContent;
    private CardLayout cardLayout;

    public AdminDashboard() {

        // FRAME SETTINGS
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));

        sidebar.add(Box.createRigidArea(new Dimension(0, 150)));

        addSidebarNav(sidebar, "USERS", "USERS_PAGE");
        addSidebarNav(sidebar, "GUESTS", "GUESTS_PAGE");
        addSidebarNav(sidebar, "ROOMS", "ROOMS_PAGE");
        addSidebarNav(sidebar, "RESERVATIONS", "RESERVATIONS_PAGE");
        addSidebarNav(sidebar, "REPORTS", "REPORTS_PAGE");

        sidebar.add(Box.createVerticalGlue());
        addSidebarNav(sidebar, "LOGOUT", "LOGOUT_ACTION");
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        add(sidebar, BorderLayout.WEST);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(Color.WHITE);

        JLabel lblHotelName = new JLabel("HOTEL NAME", SwingConstants.CENTER);
        lblHotelName.setFont(new Font("Arial", Font.BOLD, 50));
        lblHotelName.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        centerWrapper.add(lblHotelName, BorderLayout.NORTH);

        // CardLayout Area
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setBackground(Color.WHITE);

        // for separate panels
        mainContent.add(new UserTablePanel(), "USERS_PAGE");
        mainContent.add(new GuestTablePanel(), "GUESTS_PAGE");

        // Placeholder panels for other sections
        mainContent.add(new JPanel(), "ROOMS_PAGE");

        centerWrapper.add(mainContent, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        setVisible(true);
    }

    private void addSidebarNav(JPanel container, String text, String cardName) {
        JLabel navLabel = new JLabel(text);
        navLabel.setFont(new Font("Arial", Font.BOLD, 18));
        navLabel.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 0));
        navLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        navLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cardName.equals("LOGOUT_ACTION")) {
                    dispose(); //
                    new MainFrame().setVisible(true);
                } else {
                    cardLayout.show(mainContent, cardName);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                navLabel.setForeground(new Color(197, 160, 89)); // Maayo Gold
            }
            @Override
            public void mouseExited(MouseEvent e) {
                navLabel.setForeground(Color.BLACK);
            }
        });
        container.add(navLabel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard());
    }
}