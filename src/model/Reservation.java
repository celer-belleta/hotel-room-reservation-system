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
    private double totalAmount;
    private double amountPaid;

    private String managedBy;

    private String timeIn;
    private String timeOut;

    private double damageFee;
    private double damagePaid;

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

    //
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }


    //
    public double getTotalAmount() {
        return totalAmount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    //
    public double getRemainingBalance() {
        return (totalAmount + damageFee) - (amountPaid + damagePaid);
    }

    //
    public String getManagedBy() { return managedBy; }
    public void setManagedBy(String managedBy) { this.managedBy = managedBy; }

    //
    public String getTimeIn() { return timeIn; }
    public void setTimeIn(String timeIn) { this.timeIn = timeIn; }

    public String getTimeOut() { return timeOut; }
    public void setTimeOut(String timeOut) { this.timeOut = timeOut; }

    //
    public double getDamageFee() { return damageFee; }
    public void setDamageFee(double damageFee) { this.damageFee = damageFee; }
    public double getDamagePaid() { return damagePaid; }
    public void setDamagePaid(double damagePaid) { this.damagePaid = damagePaid; }

    //
    public long getNights() {
        if (checkIn == null || checkOut == null) return 0;

        // Calculate the difference in milliseconds
        long diff = checkOut.getTime() - checkIn.getTime();

        // Convert to days
        long nights = java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS);

        // If the guest stays for a few hours or the same day, it counts as 1 night
        return (nights <= 0) ? 1 : nights;
    }
}