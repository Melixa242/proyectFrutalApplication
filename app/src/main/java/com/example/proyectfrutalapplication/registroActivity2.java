package com.example.proyectfrutalapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class registroActivity2 extends AppCompatActivity {

    private EditText fullNameEditText, emailEditText, phoneEditText;
    private EditText passwordEditText, confirmPasswordEditText;
    private CheckBox termsCheckBox;
    private Button registerButton;
    private TextView loginLinkTextView;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        termsCheckBox = findViewById(R.id.termsCheckBox);
        registerButton = findViewById(R.id.registerButton);
        loginLinkTextView = findViewById(R.id.loginLinkTextView);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> attemptRegister());

        loginLinkTextView.setOnClickListener(v -> {
           Intent intePasar= new Intent(registroActivity2.this, loginActivity.class);
           startActivity(intePasar);
           finish();
        });
    }

    private void attemptRegister() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validaciones
        if (fullName.isEmpty()) {
            fullNameEditText.setError("Ingrese su nombre completo");
            fullNameEditText.requestFocus();
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

        // Verificar si el email ya existe
        if (dbHelper.emailExists(email)) {
            emailEditText.setError("Este email ya está registrado");
            emailEditText.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            phoneEditText.setError("Ingrese su teléfono");
            phoneEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Ingrese una contraseña");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("La contraseña debe tener al menos 6 caracteres");
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Las contraseñas no coinciden");
            confirmPasswordEditText.requestFocus();
            return;
        }

        if (!termsCheckBox.isChecked()) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Registrar usuario
        long userId = dbHelper.registerUser(fullName, email, phone, password);

        if (userId > 0) {
            Toast.makeText(this, "¡Registro exitoso! Inicie sesión", Toast.LENGTH_LONG).show();
           Intent intent = new Intent(registroActivity2.this, loginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar usuario. Intente nuevamente",
                    Toast.LENGTH_LONG).show();
        }
    }
}