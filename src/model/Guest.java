package model;

public class Guest {
    private int guestId;
    private String name;
    private String contact;
    private String idNumber;
    private String username;
    private String password;

    public Guest(int guestId, String name, String contact, String idNumber, String username, String password) {
        this.guestId = guestId;
        this.name = name;
        this.contact = contact;
        this.idNumber = idNumber;
        this.username = username;
        this.password = password;
    }

    // Getters
    public int getGuestId() { return guestId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getIdNumber() { return idNumber; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}