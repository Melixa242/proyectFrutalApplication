package com.example.proyectfrutalapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.proyectfrutalapplication.R;

public class TranslatorActivity extends AppCompatActivity {

    private EditText englishEditText;
    private TextView spanishTextView;
    private Button translateButton, downloadModelsButton;
    private ProgressBar progressBar;
    private TextView statusTextView;

    private com.example.proyectfrutalapplication.TranslatorHelper translatorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        setupToolbar();
        initializeViews();

        translatorHelper = new com.example.proyectfrutalapplication.TranslatorHelper(this);

        checkModelsStatus();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Traductor EN → ES");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        englishEditText = findViewById(R.id.englishEditText);
        spanishTextView = findViewById(R.id.spanishTextView);
        translateButton = findViewById(R.id.translateButton);
        downloadModelsButton = findViewById(R.id.downloadModelsButton);
        progressBar = findViewById(R.id.progressBar);
        statusTextView = findViewById(R.id.statusTextView);

        progressBar.setVisibility(View.GONE);
    }

    private void checkModelsStatus() {
        translatorHelper.checkModelDownloaded(isDownloaded -> {
            if (isDownloaded) {
                statusTextView.setText("✅ Modelos descargados - Listo para traducir");
                statusTextView.setTextColor(getColor(R.color.colorPrimary));
                downloadModelsButton.setVisibility(View.GONE);
                translateButton.setEnabled(true);
            } else {
                statusTextView.setText("⚠️ Modelos no descargados - Se descargarán al traducir");
                statusTextView.setTextColor(getColor(R.color.warning_yellow));
                downloadModelsButton.setVisibility(View.VISIBLE);
                translateButton.setEnabled(true);
            }
        });
    }

    private void setupListeners() {
        translateButton.setOnClickListener(v -> translateText());
        downloadModelsButton.setOnClickListener(v -> downloadModels());
    }

    private void translateText() {
        String englishText = englishEditText.getText().toString().trim();

        if (englishText.isEmpty()) {
            Toast.makeText(this, "Ingrese texto en inglés", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        translateButton.setEnabled(false);
        statusTextView.setText("🔄 Traduciendo...");

        translatorHelper.translateText(englishText, new com.example.proyectfrutalapplication.TranslatorHelper.TranslationCallback() {
            @Override
            public void onSuccess(String translatedText) {
                progressBar.setVisibility(View.GONE);
                translateButton.setEnabled(true);
                spanishTextView.setText(translatedText);
                spanishTextView.setVisibility(View.VISIBLE);
                statusTextView.setText("✅ Traducción completada");
                statusTextView.setTextColor(getColor(R.color.colorPrimary));
            }

            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                translateButton.setEnabled(true);
                Toast.makeText(TranslatorActivity.this,
                        "Error: " + error, Toast.LENGTH_LONG).show();
                statusTextView.setText("❌ Error en traducción");
                statusTextView.setTextColor(getColor(android.R.color.holo_red_dark));
            }

            @Override
            public void onDownloading(String message) {
                statusTextView.setText("⬇️ Descargando modelos (~60 MB)...");
            }
        });
    }

    private void downloadModels() {
        progressBar.setVisibility(View.VISIBLE);
        downloadModelsButton.setEnabled(false);

        translatorHelper.downloadModels(new com.example.proyectfrutalapplication.TranslatorHelper.DownloadCallback() {
            @Override
            public void onDownloadStarted() {
                statusTextView.setText("⬇️ Descargando modelos...");
                Toast.makeText(TranslatorActivity.this,
                        "Descargando modelos de idiomas (~60 MB)",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDownloadSuccess() {
                progressBar.setVisibility(View.GONE);
                downloadModelsButton.setVisibility(View.GONE);
                translateButton.setEnabled(true);
                statusTextView.setText("✅ Modelos descargados correctamente");
                statusTextView.setTextColor(getColor(R.color.colorPrimary));
                Toast.makeText(TranslatorActivity.this,
                        "Modelos descargados. Ya puede traducir sin conexión",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDownloadFailed(String error) {
                progressBar.setVisibility(View.GONE);
                downloadModelsButton.setEnabled(true);
                Toast.makeText(TranslatorActivity.this,
                        "Error descargando: " + error, Toast.LENGTH_LONG).show();
                statusTextView.setText("❌ Error en descarga");
                statusTextView.setTextColor(getColor(android.R.color.holo_red_dark));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (translatorHelper != null) {
            translatorHelper.close();
        }
    }
}

// ========== EJEMPLO DE USO EN TU API ==========
/*
// En tu actividad donde consumes la API:

private void loadProductsFromAPI() {
    // 1. Obtener productos de la API (ejemplo)
    String apiResponse = "{\"name\":\"Strawberry\",\"price\":10.5}";

    // 2. Parsear JSON
    JSONObject json = new JSONObject(apiResponse);
    String englishName = json.getString("name");

    // 3. Traducir
    TranslatorHelper translator = new TranslatorHelper(this);
    translator.translateText(englishName, new TranslatorHelper.TranslationCallback() {
        @Override
        public void onSuccess(String spanishName) {
            // Usar el nombre traducido
            product.setName(spanishName); // "Fresa"
            // Mostrar en tu UI
        }

        @Override
        public void onFailure(String error) {
            // Usar nombre original si falla
            product.setName(englishName);
        }

        @Override
        public void onDownloading(String message) {
            // Mostrar progreso
            showLoading(message);
        }
    });
}
*/