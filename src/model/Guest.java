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

    // GETTERS
    public int getGuestId() { return guestId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getIdNumber() { return idNumber; }
}