package com.example.proyectfrutalapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProductSaleAdapter extends ArrayAdapter<Product> {

    private Context context;
    private List<Product> products;
    private OnAddToCartListener listener;

    public interface OnAddToCartListener {
        void onAddToCart(Product product, double quantity, double price);
    }

    public ProductSaleAdapter(Context context, List<Product> products, OnAddToCartListener listener) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product_sale, parent, false);
        }

        Product product = products.get(position);

        ImageView productImageView = convertView.findViewById(R.id.productImageView);
        TextView nameTextView = convertView.findViewById(R.id.productNameTextView);
        TextView pricesTextView = convertView.findViewById(R.id.productPricesTextView);
        TextView stockTextView = convertView.findViewById(R.id.productStockTextView);
        Button addToCartButton = convertView.findViewById(R.id.addToCartButton);

        nameTextView.setText(product.getName());

        // Mostrar ambos precios
        pricesTextView.setText(String.format(
                "Mayor: Bs %.2f | Menor: Bs %.2f / %s",
                product.getWholesalePrice(),
                product.getRetailPrice(),
                product.getUnit()
        ));

        stockTextView.setText(String.format("Stock: %.2f %s", product.getQuantity(), product.getUnit()));

        addToCartButton.setOnClickListener(v -> showAddToCartDialog(product));

        return convertView;
    }

    private void showAddToCartDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_to_cart, null);
        builder.setView(dialogView);

        TextView productNameTextView = dialogView.findViewById(R.id.dialogProductNameTextView);
        RadioGroup priceTypeRadioGroup = dialogView.findViewById(R.id.priceTypeRadioGroup);
        RadioButton wholesaleRadioButton = dialogView.findViewById(R.id.wholesaleRadioButton);
        RadioButton retailRadioButton = dialogView.findViewById(R.id.retailRadioButton);
        TextView wholesalePriceTextView = dialogView.findViewById(R.id.wholesalePriceTextView);
        TextView retailPriceTextView = dialogView.findViewById(R.id.retailPriceTextView);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        EditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        TextView totalTextView = dialogView.findViewById(R.id.totalTextView);

        productNameTextView.setText(product.getName());
        wholesalePriceTextView.setText(String.format("Bs %.2f", product.getWholesalePrice()));
        retailPriceTextView.setText(String.format("Bs %.2f", product.getRetailPrice()));

        // Precio inicial por mayor (checked por defecto)
        priceEditText.setText(String.valueOf(product.getWholesalePrice()));

        // Cambiar precio según tipo seleccionado
        priceTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.wholesaleRadioButton) {
                priceEditText.setText(String.valueOf(product.getWholesalePrice()));
            } else {
                priceEditText.setText(String.valueOf(product.getRetailPrice()));
            }
            updateTotal(quantityEditText, priceEditText, totalTextView);
        });

        // Calcular total en tiempo real
        quantityEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotal(quantityEditText, priceEditText, totalTextView);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        priceEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotal(quantityEditText, priceEditText, totalTextView);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        builder.setTitle("Agregar al Carrito")
                .setPositiveButton("Agregar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String quantityStr = quantityEditText.getText().toString().trim();
            String priceStr = priceEditText.getText().toString().trim();

            if (quantityStr.isEmpty()) {
                quantityEditText.setError("Ingrese cantidad");
                return;
            }

            if (priceStr.isEmpty()) {
                priceEditText.setError("Ingrese precio");
                return;
            }

            double quantity = Double.parseDouble(quantityStr);
            double price = Double.parseDouble(priceStr);

            if (quantity <= 0) {
                quantityEditText.setError("Cantidad debe ser mayor a 0");
                return;
            }

            if (quantity > product.getQuantity()) {
                quantityEditText.setError("Stock insuficiente");
                return;
            }

            if (price <= 0) {
                priceEditText.setError("Precio debe ser mayor a 0");
                return;
            }

            if (listener != null) {
                listener.onAddToCart(product, quantity, price);
            }

            dialog.dismiss();
        });
    }

    private void updateTotal(EditText quantityEditText, EditText priceEditText, TextView totalTextView) {
        try {
            double quantity = Double.parseDouble(quantityEditText.getText().toString());
            double price = Double.parseDouble(priceEditText.getText().toString());
            double total = quantity * price;
            totalTextView.setText(String.format("Total: Bs %.2f", total));
        } catch (NumberFormatException e) {
            totalTextView.setText("Total: Bs 0.00");
        }
    }
}
