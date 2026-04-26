package model;

import java.sql.Timestamp;

public class Payment {
    private int paymentId;
    private int resId;
    private double amountPaid;
    private Timestamp paymentDate;
    private String paymentMethod; // Cash or Card
    private String paymentType;   // Down Payment or Full Payment
    private String invoiceNumber;

    //private double totalAmountDue;
    //private double discountAmount;

    public Payment(int paymentId, int resId, double amountPaid, Timestamp paymentDate,
                   String paymentMethod, String paymentType, String invoiceNumber) {
        this.paymentId = paymentId;
        this.resId = resId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.invoiceNumber = invoiceNumber;
    }

    // Getters
    public int getPaymentId() { return paymentId; }
    public int getResId() { return resId; }
    public double getAmountPaid() { return amountPaid; }
    public Timestamp getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentType() { return paymentType; }
    public String getInvoiceNumber() { return invoiceNumber; }
}