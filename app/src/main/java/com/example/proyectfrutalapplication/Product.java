package com.example.proyectfrutalapplication;

class Product {
    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String fraction;
    private String productionDate;
    private String expiryDate;
    private String notes;

    public Product() {}

    public Product(int id, String name, double quantity, String unit, String fraction,
                   String productionDate, String expiryDate, String notes) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.fraction = fraction;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getFraction() { return fraction; }
    public void setFraction(String fraction) { this.fraction = fraction; }

    public String getProductionDate() { return productionDate; }
    public void setProductionDate(String productionDate) { this.productionDate = productionDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return name + " - " + quantity + " " + unit;
    }
}