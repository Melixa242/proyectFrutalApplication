package com.example.proyectfrutalapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class splashActivity extends AppCompatActivity {
    private CardView logoCard;
    private TextView appNameTextView;
    private TextView appSubtitleTextView;
    private CardView productCard;
    private ProgressBar progressBar;
    private TextView loadingTextView;
    private View circle1, circle2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Inicializar vistas
        logoCard = findViewById(R.id.logoCard);
        appNameTextView = findViewById(R.id.appNameTextView);
        appSubtitleTextView = findViewById(R.id.appSubtitleTextView);
        productCard = findViewById(R.id.productCard);
        progressBar = findViewById(R.id.progressBar);
        loadingTextView = findViewById(R.id.loadingTextView);
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);

        // Iniciar animaciones
        startAnimations();

        // Click en el logo para navegar
        logoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });

        // También en el producto
        productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });

        // Navegación automática después de 3 segundos (opcional)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Descomentar la siguiente línea para navegación automática
                // navigateToRegister();
            }
        }, 3000);
    }

    private void startAnimations() {
        // Animación del logo - Escala y fade in
        AnimationSet logoAnimSet = new AnimationSet(true);
        ScaleAnimation scaleAnim = new ScaleAnimation(
                0.5f, 1.0f, 0.5f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnim.setDuration(800);

        AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(800);

        logoAnimSet.addAnimation(scaleAnim);
        logoAnimSet.addAnimation(alphaAnim);
        logoCard.startAnimation(logoAnimSet);

        // Animación de los círculos - Rotación continua
        Animation rotateCircle1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        rotateCircle1.setDuration(2000);
        rotateCircle1.setRepeatCount(Animation.INFINITE);
        circle1.startAnimation(rotateCircle1);

        Animation rotateCircle2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        rotateCircle2.setDuration(3000);
        rotateCircle2.setRepeatCount(Animation.INFINITE);
        circle2.startAnimation(rotateCircle2);

        // Animación del texto - Slide desde arriba
        Animation slideDown = new AlphaAnimation(0.0f, 1.0f);
        slideDown.setDuration(1000);
        slideDown.setStartOffset(400);
        appNameTextView.startAnimation(slideDown);

        Animation slideDown2 = new AlphaAnimation(0.0f, 1.0f);
        slideDown2.setDuration(1000);
        slideDown2.setStartOffset(600);
        appSubtitleTextView.startAnimation(slideDown2);

        // Animación del producto - Bounce
        ScaleAnimation bounceAnim = new ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        bounceAnim.setDuration(1000);
        bounceAnim.setStartOffset(800);
        productCard.startAnimation(bounceAnim);

        // Animación del texto de carga - Parpadeo
        Animation blinkAnim = new AlphaAnimation(0.3f, 1.0f);
        blinkAnim.setDuration(1000);
        blinkAnim.setRepeatMode(Animation.REVERSE);
        blinkAnim.setRepeatCount(Animation.INFINITE);
        loadingTextView.startAnimation(blinkAnim);

        // Efecto de pulsación en el logo
        startPulseAnimation();
    }

    private void startPulseAnimation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScaleAnimation pulseAnim = new ScaleAnimation(
                        1.0f, 1.05f, 1.0f, 1.05f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                pulseAnim.setDuration(1000);
                pulseAnim.setRepeatMode(Animation.REVERSE);
                pulseAnim.setRepeatCount(1);
                logoCard.startAnimation(pulseAnim);

                handler.postDelayed(this, 3000);
            }
        }, 2000);
    }

    private void navigateToRegister() {
        // Animación de salida
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        findViewById(android.R.id.content).startAnimation(fadeOut);

        String mensajeReg = "¡Bienvenido! ¿Deseas registrarte?";
        Toast.makeText(splashActivity.this,
                "Bienvenido a la aplicación de Pulpas frutales",
                Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent moverInte = new Intent(splashActivity.this, registroActivity2.class);
                moverInte.putExtra("mensajeReg", mensajeReg);
                startActivity(moverInte);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 500);
    }
}