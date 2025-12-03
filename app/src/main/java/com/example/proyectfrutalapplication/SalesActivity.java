package com.example.proyectfrutalapplication;

import android.content.Intent;
import android.hardware.SensorAdditionalInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class SalesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private DrawerLayout drawerLayout;
    private ListView productsListView;
    private TextView cartCountTextView, cartTotalTextView;
    private FloatingActionButton viewCartButton;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ProductSaleAdapter productAdapter;
    private List<Product> productsList;


    public static List<CartItem> shoppingCart = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        setupToolbarAndDrawer();
        initializeViews();
        loadProducts();
        updateCartInfo();
        initShakeSensor();
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initializeViews() {
        productsListView = findViewById(R.id.productsListView);
        cartCountTextView = findViewById(R.id.cartCountTextView);
        cartTotalTextView = findViewById(R.id.cartTotalTextView);
        viewCartButton = findViewById(R.id.viewCartButton);

        viewCartButton.setOnClickListener(v -> {
            if (shoppingCart.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(SalesActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadProducts() {
        productsList = dbHelper.getAvailableProductsForSale();

        productAdapter = new ProductSaleAdapter(this, productsList,
                new ProductSaleAdapter.OnAddToCartListener() {
                    @Override
                    public void onAddToCart(Product product, double quantity, double price) {
                        addToCart(product, quantity, price);
                    }
                });

        productsListView.setAdapter(productAdapter);
    }

    private void addToCart(Product product, double quantity, double price) {
        // Verificar si el producto ya está en el carrito
        boolean found = false;
        for (CartItem item : shoppingCart) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            CartItem newItem = new CartItem(product, quantity, price);
            shoppingCart.add(newItem);
        }

        updateCartInfo();
        Toast.makeText(this, "Agregado al carrito", Toast.LENGTH_SHORT).show();
    }

    private void updateCartInfo() {
        int itemCount = shoppingCart.size();
        double total = 0;

        for (CartItem item : shoppingCart) {
            total += item.getSubtotal();
        }

        cartCountTextView.setText(String.valueOf(itemCount));
        cartTotalTextView.setText(String.format("Bs %.2f", total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartInfo();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, HomeActivity.class));
        } else if (id == R.id.nav_sales) {
            // Ya estamos aquí
        } else if (id == R.id.nav_my_sales) {
            startActivity(new Intent(this, MySalesActivity.class));
        } else if (id == R.id.nav_products) {
            startActivity(new Intent(this, ManageProductsActivity.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(this, ManageUsersActivity.class));
        } else if (id == R.id.nav_roles) {
            startActivity(new Intent(this, ManageRolesActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) /
                        diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    Toast.makeText(this, "🔄 Actualizando productos...",
                            Toast.LENGTH_SHORT).show();
                    loadProducts();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

    }
    private void initShakeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

}