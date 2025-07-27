
package com.example.proyectfrutalapplication;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class registroActivity2 extends AppCompatActivity {
    TextView recibirMens;
    ImageView PasarSplas;
    EditText edtcorreo,edtclave,edtnombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        PasarSplas=findViewById(R.id.registerLogoImageView);
       recibirMens=findViewById(R.id.registerTitleTextView);


        String captura = getIntent().getStringExtra("mensajeReg");

        recibirMens.setText(captura);

        PasarSplas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pasar=new Intent(registroActivity2.this,confirm_passwordActivity.class);
                startActivity(pasar);
            }
        });

            edtnombre=findViewById(R.id.fullNameEditText);
            edtcorreo=findViewById(R.id.emailEditText);
            edtclave=findViewById(R.id.passwordEditText);
            Bundle recibirDatos=getIntent().getExtras();
            if (recibirDatos!=null){
                String mensRe=recibirDatos.getString("Inicio");
                String recCorreo=recibirDatos.getString("edtcorreo");
                String recPass=recibirDatos.getString("edtclave");

                edtnombre.setText(mensRe);
                edtcorreo.setText(recCorreo);
                edtclave.setText(recPass);


            }

    }
}