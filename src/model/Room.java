package model;

public class Room {
    private int id;
    private String roomNumber;
    private String type;
    private double price;
    private String status;
    private String amenities;

    public Room(int id, String roomNumber, String type, double price, String status, String amenities) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
        this.amenities = amenities;
    }

    // Getters
    public int getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public String getAmenities() { return amenities; }
}