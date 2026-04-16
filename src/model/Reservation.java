package model;

import java.sql.Date;

public class Reservation {
    private int resId;
    private int guestId;
    private int roomId;
    private Date checkIn;
    private Date checkOut;
    private String status;

    public Reservation(int resId, int guestId, int roomId, Date checkIn, Date checkOut, String status) {
        this.resId = resId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
    }

    // Getters and Setters
    public int getResId() { return resId; }
    public int getGuestId() { return guestId; }
    public int getRoomId() { return roomId; }
    public Date getCheckIn() { return checkIn; }
    public Date getCheckOut() { return checkOut; }
    public String getStatus() { return status; }
}