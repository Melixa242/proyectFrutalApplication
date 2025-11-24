package com.example.proyectfrutalapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Context context;
    private List<Product> products;
    private ProductActionListener listener;

    public interface ProductActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
        void onSendWhatsApp(Product product);
    }

    public ProductAdapter(Context context, List<Product> products, ProductActionListener listener) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }

        Product product = products.get(position);

        TextView nameTextView = convertView.findViewById(R.id.productNameTextView);
        TextView quantityTextView = convertView.findViewById(R.id.productQuantityTextView);
        TextView datesTextView = convertView.findViewById(R.id.productDatesTextView);
        TextView notesTextView = convertView.findViewById(R.id.productNotesTextView);
        ImageButton editButton = convertView.findViewById(R.id.editProductButton);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteProductButton);
        ImageButton whatsappButton = convertView.findViewById(R.id.whatsappProductButton);

        nameTextView.setText(product.getName());
        quantityTextView.setText(product.getQuantity() + " " + product.getUnit() +
                " - Fracción: " + product.getFraction());
        datesTextView.setText("Prod: " + product.getProductionDate() +
                " | Venc: " + product.getExpiryDate());

        if (product.getNotes() != null && !product.getNotes().isEmpty()) {
            notesTextView.setVisibility(View.VISIBLE);
            notesTextView.setText(product.getNotes());
        } else {
            notesTextView.setVisibility(View.GONE);
        }

        editButton.setOnClickListener(v -> {
            if (listener != null) listener.onEditProduct(product);
        });

        deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteProduct(product);
        });

        whatsappButton.setOnClickListener(v -> {
            if (listener != null) listener.onSendWhatsApp(product);
        });

        return convertView;
    }
}