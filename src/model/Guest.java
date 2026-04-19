package model;

public class Guest {
    private int guestId;
    private String name;
    private String contact;
    private String idNumber;

    public Guest(int guestId, String name, String contact, String idNumber) {
        this.guestId = guestId;
        this.name = name;
        this.contact = contact;
        this.idNumber = idNumber;
    }

    // Getters
    public int getGuestId() { return guestId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getIdNumber() { return idNumber; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
}