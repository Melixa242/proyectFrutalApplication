package com.example.proyectfrutalapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SENSOR DE MOVIMIENTO - DETECTOR DE AGITADO
 * Detecta cuando el usuario agita el teléfono para limpiar el carrito
 */
public class ShakeSensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView statusTextView;

    // Variables para detectar agitado
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;
    private int shakeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_sensor);

        statusTextView = findViewById(R.id.sensorStatusTextView);

        // Inicializar sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if (accelerometer != null) {
                statusTextView.setText("✅ Sensor de acelerómetro disponible\n\n🤳 Agita el teléfono para limpiar el carrito");
            } else {
                statusTextView.setText("❌ Este dispositivo no tiene acelerómetro");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    shakeCount++;
                    onShakeDetected();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesario para este sensor
    }

    /**
     * Se ejecuta cuando se detecta agitado del teléfono
     */
    private void onShakeDetected() {
        // Verificar si hay items en el carrito (usando la variable estática de SalesActivity)
        if (!SalesActivity.shoppingCart.isEmpty()) {

            statusTextView.setText("🎉 ¡Agitado detectado! #" + shakeCount +
                    "\n\nCarrito tiene " + SalesActivity.shoppingCart.size() + " items");

            // Mostrar diálogo de confirmación
            new AlertDialog.Builder(this)
                    .setTitle("🗑️ Vaciar carrito")
                    .setMessage("¿Estás seguro de que quieres vaciar el carrito?\n\n" +
                            "Items en carrito: " + SalesActivity.shoppingCart.size())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Sí, vaciar", (dialog, which) -> {
                        SalesActivity.shoppingCart.clear();
                        Toast.makeText(this, "🗑️ Carrito vaciado exitosamente",
                                Toast.LENGTH_SHORT).show();
                        statusTextView.setText("✅ Carrito limpiado\n\n🤳 Agita nuevamente si es necesario");
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show();
                    })
                    .show();

        } else {
            statusTextView.setText("ℹ️ Agitado detectado #" + shakeCount +
                    "\n\nEl carrito ya está vacío");
            Toast.makeText(this, "El carrito ya está vacío", Toast.LENGTH_SHORT).show();
        }
    }
}