package model;

public class Room {
    private int id;
    private String roomNumber;
    private String type;
    private double price;
    private String status;
    private String amenities;
    private String packageType;
    private int maxGuest;

    public Room(int id, String roomNumber, String type, double price, String status,
                String amenities, String packageType, int maxGuest) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
        this.amenities = amenities;
        this.packageType = packageType;
        this.maxGuest = maxGuest;
    }

    public Room() {}

    // Getters
    public int getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
    public String getAmenities() { return amenities; }
    public String getPackageType() { return packageType; } // Added Getter
    public int getMaxGuest() { return maxGuest; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setPrice(double price) { this.price = price; }
    public void setAmenities(String amenities) { this.amenities = amenities; }
    public void setPackageType(String packageType) { this.packageType = packageType; }
    public void setMaxGuest(int maxGuest) { this.maxGuest = maxGuest; }
}