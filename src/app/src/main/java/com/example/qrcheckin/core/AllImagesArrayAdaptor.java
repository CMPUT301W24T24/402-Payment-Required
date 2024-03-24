package com.example.qrcheckin.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrcheckin.R;

import java.util.ArrayList;

/**
 * An array adapter for displaying event information and connecting it with the ui
 */
public class AllImagesArrayAdaptor extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private ArrayList<User> users;
    private Context context;

    /**
     * Constructor for the EventArrayAdaptor
     * @param context the context of the app
     * @param events the list of events
     * @param users the list of users
     */
    public AllImagesArrayAdaptor(Context context, ArrayList<Event> events, ArrayList<User> users) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
        this.users = users;
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
            view = LayoutInflater.from(context).inflate(R.layout.content_image, parent,false);
        }
        Bitmap bitmap;
        if (events == null) {
            // TODO add events poster getting method
        } else if (users == null) {
            Database db = new Database();
            db.getUserPicture(users.get(position), view.findViewById(R.id.single_image));
        }
        return view;
    }
}
