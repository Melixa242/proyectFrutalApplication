package com.example.proyectfrutalapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class loginActivity extends AppCompatActivity {
    ImageView moverP;
    EditText edtcorreo, edtclave;
    Button datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        moverP=findViewById(R.id.loginLogoImageView);
        edtclave=findViewById(R.id.passwordEditText);
        edtcorreo=findViewById(R.id.usernameEditText);
        datos=findViewById(R.id.loginButton);

        moverP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intentmover=new Intent(loginActivity.this,splashActivity.class);
                startActivity(intentmover);
            }
        });


        datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String IniciarS="Completa tus datos";
                String correo= edtcorreo.getText().toString();
                String clave=edtclave.getText().toString();
                //INSTANCIAR LA CLASE BUNDLE
                Bundle bundleDatos=new Bundle();
                bundleDatos.putString("edtcorreo",correo);
                bundleDatos.putString("edtclave", clave);
                bundleDatos.putString("Inicio",IniciarS);
                Intent intePasar=new Intent(loginActivity.this,registroActivity2.class);
                intePasar.putExtras(bundleDatos);
                startActivity(intePasar);

            }
        });

    }
}