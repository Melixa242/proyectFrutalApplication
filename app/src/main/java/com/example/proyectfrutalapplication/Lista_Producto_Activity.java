package com.example.proyectfrutalapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Lista_Producto_Activity extends AppCompatActivity {
    private RecyclerView ReciclearView;
    private List<productos> listaProductos;
    private ProductosAdapter productosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_producto);
        listaProductos=new ArrayList<>();
        listaProductos.add(new productos("1", "Maracuya sin semilla","500gr","15 unidades","20 bs","20/06/2025","20/06/2025"));
        listaProductos.add(new productos("2", "Frutilla","500gr","15 unidades","20 bs","20/06/2025","20/06/2025"));
        listaProductos.add(new productos("3", "Tamarindo","500gr","10 unidades","20 bs","20/06/2025","20/06/2025"));
        listaProductos.add(new productos("4", "Copoazu","500gr","15 unidades","25 bs","20/06/2025","20/06/2025"));
        listaProductos.add(new productos("5", "Acerola","500gr","15 unidades","20 bs","20/06/2025","20/06/2025"));
        listaProductos.add(new productos("6", "Piña","500gr","15 unidades","15 bs","20/06/2025","20/06/2025"));
        listaProductos.add(new productos("7", "Maracuya con Semilla","500gr","15 unidades","15 bs","20/06/2025","20/06/2025"));
        listaProductos.add(new productos("8", "Acai","500gr","15 unidades","25 bs","20/06/2025","20/06/2025"));


        ReciclearView=findViewById(R.id.reciclear);
        ReciclearView.setLayoutManager(new LinearLayoutManager(this));
        productosAdapter=new ProductosAdapter(listaProductos);
        ReciclearView.setAdapter(productosAdapter);

    }
}