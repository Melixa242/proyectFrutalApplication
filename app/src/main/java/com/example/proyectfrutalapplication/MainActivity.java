package com.example.proyectfrutalapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    ImageView moverPantalla;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                int id = item.getItemId();
                if (id == R.id.nav_login) {
                    Intent intent = new Intent(MainActivity.this, loginActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_change_password) {
                    Intent intent = new Intent(MainActivity.this, confirm_passwordActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_register_pulp_drawer) {
                    Intent intent = new Intent(MainActivity.this, registroActivity2.class);
                    startActivity(intent);
                } else if (id == R.id.nav_products_list) {
                    Intent intent = new Intent(MainActivity.this, Lista_Producto_Activity.class);
                    startActivity(intent);
                } else if (id == R.id.splash) {
                Intent intent = new Intent(MainActivity.this, splashActivity.class);
                startActivity(intent);
            }

                return false;
            }
        });


        moverPantalla=findViewById(R.id.pulpLogoImageView);
        moverPantalla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent moverIntent=new Intent(MainActivity.this,loginActivity.class);
                startActivity(moverIntent);

            }
        });

    }
}