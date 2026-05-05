package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoomPanel extends JPanel {
    private final String imageDir = "src/images/";

    public RoomPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0)); // Reduced vertical padding

        JLabel lblMainTitle = new JLabel("AURELIA GRAND ROOMS");
        lblMainTitle.setFont(new Font("Serif", Font.PLAIN, 24)); // Smaller title size
        lblMainTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel underline = new JPanel();
        underline.setBackground(new Color(52, 73, 94));
        underline.setMaximumSize(new Dimension(40, 2)); // Smaller underline

        headerPanel.add(lblMainTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(underline);

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(0, 40, 40, 40));

        gridPanel.add(createRoomCard("Superior Room",
                "A sanctuary amidst the bustle of the city, offering a space where modern comfort comes naturally.",
                imageDir + "superior.jpg"));

        gridPanel.add(createRoomCard("Deluxe Room",
                "Experience sophisticated comfort designed for travelers seeking a relaxing and productive stay in Cebu.",
                imageDir + "deluxe.jpg"));

        gridPanel.add(createRoomCard("Family Room",
                "Designed to bring everyone together, the Family Room is your cozy urban home away from home.",
                imageDir + "family.jpg"));

        gridPanel.add(createRoomCard("Specialty Room",
                "An elevated experience featuring generous space and unrivaled comfort for a truly romantic escape.",
                imageDir + "specialty.jpg"));

        add(headerPanel, BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
    }

    private JPanel createRoomCard(String title, String description, String imgPath) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(0, 0, 10, 0) // Reduced bottom padding
        ));

        JLabel imgLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                ImageIcon icon = new ImageIcon(imgPath);
                if (icon.getImage() != null && icon.getIconWidth() > 0) {
                    g2.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2.setColor(new Color(240, 240, 240));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imgLabel.setPreferredSize(new Dimension(300, 180));
        imgLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 180));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(new EmptyBorder(10, 20, 10, 20)); // Compact text padding
        textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16)); // Slightly smaller title
        lblTitle.setForeground(new Color(44, 62, 80));

        JLabel lblDesc = new JLabel("<html><body style='width: 250px;'>" + description + "</body></html>");
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(100, 100, 100));

        textPanel.add(lblTitle);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(lblDesc);

        card.add(imgLabel);
        card.add(textPanel);

        return card;
    }
}