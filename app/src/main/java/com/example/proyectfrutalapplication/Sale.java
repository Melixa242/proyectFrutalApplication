package com.example.proyectfrutalapplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Sale {
    private int id;
    private int userId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private double totalAmount;
    private String saleDate;
    private String status; // Pendiente, Completada, Cancelada
    private String notes;

    public Sale() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.saleDate = sdf.format(new Date());
        this.status = "Pendiente";
    }

    public Sale(int id, int userId, String customerName, String customerPhone,
                String customerAddress, double totalAmount, String saleDate,
                String status, String notes) {
        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
        this.status = status;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getSaleDate() { return saleDate; }
    public void setSaleDate(String saleDate) { this.saleDate = saleDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}