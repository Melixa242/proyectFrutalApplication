package com.example.proyectfrutalapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SaleAdapter extends ArrayAdapter<Sale> {

    private Context context;
    private List<Sale> sales;
    private OnSaleActionListener listener;

    public interface OnSaleActionListener {
        void onViewDetails(Sale sale);
        void onChangeStatus(Sale sale);
        void onResendWhatsApp(Sale sale);
        void onDeleteSale(Sale sale);
    }

    public SaleAdapter(Context context, List<Sale> sales, OnSaleActionListener listener) {
        super(context, 0, sales);
        this.context = context;
        this.sales = sales;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sale, parent, false);
        }

        Sale sale = sales.get(position);

        TextView idTextView = convertView.findViewById(R.id.saleIdTextView);
        TextView customerTextView = convertView.findViewById(R.id.saleCustomerTextView);
        TextView dateTextView = convertView.findViewById(R.id.saleDateTextView);
        TextView totalTextView = convertView.findViewById(R.id.saleTotalTextView);
        TextView statusTextView = convertView.findViewById(R.id.saleStatusTextView);
        ImageButton detailsButton = convertView.findViewById(R.id.viewDetailsButton);
        ImageButton statusButton = convertView.findViewById(R.id.changeStatusButton);
        ImageButton whatsappButton = convertView.findViewById(R.id.resendWhatsAppButton);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteSaleButton);

        idTextView.setText("Venta #" + sale.getId());
        customerTextView.setText(sale.getCustomerName());
        dateTextView.setText(sale.getSaleDate());
        totalTextView.setText(String.format("Bs %.2f", sale.getTotalAmount()));
        statusTextView.setText(sale.getStatus());

        // Color según estado
        switch (sale.getStatus()) {
            case "Pendiente":
                statusTextView.setTextColor(Color.parseColor("#FF9800"));
                break;
            case "Completada":
                statusTextView.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Cancelada":
                statusTextView.setTextColor(Color.parseColor("#F44336"));
                break;
        }

        detailsButton.setOnClickListener(v -> {
            if (listener != null) listener.onViewDetails(sale);
        });

        statusButton.setOnClickListener(v -> {
            if (listener != null) listener.onChangeStatus(sale);
        });

        whatsappButton.setOnClickListener(v -> {
            if (listener != null) listener.onResendWhatsApp(sale);
        });

        deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteSale(sale);
        });

        return convertView;
    }
}