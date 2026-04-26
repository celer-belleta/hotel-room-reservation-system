package model;

import java.sql.Date; // for year, month, and day only

public class Reservation {
    private int resId;
    private int guestId;
    private int roomId;
    private Date checkIn;
    private Date checkOut;
    private String status;
    private int packageId;

    public Reservation(int resId, int guestId, int roomId, Date checkIn, Date checkOut, String status, int packageId) {
        this.resId = resId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.packageId = packageId;
    }

    // Getters
    public int getResId() { return resId; }
    public int getGuestId() { return guestId; }
    public int getRoomId() { return roomId; }
    public Date getCheckIn() { return checkIn; }
    public Date getCheckOut() { return checkOut; }
    public String getStatus() { return status; }
    public int getPackageId() { return packageId; }

    // Setters
    public void setResId(int resId) { this.resId = resId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setCheckIn(Date checkIn) { this.checkIn = checkIn; }
    public void setCheckOut(Date checkOut) { this.checkOut = checkOut; }
    public void setStatus(String status) { this.status = status; }
    public void setPackageId(int packageId) { this.packageId = packageId; }
}