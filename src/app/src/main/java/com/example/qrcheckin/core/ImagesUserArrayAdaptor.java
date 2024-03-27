package com.example.qrcheckin.core;

import static com.example.qrcheckin.core.Database.getAllImagesEventPoster;
import static com.example.qrcheckin.core.Database.getAllImagesUserPicture;

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
public class ImagesUserArrayAdaptor extends ArrayAdapter<User> {

    private ArrayList<User> users;
    private Context context;

    /**
     * Constructor for the EventArrayAdaptor
     * @param context the context of the app
     * @param users the list of users
     */
    public ImagesUserArrayAdaptor(Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.context = context;
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

        getAllImagesUserPicture(this, position, view.findViewById(R.id.single_image));
        return view;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
