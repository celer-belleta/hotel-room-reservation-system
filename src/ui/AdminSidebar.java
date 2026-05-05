package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminSidebar extends JPanel {
    private AdminDashboard dashboard;
    private final String chevron = "  \u25BC";

    public AdminSidebar(AdminDashboard dashboard) {
        this.dashboard = dashboard;
        this.setPreferredSize(new Dimension(260, 0));
        this.setBackground(Color.WHITE);
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);

        menuPanel.add(Box.createRigidArea(new Dimension(0, 110)));

        addNavHeader(menuPanel, "DASHBOARD", "DASHBOARD");

        addDropdownMenu(menuPanel, "USERS", new String[][]{
                {"User List", "USER_LIST"},
        });

        addDropdownMenu(menuPanel, "GUESTS", new String[][]{
                {"Manage Guests", "GUEST_LIST"}
        });

        addDropdownMenu(menuPanel, "ROOMS", new String[][]{
                {"Manage Rooms", "ROOM_LIST"}
                //{"Amenities and Packages", "AMENITIES"}
        });

        addDropdownMenu(menuPanel, "RESERVATIONS", new String[][]{
                {"Manage Reservations", "RESERVATIONS"}
        });

        addDropdownMenu(menuPanel, "REPORTS", new String[][]{
                {"Occupancy Reports", "OCCUPANCY_REPORT"},
                {"Revenue Reports", "REVENUE_REPORT"}
        });

        add(menuPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        JLabel lblLogout = new JLabel("LOGOUT");
        lblLogout.setFont(new Font("Arial", Font.BOLD, 18));
        lblLogout.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 0));
        lblLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { dashboard.showPage("LOGOUT"); }
            @Override
            public void mouseEntered(MouseEvent e) { lblLogout.setForeground(new Color(197, 160, 89)); }
            @Override
            public void mouseExited(MouseEvent e) { lblLogout.setForeground(Color.BLACK); }
        });

        bottomPanel.add(lblLogout, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addNavHeader(JPanel parent, String text, String card) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(8, 40, 8, 0));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { dashboard.showPage(card); }
            @Override
            public void mouseEntered(MouseEvent e) { label.setForeground(new Color(197, 160, 89)); }
            @Override
            public void mouseExited(MouseEvent e) { label.setForeground(Color.BLACK); }
        });
        parent.add(label);
    }

    private void addDropdownMenu(JPanel parent, String title, String[][] subItems) {
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBackground(Color.WHITE);
        subPanel.setVisible(false);

        JLabel mainLabel = new JLabel(title + chevron);
        mainLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainLabel.setBorder(BorderFactory.createEmptyBorder(8, 40, 8, 0));
        mainLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        mainLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                subPanel.setVisible(!subPanel.isVisible());
                parent.revalidate();
                parent.repaint();
            }
            @Override
            public void mouseEntered(MouseEvent e) { mainLabel.setForeground(new Color(197, 160, 89)); }
            @Override
            public void mouseExited(MouseEvent e) { mainLabel.setForeground(Color.BLACK); }
        });

        parent.add(mainLabel);

        for (String[] item : subItems) {
            JLabel subLabel = new JLabel(item[0]);
            subLabel.setFont(new Font("Arial", Font.BOLD, 16));
            subLabel.setBorder(BorderFactory.createEmptyBorder(5, 60, 5, 0));
            subLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            subLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { dashboard.showPage(item[1]); }
                @Override
                public void mouseEntered(MouseEvent e) { subLabel.setForeground(new Color(197, 160, 89)); }
                @Override
                public void mouseExited(MouseEvent e) { subLabel.setForeground(Color.BLACK); }
            });
            subLabel.setForeground(Color.BLACK);
            subPanel.add(subLabel);
        }
        parent.add(subPanel);
    }
}