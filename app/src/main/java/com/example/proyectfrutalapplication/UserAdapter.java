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

public class UserAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> users;
    private UserActionListener listener;

    public interface UserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
        void onAssignRole(User user);
    }

    public UserAdapter(Context context, List<User> users, UserActionListener listener) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }

        User user = users.get(position);

        TextView nameTextView = convertView.findViewById(R.id.userNameTextView);
        TextView emailTextView = convertView.findViewById(R.id.userEmailTextView);
        TextView phoneTextView = convertView.findViewById(R.id.userPhoneTextView);
        ImageButton editButton = convertView.findViewById(R.id.editUserButton);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteUserButton);
        ImageButton roleButton = convertView.findViewById(R.id.assignRoleButton);

        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhone());

        editButton.setOnClickListener(v -> {
            if (listener != null) listener.onEditUser(user);
        });

        deleteButton.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteUser(user);
        });

        roleButton.setOnClickListener(v -> {
            if (listener != null) listener.onAssignRole(user);
        });

        return convertView;
    }
}
