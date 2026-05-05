package model;

public class Package {
    private int packageId;
    private String packageName;
    private double price;
    private String description;

    public Package(int packageId, String packageName, double price, String description) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.price = price;
        this.description = description;
    }

    // GETTERS
    public int getPackageId() { return packageId; }
    public String getPackageName() { return packageName; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }

    // SETTERS
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
}