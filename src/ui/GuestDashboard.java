package ui;

import dao.RoomDB;
import model.Room;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GuestDashboard extends JFrame {

    private JPanel roomListPanel; // This will hold all our room "cards"

    public GuestDashboard(String username) {
        // Basic Frame Setup
        setTitle("MAAYO HOTEL - Welcome " + username);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Opens in full screen
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        //  HEADER SECTION
        JLabel headerLabel = new JLabel("Available Rooms & Exclusive Offers", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // 2. MIDDLE SECTION (Scrollable List)
        roomListPanel = new JPanel();
        roomListPanel.setLayout(new BoxLayout(roomListPanel, BoxLayout.Y_AXIS));

        // JScrollPane allows the user to scroll if there are many rooms
        JScrollPane scrollPane = new JScrollPane(roomListPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 3. LOAD THE DATA
        displayAvailableRooms();
    }

    private void displayAvailableRooms() {
        RoomDB roomDb = new RoomDB();

        // Pull all rooms using your existing RoomDB method
        ArrayList<Room> allRooms = roomDb.getAllRooms();

        // Loop through each room found in the database
        for (Room r : allRooms) {

            // Only show rooms that are actually 'Available'
            if (r.getStatus().equalsIgnoreCase("Available")) {

                // Create a "Card" (a small panel) for each room
                JPanel card = new JPanel(new GridLayout(4, 1, 5, 5));
                card.setBorder(BorderFactory.createTitledBorder("Room Info"));
                card.setMaximumSize(new Dimension(800, 200)); // Keeps cards from stretching too tall

                // Room Type & Price
                JLabel typeLabel = new JLabel("Type: " + r.getType());
                typeLabel.setFont(new Font("Arial", Font.BOLD, 18));

                JLabel priceLabel = new JLabel("Price: ₱" + r.getPrice() + " per night");

                // Amenities/Perks (This is the 'offers' part your prof wanted)
                String perks = r.getAmenities();
                JLabel perksLabel = new JLabel("Inclusions: " + (perks != null ? perks : "Standard Room Perks"));

                // Booking Button
                JButton btnBook = new JButton("Book " + r.getType() + " Now");

                // Add everything to the card
                card.add(typeLabel);
                card.add(priceLabel);
                card.add(perksLabel);
                card.add(btnBook);

                // Add the card to our main list panel
                roomListPanel.add(card);

                // Add a small invisible gap between cards so they don't touch
                roomListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        // Refresh the panel to show the newly added rooms
        roomListPanel.revalidate();
        roomListPanel.repaint();
    }
}