package com.example.proyectfrutalapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class activity_contact extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private MapView mapView;
    private EditText nameEditText, emailEditText, messageEditText;
    private Button sendMessageButton, openMapsButton;

    // Coordenadas de ubicación (ejemplo: Santa Cruz, Bolivia)
    private static final double LATITUDE = -17.77303801118969;
    private static final double LONGITUDE = -63.2111752248406;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar osmdroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_contact);

        // Inicializar vistas
        initializeViews();

        // Configurar toolbar y drawer
        setupToolbarAndDrawer();

        // Configurar mapa
        setupMap();

        // Configurar listeners
        setupListeners();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerlayout);
        mapView = findViewById(R.id.mapView);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        openMapsButton = findViewById(R.id.openMapsButton);
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupMap() {
        // Configurar el mapa
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);

        // Establecer centro del mapa
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(LATITUDE, LONGITUDE);
        mapController.setCenter(startPoint);

        // Agregar marcador
        addMarker(startPoint);
    }

    private void addMarker(GeoPoint point) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Nuestra Ubicación");
        marker.setSnippet("Encuéntranos aquí");

        // Opcional: agregar ícono personalizado
        // marker.setIcon(getResources().getDrawable(R.drawable.ic_marker));

        mapView.getOverlays().add(marker);
    }

    private void setupListeners() {
        // Botón enviar mensaje
        sendMessageButton.setOnClickListener(v -> sendMessage());

        // Botón abrir en Google Maps
        openMapsButton.setOnClickListener(v -> openInGoogleMaps());
    }

    private void sendMessage() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        // Validaciones
        if (name.isEmpty()) {
            nameEditText.setError("Ingrese su nombre");
            nameEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Ingrese su email");
            emailEditText.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email inválido");
            emailEditText.requestFocus();
            return;
        }

        if (message.isEmpty()) {
            messageEditText.setError("Ingrese su mensaje");
            messageEditText.requestFocus();
            return;
        }

        // Enviar email usando Intent
        sendWhatsAppMessageWithChoice(name, email, message);
    }

    private void sendWhatsAppMessageWithChoice(String name, String email, String message) {
        String phoneNumber = "59175123456"; // CAMBIA ESTE NÚMERO

        String whatsappMessage =
                "*Mensaje de contacto*\n\n" +
                        "*Nombre:* " + name + "\n" +
                        "*Email:* " + email + "\n\n" +
                        "*Mensaje:*\n" + message;

        try {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, whatsappMessage);

            // Esto permite elegir entre WhatsApp normal y Business si ambos están instalados
            whatsappIntent.setPackage("com.whatsapp");

            startActivity(whatsappIntent);

            // Limpiar campos
            nameEditText.setText("");
            emailEditText.setText("");
            messageEditText.setText("");

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_LONG).show();
        }
    }

    private void openInGoogleMaps() {
        // Crear URI para Google Maps
        String uri = String.format("geo:%f,%f?q=%f,%f(Nuestra Ubicación)",
                LATITUDE, LONGITUDE, LATITUDE, LONGITUDE);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            // Si Google Maps no está instalado, abrir en navegador
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=" +
                            LATITUDE + "," + LONGITUDE));
            startActivity(webIntent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Manejar navegación del menú lateral
        int id = item.getItemId();


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
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}