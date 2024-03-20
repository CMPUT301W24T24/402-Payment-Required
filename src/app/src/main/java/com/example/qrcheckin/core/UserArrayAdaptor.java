package com.example.qrcheckin.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrcheckin.R;

import java.util.ArrayList;

public class UserArrayAdaptor extends ArrayAdapter<User> {
    interface CreateListener {
        void createUser(int position);
    }
    private ArrayList<User> users;
    private Context context;

    /**
     * Constructor for the UserArrayAdaptor
     * @param context The context of the app
     * @param users The list of users
     */
    public UserArrayAdaptor(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    /**
     * Get the view of the event
     * @param position the position of the event
     * @param convertView the view of the event
     * @param parent the parent of the view
     * @return the view of the event
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_user, parent,false);
        }

        User user = users.get(position);
        ((ImageView) view.findViewById(R.id.user_profile_image)).setImageBitmap(user.generateProfilePicture());
        ((TextView) view.findViewById(R.id.user_name_text)).setText(user.getName());
        // TODO: set user number of checkins
        ((TextView) view.findViewById(R.id.user_number_of_checkins)).setText("0");
        ((TextView) view.findViewById(R.id.user_phone_number_text)).setText(user.getPhone());

        return view;
    }
}
