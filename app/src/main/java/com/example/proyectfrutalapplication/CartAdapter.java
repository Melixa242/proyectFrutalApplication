package com.example.proyectfrutalapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CartAdapter extends ArrayAdapter<CartItem> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onQuantityChanged();
        void onItemRemoved();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangeListener listener) {
        super(context, 0, cartItems);
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }

        CartItem item = cartItems.get(position);

        TextView nameTextView = convertView.findViewById(R.id.cartItemNameTextView);
        TextView priceTextView = convertView.findViewById(R.id.cartItemPriceTextView);
        TextView quantityTextView = convertView.findViewById(R.id.cartItemQuantityTextView);
        TextView subtotalTextView = convertView.findViewById(R.id.cartItemSubtotalTextView);
        ImageButton decreaseButton = convertView.findViewById(R.id.decreaseQuantityButton);
        ImageButton increaseButton = convertView.findViewById(R.id.increaseQuantityButton);
        ImageButton removeButton = convertView.findViewById(R.id.removeItemButton);

        nameTextView.setText(item.getProduct().getName());
        priceTextView.setText(String.format("Bs %.2f / %s",
                item.getUnitPrice(), item.getProduct().getUnit()));
        quantityTextView.setText(String.format("%.2f", item.getQuantity()));
        subtotalTextView.setText(String.format("Bs %.2f", item.getSubtotal()));

        decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.decrementQuantity();
                notifyDataSetChanged();
                if (listener != null) listener.onQuantityChanged();
            }
        });

        increaseButton.setOnClickListener(v -> {
            // Verificar stock disponible
            if (item.getQuantity() < item.getProduct().getQuantity()) {
                item.incrementQuantity();
                notifyDataSetChanged();
                if (listener != null) listener.onQuantityChanged();
            }
        });

        removeButton.setOnClickListener(v -> {
            cartItems.remove(position);
            notifyDataSetChanged();
            if (listener != null) listener.onItemRemoved();
        });

        return convertView;
    }
}