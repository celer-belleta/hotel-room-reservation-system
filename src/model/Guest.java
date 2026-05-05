package model;

public class Guest {
    private int guestId;
    private String firstName;
    private String lastName;
    private String contact;
    private String idType;
    private String idNumber;
    private String username;
    private String password;

    public Guest(int guestId, String firstName, String lastName, String contact, String idType, String idNumber, String username, String password) {
        this.guestId = guestId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.idType = idType;
        this.idNumber = idNumber;
        this.username = username;
        this.password = password;
    }

    // Getters
    public int getGuestId() { return guestId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getContact() { return contact; }
    public String getIdType() { return idType; }
    public String getIdNumber() { return idNumber; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setContact(String contact) { this.contact = contact; }
    public void setIdType(String idType) { this.idType = idType; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}