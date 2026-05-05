package model;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String role;

    public User(int id, String firstName, String lastName, String username, String password, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // GETTERS
    public int getId() {
        return id;
    }
    public String getFirstName() { return firstName; }
    public String getLastName() {
        return lastName;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() { return password; }
    public String getRole() {
        return role;
    }

    // SETTERS
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}