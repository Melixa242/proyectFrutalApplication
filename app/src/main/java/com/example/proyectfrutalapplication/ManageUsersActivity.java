package com.example.proyectfrutalapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class ManageUsersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UserAdapter.UserActionListener {

    private DrawerLayout drawerLayout;
    private ListView usersListView;
    private Button addUserButton;

    private DatabaseHelper dbHelper;
    private UserAdapter userAdapter;
    private List<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        dbHelper = new DatabaseHelper(this);

        setupToolbarAndDrawer();
        initializeViews();
        loadUsers();
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
        usersListView = findViewById(R.id.usersListView);
        addUserButton = findViewById(R.id.addUserButton);
    }

    private void loadUsers() {
        usersList = dbHelper.getAllUsers();
        userAdapter = new UserAdapter(this, usersList, this);
        usersListView.setAdapter(userAdapter);
    }

    private void setupListeners() {
        addUserButton.setOnClickListener(v -> showAddUserDialog());
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.dialogNameEditText);
        EditText emailEditText = dialogView.findViewById(R.id.dialogEmailEditText);
        EditText phoneEditText = dialogView.findViewById(R.id.dialogPhoneEditText);
        EditText passwordEditText = dialogView.findViewById(R.id.dialogPasswordEditText);

        builder.setTitle("Agregar Usuario")
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (validateUserInput(name, email, phone, password)) {
                if (dbHelper.emailExists(email)) {
                    emailEditText.setError("Este email ya existe");
                    return;
                }

                long result = dbHelper.registerUser(name, email, phone, password);
                if (result > 0) {
                    Toast.makeText(this, "Usuario agregado", Toast.LENGTH_SHORT).show();
                    loadUsers();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Error al agregar usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onEditUser(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.dialogNameEditText);
        EditText emailEditText = dialogView.findViewById(R.id.dialogEmailEditText);
        EditText phoneEditText = dialogView.findViewById(R.id.dialogPhoneEditText);

        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhone());

        builder.setTitle("Editar Usuario")
                .setPositiveButton("Actualizar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = dbHelper.updateUser(user.getId(), name, email, phone);
            if (result) {
                Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                loadUsers();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Está seguro de eliminar a " + user.getName() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean result = dbHelper.deleteUser(user.getId());
                    if (result) {
                        Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onAssignRole(User user) {
        List<Role> allRoles = dbHelper.getAllRoles();
        List<Role> userRoles = dbHelper.getUserRoles(user.getId());

        String[] roleNames = new String[allRoles.size()];
        boolean[] checkedRoles = new boolean[allRoles.size()];

        for (int i = 0; i < allRoles.size(); i++) {
            roleNames[i] = allRoles.get(i).getName();

            // Verificar si el usuario tiene este rol
            for (Role userRole : userRoles) {
                if (userRole.getId() == allRoles.get(i).getId()) {
                    checkedRoles[i] = true;
                    break;
                }
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Asignar Roles a " + user.getName())
                .setMultiChoiceItems(roleNames, checkedRoles, (dialog, which, isChecked) -> {
                    Role role = allRoles.get(which);
                    if (isChecked) {
                        dbHelper.assignRoleToUser(user.getId(), role.getId());
                    } else {
                        dbHelper.removeRoleFromUser(user.getId(), role.getId());
                    }
                })
                .setPositiveButton("Cerrar", (dialog, which) -> {
                    Toast.makeText(this, "Roles actualizados", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private boolean validateUserInput(String name, String email, String phone, String password) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "Ingrese el teléfono", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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