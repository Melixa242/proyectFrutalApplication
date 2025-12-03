package com.example.proyectfrutalapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ManageClientesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewClientes;
    private ClienteAdapter clienteAdapter;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fabAddCliente;
    private LinearLayout llEmptyState;
    private TextView tvTotalClientes;
    private EditText etSearchCliente;
    private Button btnFilterClientes;

    private List<Cliente> allClientes;
    private List<Cliente> filteredClientes;
    private boolean showOnlyActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_clientes);

        databaseHelper = new DatabaseHelper(this);

        // Inicializar vistas
        recyclerViewClientes = findViewById(R.id.recyclerViewClientes);
        fabAddCliente = findViewById(R.id.fabAddCliente);
        llEmptyState = findViewById(R.id.tvEmptyStateClientes); // Corregido
        tvTotalClientes = findViewById(R.id.tvTotalClientes);
        etSearchCliente = findViewById(R.id.etSearchCliente);
        btnFilterClientes = findViewById(R.id.btnFilterClientes);

        // Configurar RecyclerView
        recyclerViewClientes.setLayoutManager(new LinearLayoutManager(this));

        // Cargar clientes
        loadClientes();

        // Botón agregar cliente
        fabAddCliente.setOnClickListener(v -> showAddClienteDialog());

        // Búsqueda en tiempo real
        etSearchCliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClientes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Botón de filtros
        btnFilterClientes.setOnClickListener(v -> showFilterDialog());
    }

    private void loadClientes() {
        allClientes = databaseHelper.getAllClientes();
        filteredClientes = new ArrayList<>(allClientes);
        updateUI();
    }

    private void filterClientes(String searchText) {
        filteredClientes.clear();

        if (TextUtils.isEmpty(searchText)) {
            filteredClientes.addAll(allClientes);
        } else {
            String searchLower = searchText.toLowerCase();
            for (Cliente cliente : allClientes) {
                if (cliente.getNombre().toLowerCase().contains(searchLower) ||
                        (cliente.getTelefono() != null && cliente.getTelefono().contains(searchText)) ||
                        (cliente.getEmail() != null && cliente.getEmail().toLowerCase().contains(searchLower))) {
                    filteredClientes.add(cliente);
                }
            }
        }

        // Aplicar filtro de activos si está habilitado
        if (showOnlyActive) {
            List<Cliente> temp = new ArrayList<>();
            for (Cliente c : filteredClientes) {
                if (c.isActivo()) {
                    temp.add(c);
                }
            }
            filteredClientes = temp;
        }

        updateUI();
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_filter_clientes, null);

        CheckBox cbOnlyActive = dialogView.findViewById(R.id.cbOnlyActive);
        cbOnlyActive.setChecked(showOnlyActive);

        builder.setView(dialogView)
                .setTitle("Filtrar Clientes")
                .setPositiveButton("Aplicar", (dialog, which) -> {
                    showOnlyActive = cbOnlyActive.isChecked();
                    filterClientes(etSearchCliente.getText().toString());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void updateUI() {
        if (filteredClientes.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE);
            recyclerViewClientes.setVisibility(View.GONE);
            tvTotalClientes.setText("0 clientes");
        } else {
            llEmptyState.setVisibility(View.GONE);
            recyclerViewClientes.setVisibility(View.VISIBLE);
            tvTotalClientes.setText(filteredClientes.size() + " cliente(s)");

            clienteAdapter = new ClienteAdapter(filteredClientes, this::onClienteClick);
            recyclerViewClientes.setAdapter(clienteAdapter);
        }
    }

    private void onClienteClick(Cliente cliente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opciones de Cliente");

        String[] options;
        if (cliente.isActivo()) {
            options = new String[]{"Ver Detalles", "Editar", "Desactivar", "Eliminar"};
        } else {
            options = new String[]{"Ver Detalles", "Editar", "Activar", "Eliminar"};
        }

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showClienteDetails(cliente);
                    break;
                case 1:
                    showEditClienteDialog(cliente);
                    break;
                case 2:
                    toggleClienteStatus(cliente);
                    break;
                case 3:
                    confirmDeleteCliente(cliente);
                    break;
            }
        });
        builder.show();
    }

    private void showClienteDetails(Cliente cliente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_cliente_details, null);

        TextView tvNombre = dialogView.findViewById(R.id.tvDetailNombre);
        TextView tvTelefono = dialogView.findViewById(R.id.tvDetailTelefono);
        TextView tvDireccion = dialogView.findViewById(R.id.tvDetailDireccion);
        TextView tvEmail = dialogView.findViewById(R.id.tvDetailEmail);
        TextView tvNotas = dialogView.findViewById(R.id.tvDetailNotas);
        TextView tvEstado = dialogView.findViewById(R.id.tvDetailEstado);

        tvNombre.setText(cliente.getNombre());
        tvTelefono.setText(cliente.getTelefono() != null ? cliente.getTelefono() : "No especificado");
        tvDireccion.setText(cliente.getDireccion() != null ? cliente.getDireccion() : "No especificado");
        tvEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "No especificado");
        tvNotas.setText(cliente.getNotas() != null && !cliente.getNotas().isEmpty() ?
                cliente.getNotas() : "Sin notas");
        tvEstado.setText(cliente.isActivo() ? "ACTIVO" : "INACTIVO");
        tvEstado.setTextColor(cliente.isActivo() ?
                getResources().getColor(android.R.color.holo_green_dark) :
                getResources().getColor(android.R.color.holo_red_dark));

        builder.setView(dialogView)
                .setTitle("Detalles del Cliente")
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showAddClienteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_cliente, null);

        EditText etNombre = dialogView.findViewById(R.id.etClienteNombre);
        EditText etTelefono = dialogView.findViewById(R.id.etClienteTelefono);
        EditText etDireccion = dialogView.findViewById(R.id.etClienteDireccion);
        EditText etEmail = dialogView.findViewById(R.id.etClienteEmail);
        EditText etNotas = dialogView.findViewById(R.id.etClienteNotas);

        builder.setView(dialogView)
                .setTitle("Agregar Cliente")
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String notas = etNotas.getText().toString().trim();

            // Validaciones
            if (TextUtils.isEmpty(nombre)) {
                etNombre.setError("El nombre es requerido");
                etNombre.requestFocus();
                return;
            }

            if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
                etEmail.setError("Email inválido");
                etEmail.requestFocus();
                return;
            }

            if (!TextUtils.isEmpty(email) && databaseHelper.clienteEmailExists(email)) {
                etEmail.setError("Este email ya está registrado");
                etEmail.requestFocus();
                return;
            }

            Cliente cliente = new Cliente(nombre, telefono, direccion, email, notas);
            long result = databaseHelper.addCliente(cliente);

            if (result > 0) {
                Toast.makeText(this, "Cliente agregado correctamente",
                        Toast.LENGTH_SHORT).show();
                loadClientes();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error al agregar cliente",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditClienteDialog(Cliente cliente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_cliente, null);

        EditText etNombre = dialogView.findViewById(R.id.etClienteNombre);
        EditText etTelefono = dialogView.findViewById(R.id.etClienteTelefono);
        EditText etDireccion = dialogView.findViewById(R.id.etClienteDireccion);
        EditText etEmail = dialogView.findViewById(R.id.etClienteEmail);
        EditText etNotas = dialogView.findViewById(R.id.etClienteNotas);

        // Llenar datos actuales
        etNombre.setText(cliente.getNombre());
        etTelefono.setText(cliente.getTelefono());
        etDireccion.setText(cliente.getDireccion());
        etEmail.setText(cliente.getEmail());
        etNotas.setText(cliente.getNotas());

        builder.setView(dialogView)
                .setTitle("Editar Cliente")
                .setPositiveButton("Actualizar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        String originalEmail = cliente.getEmail();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();
            String direccion = etDireccion.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String notas = etNotas.getText().toString().trim();

            // Validaciones
            if (TextUtils.isEmpty(nombre)) {
                etNombre.setError("El nombre es requerido");
                etNombre.requestFocus();
                return;
            }

            if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
                etEmail.setError("Email inválido");
                etEmail.requestFocus();
                return;
            }

            // Verificar email único solo si cambió
            if (!email.equals(originalEmail) && !TextUtils.isEmpty(email) &&
                    databaseHelper.clienteEmailExists(email)) {
                etEmail.setError("Este email ya está registrado");
                etEmail.requestFocus();
                return;
            }

            cliente.setNombre(nombre);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            cliente.setEmail(email);
            cliente.setNotas(notas);

            boolean result = databaseHelper.updateCliente(cliente);

            if (result) {
                Toast.makeText(this, "Cliente actualizado correctamente",
                        Toast.LENGTH_SHORT).show();
                loadClientes();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error al actualizar cliente",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleClienteStatus(Cliente cliente) {
        String action = cliente.isActivo() ? "desactivar" : "activar";
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Desea " + action + " este cliente?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (cliente.isActivo()) {
                        boolean result = databaseHelper.deactivateCliente(cliente.getId());
                        if (result) {
                            Toast.makeText(this, "Cliente desactivado",
                                    Toast.LENGTH_SHORT).show();
                            loadClientes();
                        }
                    } else {
                        cliente.setActivo(true);
                        boolean result = databaseHelper.updateCliente(cliente);
                        if (result) {
                            Toast.makeText(this, "Cliente activado",
                                    Toast.LENGTH_SHORT).show();
                            loadClientes();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void confirmDeleteCliente(Cliente cliente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_delete_confirmation, null);

        RadioGroup radioGroup = dialogView.findViewById(R.id.rgDeleteType);
        TextView tvWarning = dialogView.findViewById(R.id.tvDeleteWarning);

        builder.setView(dialogView)
                .setTitle("Eliminar Cliente")
                .setPositiveButton("Eliminar", null)
                .setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == R.id.rbLogicalDelete) {
                // Borrado lógico
                boolean result = databaseHelper.deactivateCliente(cliente.getId());
                if (result) {
                    Toast.makeText(this, "Cliente desactivado correctamente",
                            Toast.LENGTH_SHORT).show();
                    loadClientes();
                    dialog.dismiss();
                }
            } else if (selectedId == R.id.rbPhysicalDelete) {
                // Borrado físico - confirmación adicional
                new AlertDialog.Builder(this)
                        .setTitle("⚠️ Confirmación Final")
                        .setMessage("Esta acción eliminará permanentemente el cliente y no se puede deshacer. ¿Está completamente seguro?")
                        .setPositiveButton("Sí, eliminar", (d, w) -> {
                            boolean result = databaseHelper.deleteCliente(cliente.getId());
                            if (result) {
                                Toast.makeText(this, "Cliente eliminado permanentemente",
                                        Toast.LENGTH_SHORT).show();
                                loadClientes();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClientes();
    }
}