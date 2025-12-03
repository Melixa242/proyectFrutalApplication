package com.example.proyectfrutalapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ListView productsListView;
    private Button addProductButton;
    private static final String TAG = "ManageProductsActivity";

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private ProductAdapter productAdapter;
    private List<Product> productsList;
    private List<FruitAPI> fruitsFromAPI;

    // ========== TRADUCTOR ==========
    private TranslatorHelper translatorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        translatorHelper = new TranslatorHelper(this); // NUEVO

        setupToolbarAndDrawer();
        initializeViews();
        loadFruitsFromAPI();
        loadProducts();
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
        productsListView = findViewById(R.id.productsListView);
        addProductButton = findViewById(R.id.addProductButton);
    }

    private void loadFruitsFromAPI() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando catálogo de frutas...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FruitAPIClient.getAllFruits(new FruitAPIClient.OnFruitsLoadedListener() {
            @Override
            public void onFruitsLoaded(List<FruitAPI> fruits) {
                progressDialog.dismiss();
                fruitsFromAPI = fruits;
                Log.d(TAG, "Frutas cargadas: " + fruits.size());
                Toast.makeText(ManageProductsActivity.this,
                        fruits.size() + " frutas disponibles en el catálogo",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Log.e(TAG, "Error al cargar frutas: " + error);
                Toast.makeText(ManageProductsActivity.this,
                        "Error: " + error + "\nSe puede usar sin catálogo",
                        Toast.LENGTH_LONG).show();
                fruitsFromAPI = new ArrayList<>();
            }
        });
    }

    private void loadProducts() {
        productsList = dbHelper.getAllProducts();
        productAdapter = new ProductAdapter(this, productsList, new ProductAdapter.ProductActionListener() {
            @Override
            public void onEditProduct(Product product) {
                showEditProductDialog(product);
            }

            @Override
            public void onDeleteProduct(Product product) {
                showDeleteProductDialog(product);
            }

            @Override
            public void onSendWhatsApp(Product product) {
                sendToWhatsApp(product);
            }
        });
        productsListView.setAdapter(productAdapter);
    }

    private void setupListeners() {
        addProductButton.setOnClickListener(v -> showAddProductDialog());
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        AutoCompleteTextView fruitAutoComplete = dialogView.findViewById(R.id.fruitNameEditText);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        Spinner unitSpinner = dialogView.findViewById(R.id.unitSpinner);
        EditText wholesalePriceEditText = dialogView.findViewById(R.id.wholesalePriceEditText);
        EditText retailPriceEditText = dialogView.findViewById(R.id.retailPriceEditText);
        RadioGroup fractionGroup1 = dialogView.findViewById(R.id.fractionRadioGroup);
        RadioGroup fractionGroup2 = dialogView.findViewById(R.id.fractionRadioGroup2);
        EditText productionDateEditText = dialogView.findViewById(R.id.productionDateEditText);
        EditText expiryDateEditText = dialogView.findViewById(R.id.expiryDateEditText);
        EditText notesEditText = dialogView.findViewById(R.id.notesEditText);
        Button loadInfoButton = dialogView.findViewById(R.id.loadFruitInfoButton);

        // Configurar autocomplete con frutas traducidas
        setupAutoCompleteWithTranslation(fruitAutoComplete);

        // Cargar info de fruta con traducción
        loadInfoButton.setOnClickListener(v -> {
            String fruitName = fruitAutoComplete.getText().toString().trim();
            if (!fruitName.isEmpty()) {
                loadFruitInfoWithTranslation(fruitName, notesEditText);
            } else {
                Toast.makeText(this, "Ingrese el nombre de una fruta", Toast.LENGTH_SHORT).show();
            }
        });

        productionDateEditText.setOnClickListener(v -> showDatePicker(productionDateEditText));
        expiryDateEditText.setOnClickListener(v -> showDatePicker(expiryDateEditText));

        builder.setTitle("Agregar Producto")
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = fruitAutoComplete.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();
            String unit = unitSpinner.getSelectedItem().toString();
            String wholesalePriceStr = wholesalePriceEditText.getText().toString().trim();
            String retailPriceStr = retailPriceEditText.getText().toString().trim();
            String fraction = getSelectedFraction(fractionGroup1, fractionGroup2);
            String productionDate = productionDateEditText.getText().toString().trim();
            String expiryDate = expiryDateEditText.getText().toString().trim();
            String notes = notesEditText.getText().toString().trim();

            if (validateProductInput(name, quantityStr, wholesalePriceStr, retailPriceStr,
                    productionDate, expiryDate)) {

                double quantity = Double.parseDouble(quantityStr);
                double wholesalePrice = Double.parseDouble(wholesalePriceStr);
                double retailPrice = Double.parseDouble(retailPriceStr);

                Product product = new Product(0, name, quantity, unit, fraction,
                        productionDate, expiryDate, notes, wholesalePrice, retailPrice);

                long result = dbHelper.addProduct(product, sessionManager.getUserId());

                if (result > 0) {
                    Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show();
                    loadProducts();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Error al agregar producto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showEditProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        AutoCompleteTextView fruitAutoComplete = dialogView.findViewById(R.id.fruitNameEditText);
        EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        Spinner unitSpinner = dialogView.findViewById(R.id.unitSpinner);
        EditText wholesalePriceEditText = dialogView.findViewById(R.id.wholesalePriceEditText);
        EditText retailPriceEditText = dialogView.findViewById(R.id.retailPriceEditText);
        EditText productionDateEditText = dialogView.findViewById(R.id.productionDateEditText);
        EditText expiryDateEditText = dialogView.findViewById(R.id.expiryDateEditText);
        EditText notesEditText = dialogView.findViewById(R.id.notesEditText);
        Button loadInfoButton = dialogView.findViewById(R.id.loadFruitInfoButton);

        setupAutoCompleteWithTranslation(fruitAutoComplete);

        // Llenar con datos existentes
        fruitAutoComplete.setText(product.getName());
        quantityEditText.setText(String.valueOf(product.getQuantity()));
        wholesalePriceEditText.setText(String.valueOf(product.getWholesalePrice()));
        retailPriceEditText.setText(String.valueOf(product.getRetailPrice()));
        productionDateEditText.setText(product.getProductionDate());
        expiryDateEditText.setText(product.getExpiryDate());
        notesEditText.setText(product.getNotes());

        loadInfoButton.setOnClickListener(v -> {
            String fruitName = fruitAutoComplete.getText().toString().trim();
            if (!fruitName.isEmpty()) {
                loadFruitInfoWithTranslation(fruitName, notesEditText);
            }
        });

        productionDateEditText.setOnClickListener(v -> showDatePicker(productionDateEditText));
        expiryDateEditText.setOnClickListener(v -> showDatePicker(expiryDateEditText));

        builder.setTitle("Editar Producto")
                .setPositiveButton("Actualizar", null)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = fruitAutoComplete.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();
            String wholesalePriceStr = wholesalePriceEditText.getText().toString().trim();
            String retailPriceStr = retailPriceEditText.getText().toString().trim();

            if (name.isEmpty() || quantityStr.isEmpty() ||
                    wholesalePriceStr.isEmpty() || retailPriceStr.isEmpty()) {
                Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            product.setName(name);
            product.setQuantity(Double.parseDouble(quantityStr));
            product.setUnit(unitSpinner.getSelectedItem().toString());
            product.setWholesalePrice(Double.parseDouble(wholesalePriceStr));
            product.setRetailPrice(Double.parseDouble(retailPriceStr));
            product.setProductionDate(productionDateEditText.getText().toString());
            product.setExpiryDate(expiryDateEditText.getText().toString());
            product.setNotes(notesEditText.getText().toString());

            boolean result = dbHelper.updateProduct(product);
            if (result) {
                Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                loadProducts();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========== MÉTODOS CON TRADUCCIÓN ==========

    /**
     * Configura el autocompletado con nombres de frutas traducidos al español
     */
    private void setupAutoCompleteWithTranslation(AutoCompleteTextView autoComplete) {
        if (fruitsFromAPI != null && !fruitsFromAPI.isEmpty()) {
            List<String> spanishFruitNames = new ArrayList<>();

            for (FruitAPI fruit : fruitsFromAPI) {
                // Agregar nombre original en inglés por ahora
                spanishFruitNames.add(fruit.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, spanishFruitNames);
            autoComplete.setAdapter(adapter);
            autoComplete.setThreshold(1);

            // Nota: Para traducir los nombres en el dropdown, necesitarías
            // hacer las traducciones de forma asíncrona al cargar las frutas
        }
    }

    /**
     * Carga información de la fruta desde la API y traduce al español
     */
    private void loadFruitInfoWithTranslation(String fruitName, EditText notesEditText) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando información de " + fruitName + "...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FruitAPIClient.getFruitByName(fruitName, new FruitAPIClient.OnFruitLoadedListener() {
            @Override
            public void onFruitLoaded(FruitAPI fruit) {
                // Obtener descripción en inglés
                String englishDescription = fruit.getFullDescription();

                // Mostrar estado de traducción
                progressDialog.setMessage("Traduciendo información...");

                // Traducir al español
                translatorHelper.translateText(englishDescription,
                        new TranslatorHelper.TranslationCallback() {

                            @Override
                            public void onSuccess(String translatedText) {
                                progressDialog.dismiss();

                                // Agregar la descripción traducida a las observaciones
                                String currentNotes = notesEditText.getText().toString();
                                if (!currentNotes.isEmpty()) {
                                    currentNotes += "\n\n";
                                }

                                notesEditText.setText(currentNotes + "📋 INFORMACIÓN NUTRICIONAL:\n" +
                                        translatedText);

                                Toast.makeText(ManageProductsActivity.this,
                                        "✓ Información traducida correctamente",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String error) {
                                progressDialog.dismiss();

                                // Si falla la traducción, usar texto original en inglés
                                String currentNotes = notesEditText.getText().toString();
                                if (!currentNotes.isEmpty()) {
                                    currentNotes += "\n\n";
                                }

                                notesEditText.setText(currentNotes + "📋 NUTRITIONAL INFO:\n" +
                                        englishDescription);

                                Toast.makeText(ManageProductsActivity.this,
                                        "⚠️ Info cargada (sin traducir): " + error,
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onDownloading(String message) {
                                progressDialog.setMessage("Descargando modelos de traducción...");
                            }
                        });
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                Toast.makeText(ManageProductsActivity.this,
                        "Error cargando fruta: " + error,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // ========== RESTO DE MÉTODOS ORIGINALES ==========

    private void showDeleteProductDialog(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Producto")
                .setMessage("¿Está seguro de eliminar " + product.getName() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    boolean result = dbHelper.deleteProduct(product.getId());
                    if (result) {
                        Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                        loadProducts();
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void sendToWhatsApp(Product product) {
        String message = buildWhatsAppMessage(product);

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/?text=" + Uri.encode(message)));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    private String buildWhatsAppMessage(Product product) {
        StringBuilder message = new StringBuilder();
        message.append("🍓 *PULPA DE ").append(product.getName().toUpperCase()).append("*\n\n");
        message.append("📦 *Cantidad:* ").append(product.getQuantity()).append(" ")
                .append(product.getUnit()).append("\n");
        message.append("⚖️ *Fracción:* ").append(product.getFraction()).append("\n");
        message.append("💰 *Precio Mayor:* Bs ").append(String.format("%.2f",
                product.getWholesalePrice())).append("\n");
        message.append("💵 *Precio Menor:* Bs ").append(String.format("%.2f",
                product.getRetailPrice())).append("\n");
        message.append("📅 *Fecha de Producción:* ").append(product.getProductionDate()).append("\n");
        message.append("⏰ *Fecha de Vencimiento:* ").append(product.getExpiryDate()).append("\n");

        if (product.getNotes() != null && !product.getNotes().isEmpty()) {
            message.append("\n📝 *Información:*\n").append(product.getNotes()).append("\n");
        }

        message.append("\n✨ *Pulpas de Frutas Frescas y Naturales*\n");
        message.append("📱 Contacto: +591 73641022");

        return message.toString();
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    editText.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    private String getSelectedFraction(RadioGroup group1, RadioGroup group2) {
        int selectedId1 = group1.getCheckedRadioButtonId();
        int selectedId2 = group2.getCheckedRadioButtonId();

        if (selectedId1 != -1) {
            RadioButton radioButton = group1.findViewById(selectedId1);
            return radioButton.getText().toString();
        }

        if (selectedId2 != -1) {
            RadioButton radioButton = group2.findViewById(selectedId2);
            return radioButton.getText().toString();
        }

        return "1";
    }

    private boolean validateProductInput(String name, String quantity,
                                         String wholesalePrice, String retailPrice,
                                         String productionDate, String expiryDate) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Ingrese el nombre del producto", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (quantity.isEmpty()) {
            Toast.makeText(this, "Ingrese la cantidad", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (wholesalePrice.isEmpty()) {
            Toast.makeText(this, "Ingrese el precio por mayor", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (retailPrice.isEmpty()) {
            Toast.makeText(this, "Ingrese el precio por menor", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double wPrice = Double.parseDouble(wholesalePrice);
            double rPrice = Double.parseDouble(retailPrice);

            if (wPrice <= 0 || rPrice <= 0) {
                Toast.makeText(this, "Los precios deben ser mayores a 0", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (wPrice > rPrice) {
                Toast.makeText(this, "El precio por mayor debe ser menor al precio por menor",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precios inválidos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (productionDate.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha de producción", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (expiryDate.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha de vencimiento", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar el traductor para liberar recursos
        if (translatorHelper != null) {
            translatorHelper.close();
        }
    }
}