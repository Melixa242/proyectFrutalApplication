package com.example.proyectfrutalapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductosViewHolder>{
    private List<productos> listaProducto;

    public ProductosAdapter(List<productos> listaProducto) {
        this.listaProducto = listaProducto;
    }

    @NonNull
    @Override
    public ProductosAdapter.ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ItenView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto,parent,false);
         return new ProductosViewHolder(ItenView);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductosAdapter.ProductosViewHolder holder, int position) {
        productos Producto=listaProducto.get(position);
        holder.bind(Producto);

    }

    @Override
    public int getItemCount() {
        return listaProducto.size();
    }

    public class ProductosViewHolder extends RecyclerView.ViewHolder{
        private TextView txcodigo,txtnombre,txtcontenido,txprecio,txcantidad;

        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);
            txcodigo=itemView.findViewById(R.id.tvcodigo);
            txtnombre=itemView.findViewById(R.id.tvnombre);
            txtcontenido=itemView.findViewById(R.id.tvcontenido);
            txprecio= itemView.findViewById(R.id.tvprecio);
           txcantidad =itemView.findViewById(R.id.tvcantidad);
        }

        public void bind(productos producto) {
            txcodigo.setText(producto.getCodigo());
            txtnombre.setText(producto.getNombre());
            txtcontenido.setText(String.valueOf(producto.getContenido()));
            txprecio.setText(producto.getPrecio());
            txcantidad.setText(producto.getCantidad());

        }
    }
}
