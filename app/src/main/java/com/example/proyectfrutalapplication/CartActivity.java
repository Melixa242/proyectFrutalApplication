package com.example.proyectfrutalapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class CartActivity extends AppCompatActivity implements SensorEventListener {

    private ListView cartListView;
    private TextView totalTextView;
    private Button checkoutButton, clearCartButton;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private CartAdapter cartAdapter;

    // ========== SENSOR DE AGITADO ==========
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;
    private boolean isShakeDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        setupToolbar();
        initializeViews();
        setupShakeSensor(); // NUEVO: Inicializar sensor
        loadCart();
    }

    // ========== CONFIGURACIÓN DEL SENSOR ==========
    private void setupShakeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if (accelerometer != null) {
                Toast.makeText(this, "🤳 Agita el teléfono para vaciar el carrito",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activar sensor cuando la actividad esté visible
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desactivar sensor para ahorrar batería
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    onShakeDetected();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesario para este sensor
    }

    /**
     * Se ejecuta cuando se detecta agitado del teléfono
     */
    private void onShakeDetected() {
        // Evitar múltiples diálogos
        if (isShakeDialogShowing) {
            return;
        }

        // Verificar si hay items en el carrito
        if (!SalesActivity.shoppingCart.isEmpty()) {
            isShakeDialogShowing = true;

            new AlertDialog.Builder(this)
                    .setTitle("🗑️ Vaciar carrito por agitado")
                    .setMessage("Detectamos que agitaste el teléfono.\n\n" +
                            "¿Deseas vaciar el carrito?\n\n" +
                            "Items en carrito: " + SalesActivity.shoppingCart.size())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Sí, vaciar", (dialog, which) -> {
                        SalesActivity.shoppingCart.clear();
                        Toast.makeText(this, "🗑️ Carrito vaciado exitosamente",
                                Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad
                        isShakeDialogShowing = false;
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        isShakeDialogShowing = false;
                    })
                    .setOnDismissListener(dialog -> {
                        isShakeDialogShowing = false;
                    })
                    .show();

        } else {
            Toast.makeText(this, "El carrito ya está vacío", Toast.LENGTH_SHORT).show();
        }
    }

    // ========== RESTO DEL CÓDIGO ORIGINAL ==========

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Carrito de Compras");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        cartListView = findViewById(R.id.cartListView);
        totalTextView = findViewById(R.id.totalTextView);
        checkoutButton = findViewById(R.id.checkoutButton);
        clearCartButton = findViewById(R.id.clearCartButton);

        checkoutButton.setOnClickListener(v -> showCheckoutDialog());
        clearCartButton.setOnClickListener(v -> clearCart());
    }

    private void loadCart() {
        List<CartItem> cart = SalesActivity.shoppingCart;

        if (cart.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartAdapter = new CartAdapter(this, cart, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged() {
                updateTotal();
            }

            @Override
            public void onItemRemoved() {
                updateTotal();
                if (SalesActivity.shoppingCart.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Carrito vacío", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        cartListView.setAdapter(cartAdapter);
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : SalesActivity.shoppingCart) {
            total += item.getSubtotal();
        }
        totalTextView.setText(String.format("Total: Bs %.2f", total));
    }

    private void showCheckoutDialog() {
        final CharSequence[] options = {"Cliente Existente", "Nuevo Cliente", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione el Tipo de Cliente");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Cliente Existente")) {
                showExistingCustomerDialog();
            } else if (options[item].equals("Nuevo Cliente")) {
                showCustomerDataDialog(null);
            } else if (options[item].equals("Cancelar")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showExistingCustomerDialog() {
        List<Sale> customers = dbHelper.getUniqueCustomers();

        if (customers.isEmpty()) {
            Toast.makeText(this, "No hay clientes guardados. Por favor, registre uno nuevo.",
                    Toast.LENGTH_LONG).show();
            showCustomerDataDialog(null);
            return;
        }

        String[] customerNames = new String[customers.size()];
        for (int i = 0; i < customers.size(); i++) {
            customerNames[i] = customers.get(i).getCustomerName() + " (" +
                    customers.get(i).getCustomerPhone() + ")";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione un Cliente")
                .setItems(customerNames, (dialog, which) -> {
                    Sale selectedCustomer = customers.get(which);
                    showCustomerDataDialog(selectedCustomer);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showCustomerDataDialog(Sale existingCustomer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_checkout, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.customerNameEditText);
        EditText phoneEditText = dialogView.findViewById(R.id.customerPhoneEditText);
        EditText addressEditText = dialogView.findViewById(R.id.customerAddressEditText);
        EditText notesEditText = dialogView.findViewById(R.id.notesEditText);

        if (existingCustomer != null) {
            nameEditText.setText(existingCustomer.getCustomerName());
            phoneEditText.setText(existingCustomer.getCustomerPhone());
            addressEditText.setText(existingCustomer.getCustomerAddress());
        }

        builder.setTitle("Datos del Cliente")
                .setPositiveButton("Finalizar Venta", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String notes = notesEditText.getText().toString().trim();

            if (name.isEmpty()) {
                nameEditText.setError("Ingrese el nombre");
                nameEditText.requestFocus();
                return;
            }

            if (phone.isEmpty()) {
                phoneEditText.setError("Ingrese el teléfono");
                phoneEditText.requestFocus();
                return;
            }

            if (address.isEmpty()) {
                addressEditText.setError("Ingrese la dirección");
                addressEditText.requestFocus();
                return;
            }

            completeSale(name, phone, address, notes);
            dialog.dismiss();
        });
    }

    private void completeSale(String customerName, String customerPhone,
                              String customerAddress, String notes) {
        double total = 0;
        for (CartItem item : SalesActivity.shoppingCart) {
            total += item.getSubtotal();
        }

        Sale sale = new Sale();
        sale.setUserId(sessionManager.getUserId());
        sale.setCustomerName(customerName);
        sale.setCustomerPhone(customerPhone);
        sale.setCustomerAddress(customerAddress);
        sale.setTotalAmount(total);
        sale.setNotes(notes);

        long saleId = dbHelper.createSale(sale, SalesActivity.shoppingCart);

        if (saleId > 0) {
            Toast.makeText(this, "Venta registrada exitosamente", Toast.LENGTH_LONG).show();

            new AlertDialog.Builder(this)
                    .setTitle("Enviar Pedido")
                    .setMessage("¿Desea enviar el pedido por WhatsApp?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        sendWhatsApp(sale, customerPhone);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        clearCartAndFinish();
                    })
                    .show();
        } else {
            Toast.makeText(this, "Error al registrar la venta", Toast.LENGTH_LONG).show();
        }
    }

    private void sendWhatsApp(Sale sale, String phone) {
        StringBuilder message = new StringBuilder();
        message.append("🛒 *PEDIDO DE PULPAS*\n\n");
        message.append("👤 *Cliente:* ").append(sale.getCustomerName()).append("\n");
        message.append("📍 *Dirección:* ").append(sale.getCustomerAddress()).append("\n");
        message.append("📅 *Fecha:* ").append(sale.getSaleDate()).append("\n\n");
        message.append("📦 *PRODUCTOS:*\n");

        for (CartItem item : SalesActivity.shoppingCart) {
            message.append("• ").append(item.getProduct().getName()).append("\n");
            message.append("  Cantidad: ").append(item.getQuantity()).append(" ")
                    .append(item.getProduct().getUnit()).append("\n");
            message.append("  Precio: Bs ").append(String.format("%.2f", item.getUnitPrice()))
                    .append("\n");
            message.append("  Subtotal: Bs ").append(String.format("%.2f", item.getSubtotal()))
                    .append("\n\n");
        }

        message.append("💰 *TOTAL: Bs ").append(String.format("%.2f", sale.getTotalAmount()))
                .append("*\n\n");

        if (sale.getNotes() != null && !sale.getNotes().isEmpty()) {
            message.append("📝 *Notas:* ").append(sale.getNotes()).append("\n\n");
        }

        message.append("✨ *Pulpas de Frutas Frescas y Naturales*\n");
        message.append("¡Gracias por su compra!");

        try {
            String url = "https://wa.me/" + phone.replaceAll("[^0-9]", "") +
                    "?text=" + Uri.encode(message.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);

            clearCartAndFinish();
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir WhatsApp", Toast.LENGTH_SHORT).show();
            clearCartAndFinish();
        }
    }

    private void clearCart() {
        new AlertDialog.Builder(this)
                .setTitle("Vaciar Carrito")
                .setMessage("¿Está seguro de vaciar el carrito?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SalesActivity.shoppingCart.clear();
                    Toast.makeText(this, "Carrito vaciado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clearCartAndFinish() {
        SalesActivity.shoppingCart.clear();
        Intent intent = new Intent(CartActivity.this, MySalesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}