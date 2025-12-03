package com.example.proyectfrutalapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MySalesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ListView salesListView;

    private DatabaseHelper dbHelper;
    private SaleAdapter saleAdapter;
    private List<Sale> salesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sales);

        dbHelper = new DatabaseHelper(this);

        setupToolbarAndDrawer();
        initializeViews();
        loadSales();
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
        salesListView = findViewById(R.id.salesListView);
    }

    private void loadSales() {
        salesList = dbHelper.getAllSales();

        saleAdapter = new SaleAdapter(this, salesList, new SaleAdapter.OnSaleActionListener() {
            @Override
            public void onViewDetails(Sale sale) {
                showSaleDetails(sale);
            }

            @Override
            public void onChangeStatus(Sale sale) {
                showStatusDialog(sale);
            }

            @Override
            public void onResendWhatsApp(Sale sale) {
                resendWhatsApp(sale);
            }

            @Override
            public void onDeleteSale(Sale sale) {
                deleteSale(sale);
            }
        });

        salesListView.setAdapter(saleAdapter);
    }

    private void showSaleDetails(Sale sale) {
        List<SaleDetail> details = dbHelper.getSaleDetails(sale.getId());

        StringBuilder message = new StringBuilder();
        message.append("DETALLES DE VENTA\n\n");
        message.append("Cliente: ").append(sale.getCustomerName()).append("\n");
        message.append("Teléfono: ").append(sale.getCustomerPhone()).append("\n");
        message.append("Dirección: ").append(sale.getCustomerAddress()).append("\n");
        message.append("Fecha: ").append(sale.getSaleDate()).append("\n");
        message.append("Estado: ").append(sale.getStatus()).append("\n\n");
        message.append("PRODUCTOS:\n");

        for (SaleDetail detail : details) {
            message.append("• ").append(detail.getProductName()).append("\n");
            message.append("  Cant: ").append(detail.getQuantity()).append(" ")
                    .append(detail.getUnit()).append("\n");
            message.append("  Precio: Bs ").append(String.format("%.2f", detail.getUnitPrice())).append("\n");
            message.append("  Subtotal: Bs ").append(String.format("%.2f", detail.getSubtotal())).append("\n\n");
        }

        message.append("TOTAL: Bs ").append(String.format("%.2f", sale.getTotalAmount()));

        if (sale.getNotes() != null && !sale.getNotes().isEmpty()) {
            message.append("\n\nNotas: ").append(sale.getNotes());
        }

        new AlertDialog.Builder(this)
                .setTitle("Detalle de Venta #" + sale.getId())
                .setMessage(message.toString())
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showStatusDialog(Sale sale) {
        String[] statuses = {"Pendiente", "Completada", "Cancelada"};
        int currentIndex = 0;

        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(sale.getStatus())) {
                currentIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Cambiar Estado")
                .setSingleChoiceItems(statuses, currentIndex, null)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    String newStatus = statuses[selectedPosition];

                    boolean result = dbHelper.updateSaleStatus(sale.getId(), newStatus);
                    if (result) {
                        Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                        loadSales();
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void resendWhatsApp(Sale sale) {
        List<SaleDetail> details = dbHelper.getSaleDetails(sale.getId());

        StringBuilder message = new StringBuilder();
        message.append("🛒 *PEDIDO DE PULPAS*\n\n");
        message.append("👤 *Cliente:* ").append(sale.getCustomerName()).append("\n");
        message.append("📍 *Dirección:* ").append(sale.getCustomerAddress()).append("\n");
        message.append("📅 *Fecha:* ").append(sale.getSaleDate()).append("\n\n");
        message.append("📦 *PRODUCTOS:*\n");

        for (SaleDetail detail : details) {
            message.append("• ").append(detail.getProductName()).append("\n");
            message.append("  Cantidad: ").append(detail.getQuantity()).append(" ")
                    .append(detail.getUnit()).append("\n");
            message.append("  Precio: Bs ").append(String.format("%.2f", detail.getUnitPrice()))
                    .append("\n");
            message.append("  Subtotal: Bs ").append(String.format("%.2f", detail.getSubtotal()))
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
            String phone = sale.getCustomerPhone().replaceAll("[^0-9]", "");
            String url = "https://wa.me/" + phone + "?text=" + Uri.encode(message.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSale(Sale sale) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Venta")
                .setMessage("¿Está seguro de eliminar esta venta?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean result = dbHelper.deleteSale(sale.getId());
                    if (result) {
                        Toast.makeText(this, "Venta eliminada", Toast.LENGTH_SHORT).show();
                        loadSales();
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            startActivity(new Intent(this, HomeActivity.class));
        } else if (id == R.id.nav_sales) {
            startActivity(new Intent(this, SalesActivity.class));
        } else if (id == R.id.nav_my_sales) {
            // Ya estamos aquí
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSales();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}