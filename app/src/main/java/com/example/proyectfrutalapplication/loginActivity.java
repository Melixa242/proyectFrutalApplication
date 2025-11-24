package com.example.proyectfrutalapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class loginActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLinkTextView, forgotPasswordTextView;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar base de datos y sesión
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Verificar si ya hay sesión activa
        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        // Inicializar vistas
        initializeViews();

        // Configurar listeners
        setupListeners();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerLinkTextView = findViewById(R.id.registerLinkTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());

        registerLinkTextView.setOnClickListener(v -> {
            Intent intent = new Intent(loginActivity.this, registroActivity2.class);
            startActivity(intent);
        });

        forgotPasswordTextView.setOnClickListener(v -> {
            Toast.makeText(this, "Funcionalidad próximamente", Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptLogin() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validaciones
        if (email.isEmpty()) {
            usernameEditText.setError("Ingrese su email");
            usernameEditText.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usernameEditText.setError("Email inválido");
            usernameEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Ingrese su contraseña");
            passwordEditText.requestFocus();
            return;
        }

        // Intentar login
        User user = dbHelper.loginUser(email, password);

        if (user != null) {
            // Login exitoso
            sessionManager.createLoginSession(user);
            Toast.makeText(this, "¡Bienvenido " + user.getName() + "!", Toast.LENGTH_SHORT).show();
            navigateToHome();
        } else {
            // Login fallido
            Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(loginActivity.this, splashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}