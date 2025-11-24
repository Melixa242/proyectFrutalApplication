package com.example.proyectfrutalapplication;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView welcomeTextView, userNameTextView, userEmailTextView;
    private TextView totalUsersTextView, totalProductsTextView, totalRolesTextView;
    private CardView usersCard, productsCard, rolesCard;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Verificar sesión
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        setupToolbarAndDrawer();
        initializeViews();
        loadUserInfo();
        loadStatistics();
        setupCardListeners();
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
        welcomeTextView = findViewById(R.id.welcomeTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        totalUsersTextView = findViewById(R.id.totalUsersTextView);
        totalProductsTextView = findViewById(R.id.totalProductsTextView);
        totalRolesTextView = findViewById(R.id.totalRolesTextView);
        usersCard = findViewById(R.id.usersCard);
        productsCard = findViewById(R.id.productsCard);
        rolesCard = findViewById(R.id.rolesCard);
    }

    private void loadUserInfo() {
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();

        welcomeTextView.setText("¡Bienvenido!");
        userNameTextView.setText(userName);
        userEmailTextView.setText(userEmail);
    }

    private void loadStatistics() {
        List<User> users = dbHelper.getAllUsers();
        List<Product> products = dbHelper.getAllProducts();
        List<Role> roles = dbHelper.getAllRoles();

        totalUsersTextView.setText(String.valueOf(users.size()));
        totalProductsTextView.setText(String.valueOf(products.size()));
        totalRolesTextView.setText(String.valueOf(roles.size()));
    }

    private void setupCardListeners() {
        usersCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, activity_contact.class);
            startActivity(intent);
        });

        productsCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ManageProductsActivity.class);
            startActivity(intent);
        });

        rolesCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ManageRolesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Ya estamos en home
        } else if (id == R.id.nav_products) {
            startActivity(new Intent(this, ManageProductsActivity.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(this, ManageUsersActivity.class));
        } else if (id == R.id.nav_roles) {
            startActivity(new Intent(this, ManageRolesActivity.class));

        } else if (id == R.id.nav_logout) {
            showLogoutDialog();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro de cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    sessionManager.logoutUser();
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(HomeActivity.this, loginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics(); // Actualizar estadísticas al volver
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Mostrar diálogo para salir de la app
            new AlertDialog.Builder(this)
                    .setTitle("Salir")
                    .setMessage("¿Desea salir de la aplicación?")
                    .setPositiveButton("Sí", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}