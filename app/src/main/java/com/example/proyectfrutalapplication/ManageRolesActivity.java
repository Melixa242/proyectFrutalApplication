package com.example.proyectfrutalapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ManageRolesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ListView rolesListView;
    private Button addRoleButton;

    private DatabaseHelper dbHelper;
    private RoleAdapter roleAdapter;
    private List<Role> rolesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_roles);

        dbHelper = new DatabaseHelper(this);

        setupToolbarAndDrawer();
        initializeViews();
        loadRoles();
        setupListeners();
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
        rolesListView = findViewById(R.id.rolesListView);
        addRoleButton = findViewById(R.id.addRoleButton);
    }

    private void loadRoles() {
        rolesList = dbHelper.getAllRoles();
        roleAdapter = new RoleAdapter(this, rolesList, new RoleAdapter.RoleActionListener() {
            @Override
            public void onEditRole(Role role) {
                showEditRoleDialog(role);
            }

            @Override
            public void onDeleteRole(Role role) {
                showDeleteRoleDialog(role);
            }
        });
        rolesListView.setAdapter(roleAdapter);
    }

    private void setupListeners() {
        addRoleButton.setOnClickListener(v -> showAddRoleDialog());
    }

    private void showAddRoleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_role, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.roleNameEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.roleDescriptionEditText);

        builder.setTitle("Agregar Rol")
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (name.isEmpty()) {
                nameEditText.setError("Ingrese el nombre del rol");
                nameEditText.requestFocus();
                return;
            }

            if (description.isEmpty()) {
                descriptionEditText.setError("Ingrese la descripción");
                descriptionEditText.requestFocus();
                return;
            }

            long result = dbHelper.addRole(name, description);
            if (result > 0) {
                Toast.makeText(this, "Rol agregado", Toast.LENGTH_SHORT).show();
                loadRoles();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error: El rol ya existe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditRoleDialog(Role role) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_role, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.roleNameEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.roleDescriptionEditText);

        nameEditText.setText(role.getName());
        descriptionEditText.setText(role.getDescription());

        builder.setTitle("Editar Rol")
                .setPositiveButton("Actualizar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = dbHelper.updateRole(role.getId(), name, description);
            if (result) {
                Toast.makeText(this, "Rol actualizado", Toast.LENGTH_SHORT).show();
                loadRoles();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteRoleDialog(Role role) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Rol")
                .setMessage("¿Está seguro de eliminar el rol '" + role.getName() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean result = dbHelper.deleteRole(role.getId());
                    if (result) {
                        Toast.makeText(this, "Rol eliminado", Toast.LENGTH_SHORT).show();
                        loadRoles();
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Manejar navegación
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
}