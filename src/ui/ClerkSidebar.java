package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClerkSidebar extends JPanel {
    private ClerkDashboard dashboard;

    private final Color NAVY_BLUE = new Color(44, 62, 80);
    private final Color AURELIA_GOLD = new Color(197, 160, 89);

    public ClerkSidebar(ClerkDashboard dashboard) {
        this.dashboard = dashboard;
        this.setPreferredSize(new Dimension(260, 0));

        this.setBackground(NAVY_BLUE);
        this.setLayout(new BorderLayout());

        this.setBorder(null);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(NAVY_BLUE);

        menuPanel.add(Box.createRigidArea(new Dimension(0, 110)));

        addNavHeader(menuPanel, "MANAGE GUESTS", "GUEST_LIST");
        addNavHeader(menuPanel, "ROOM AVAILABILITY", "ROOM_LIST");
        addNavHeader(menuPanel, "RESERVATIONS", "RESERVATIONS");

        add(menuPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(NAVY_BLUE);

        JLabel lblLogout = new JLabel("LOGOUT");
        lblLogout.setForeground(Color.WHITE);
        lblLogout.setFont(new Font("Arial", Font.BOLD, 18));
        lblLogout.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 0));
        lblLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { dashboard.showPage("LOGOUT"); }
            @Override
            public void mouseEntered(MouseEvent e) { lblLogout.setForeground(AURELIA_GOLD); }
            @Override
            public void mouseExited(MouseEvent e) { lblLogout.setForeground(Color.WHITE); }
        });

        bottomPanel.add(lblLogout, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addNavHeader(JPanel parent, String text, String card) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 0));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { dashboard.showPage(card); }
            @Override
            public void mouseEntered(MouseEvent e) { label.setForeground(AURELIA_GOLD); }
            @Override
            public void mouseExited(MouseEvent e) { label.setForeground(Color.WHITE); }
        });
        parent.add(label);
    }
}