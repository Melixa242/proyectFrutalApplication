package com.example.proyectfrutalapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> clientes;
    private OnClienteClickListener listener;

    public interface OnClienteClickListener {
        void onClienteClick(Cliente cliente);
    }

    public ClienteAdapter(List<Cliente> clientes, OnClienteClickListener listener) {
        this.clientes = clientes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = clientes.get(position);
        holder.bind(cliente, listener);
    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTelefono, tvDireccion, tvEmail, tvEstado;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvClienteNombre);
            tvTelefono = itemView.findViewById(R.id.tvClienteTelefono);
            tvDireccion = itemView.findViewById(R.id.tvClienteDireccion);
            tvEmail = itemView.findViewById(R.id.tvClienteEmail);
            tvEstado = itemView.findViewById(R.id.tvClienteEstado);
        }

        public void bind(Cliente cliente, OnClienteClickListener listener) {
            tvNombre.setText(cliente.getNombre());
            tvTelefono.setText("Tel: " + (cliente.getTelefono() != null ?
                    cliente.getTelefono() : "N/A"));
            tvDireccion.setText("Dir: " + (cliente.getDireccion() != null ?
                    cliente.getDireccion() : "N/A"));
            tvEmail.setText("Email: " + (cliente.getEmail() != null ?
                    cliente.getEmail() : "N/A"));
            tvEstado.setText(cliente.isActivo() ? "Activo" : "Inactivo");
            tvEstado.setTextColor(cliente.isActivo() ?
                    itemView.getContext().getResources().getColor(android.R.color.holo_green_dark) :
                    itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));

            itemView.setOnClickListener(v -> listener.onClienteClick(cliente));
        }
    }
}