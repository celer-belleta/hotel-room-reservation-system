package ui;

import javax.swing.*;
import java.awt.*;

public class ClerkDashboard extends JFrame {

    public ClerkDashboard() {

        setTitle("Clerk Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("CLERK DASHBOARD", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 50));

        add(title, BorderLayout.CENTER);
    }
}