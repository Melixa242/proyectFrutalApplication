package com.example.proyectfrutalapplication;

public class CartItem {

    private Product product;
    private double quantity;
    private double unitPrice;
    private double subtotal;

    public CartItem(Product product, double quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
        this.subtotal = quantity * unitPrice;
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    public double getSubtotal() { return subtotal; }

    public void incrementQuantity() {
        this.quantity++;
        this.subtotal = quantity * unitPrice;
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            this.subtotal = quantity * unitPrice;
        }
    }
}
