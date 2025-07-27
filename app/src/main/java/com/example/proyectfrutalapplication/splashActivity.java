package com.example.proyectfrutalapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splashActivity extends AppCompatActivity {
    ImageView moverP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        moverP=findViewById(R.id.logoImageView);
        moverP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensajeReg="UD. NOS AYUDARA REGISTRESE?";


                Toast.makeText(splashActivity.this, "Bienvenido a la aplicación de Pulpas frutales", Toast.LENGTH_LONG).show();

                Intent moverInte=new Intent(splashActivity.this,registroActivity2.class);
                moverInte.putExtra("mensajeReg",mensajeReg);
                startActivity(moverInte);
            }
        });

    }
}