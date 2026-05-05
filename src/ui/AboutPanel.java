package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AboutPanel extends JPanel {
    private String imagePath = "src/images/about_here.jpg";
    private JLabel descriptionLabel;

    public AboutPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel heroImage = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage();
                if (img != null && icon.getIconWidth() > 0) {
                    g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRect(getWidth() / 2, 0, getWidth() / 2, getHeight());

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 90));
                FontMetrics fm = g2.getFontMetrics();
                int labelY = (getHeight() / 2) + (fm.getAscent() / 4);
                g2.drawString("ABOUT", 100, labelY);
            }
        };

        heroImage.setLayout(null);

        String hotelFocusText = "<html><div style='line-height: 1.6;'>" +
                "<h1 style='color: white; font-family: Arial; font-size: 28pt;'>Aurelia Grand Hotel</h1>" +
                "<p style='color: white; font-family: Arial; font-size: 15pt;'>" +
                "Nestled in the vibrant heart of Cebu, Aurelia Grand Hotel is your " +
                "premier urban sanctuary. Every corner of our hotel is designed to " +
                "immerse you in a world of modern elegance and tranquil comfort.<br><br>" +
                "From our exquisitely detailed guest suites featuring panoramic city views " +
                "to our signature Cebuano hospitality, we offer a stay that is both " +
                "lavish and deeply personal. Whether you are unwinding in our " +
                "world-class leisure facilities or enjoying a quiet moment in our " +
                "sophisticated lounges, Aurelia is where your journey becomes an " +
                "unforgettable story of luxury.</p></div></html>";

        descriptionLabel = new JLabel(hotelFocusText);
        descriptionLabel.setVerticalAlignment(SwingConstants.CENTER);
        heroImage.add(descriptionLabel);

        heroImage.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelWidth = heroImage.getWidth();
                int panelHeight = heroImage.getHeight();
                descriptionLabel.setBounds((panelWidth / 2) + 60, 0, (panelWidth / 2) - 120, panelHeight);
            }
        });

        add(heroImage, BorderLayout.CENTER);
    }
}