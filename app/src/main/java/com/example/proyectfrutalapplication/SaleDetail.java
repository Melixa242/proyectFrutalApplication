package com.example.proyectfrutalapplication;

public class SaleDetail {
    private int id;
    private int saleId;
    private int productId;
    private String productName;
    private double quantity;
    private String unit;
    private double unitPrice;
    private double subtotal;

    public SaleDetail() {}

    public SaleDetail(int id, int saleId, int productId, String productName,
                      double quantity, String unit, double unitPrice, double subtotal) {
        this.id = id;
        this.saleId = saleId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
