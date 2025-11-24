package com.example.proyectfrutalapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RoleAdapter extends ArrayAdapter<Role> {

    private Context context;
    private List<Role> roles;
    private RoleActionListener listener;

    public interface RoleActionListener {
        void onEditRole(Role role);
        void onDeleteRole(Role role);
    }

    public RoleAdapter(Context context, List<Role> roles, RoleActionListener listener) {
        super(context, 0, roles);
        this.context = context;
        this.roles = roles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_role, parent, false);
        }

        Role role = roles.get(position);

        TextView nameTextView = convertView.findViewById(R.id.roleNameTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.roleDescriptionTextView);
        ImageButton editButton = convertView.findViewById(R.id.editRoleButton);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteRoleButton);

        nameTextView.setText(role.getName());
        descriptionTextView.setText(role.getDescription());

        editButton.setOnClickListener(v -> {
            if (listener != null) listener.onEditRole(role);
        });

        deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteRole(role);
        });

        return convertView;
    }
}