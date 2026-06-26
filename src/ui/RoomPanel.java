package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoomPanel extends JPanel {
    private final String imageDir = "src/images/";
    private JFrame dashboard;
    private final Color NAVY_BLUE = new Color(44, 62, 80);
    private final Color AURELIA_GOLD = new Color(197, 160, 89);

    public RoomPanel(JFrame dashboard) {
        this.dashboard = dashboard;
        setLayout(new BorderLayout());
        setBackground(NAVY_BLUE);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        JLabel lblMainTitle = new JLabel("AURELIA GRAND ROOMS");
        lblMainTitle.setFont(new Font("Serif", Font.PLAIN, 28));
        lblMainTitle.setForeground(AURELIA_GOLD);
        lblMainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel underline = new JPanel();
        underline.setBackground(AURELIA_GOLD);
        underline.setMaximumSize(new Dimension(60, 2));

        headerPanel.add(lblMainTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(underline);

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(0, 60, 60, 60));

        gridPanel.add(createRoomCard("Superior Room",
                "A sanctuary amidst the bustle of the city, offering a space where modern comfort comes naturally.",
                imageDir + "superior.jpg"));

        gridPanel.add(createRoomCard("Deluxe Room",
                "Experience sophisticated comfort designed for travelers seeking a relaxing and productive stay.",
                imageDir + "deluxe.jpg"));

        gridPanel.add(createRoomCard("Family Room",
                "Designed to bring everyone together, the Family Room is your cozy urban home away from home.",
                imageDir + "family.jpg"));

        gridPanel.add(createRoomCard("Specialty Room",
                "An elevated experience featuring generous space and unrivaled comfort for a romantic escape.",
                imageDir + "specialty.jpg"));

        add(headerPanel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
    }

    private JPanel createRoomCard(String title, String description, String imgPath) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1),
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String pageName = title.toUpperCase().replace(" ", "_") + "_DETAILS";
                if (dashboard instanceof MainFrame) ((MainFrame) dashboard).showCard(pageName);
                else if (dashboard instanceof AdminDashboard) ((AdminDashboard) dashboard).showPage(pageName);
                else if (dashboard instanceof ClerkDashboard) ((ClerkDashboard) dashboard).showPage(pageName);
                else {
                    try { dashboard.getClass().getMethod("showPage", String.class).invoke(dashboard, pageName); }
                    catch (Exception ex) {}
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AURELIA_GOLD, 2),
                        BorderFactory.createEmptyBorder(0, 0, 15, 0)
                ));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1),
                        BorderFactory.createEmptyBorder(0, 0, 15, 0)
                ));
            }
        });

        JLabel imgLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ImageIcon icon = new ImageIcon(imgPath);
                if (icon.getImage() != null) g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        imgLabel.setPreferredSize(new Dimension(300, 200));
        imgLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 200));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel("<html><body style='width: 250px; color:#BBB;'>" + description + "</body></html>");
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));

        textPanel.add(lblTitle);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(lblDesc);

        card.add(imgLabel);
        card.add(textPanel);
        return card;
    }
}