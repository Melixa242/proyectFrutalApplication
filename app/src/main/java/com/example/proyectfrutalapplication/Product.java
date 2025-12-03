package com.example.proyectfrutalapplication;

public class Product {
    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String fraction;
    private String productionDate;
    private String expiryDate;
    private String notes;
    private double wholesalePrice; // PRECIO POR MAYOR
    private double retailPrice;    // PRECIO POR MENOR

    public Product() {}

    public Product(int id, String name, double quantity, String unit, String fraction,
                   String productionDate, String expiryDate, String notes,
                   double wholesalePrice, double retailPrice) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.fraction = fraction;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
        this.notes = notes;
        this.wholesalePrice = wholesalePrice;
        this.retailPrice = retailPrice;
    }

    // Constructor de compatibilidad (por si tienes código antiguo)
    public Product(int id, String name, double quantity, String unit, String fraction,
                   String productionDate, String expiryDate, String notes) {
        this(id, name, quantity, unit, fraction, productionDate, expiryDate, notes, 0.0, 0.0);
    }

    // Getters y Setters
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

    public double getWholesalePrice() { return wholesalePrice; }
    public void setWholesalePrice(double wholesalePrice) { this.wholesalePrice = wholesalePrice; }

    public double getRetailPrice() { return retailPrice; }
    public void setRetailPrice(double retailPrice) { this.retailPrice = retailPrice; }

    // Método de compatibilidad para código antiguo
    @Deprecated
    public double getPrice() { return retailPrice; }

    @Deprecated
    public void setPrice(double price) { this.retailPrice = price; }

    @Override
    public String toString() {
        return name + " - " + quantity + " " + unit;
    }
}