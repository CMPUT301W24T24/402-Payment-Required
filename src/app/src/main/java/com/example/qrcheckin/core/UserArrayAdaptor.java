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

public class UserArrayAdaptor extends ArrayAdapter<User> implements Database.OnCheckInCountRetrievedListener {
    /**
     * Sets the view to the number of users checkins.
     * @param checkInCount The number of checkins into an event by the user
     */
    @Override
    public void onCheckInCountRetrieved(long checkInCount) {

    }

    /**
     * Listener that waits for the user to be created
     */
    interface CreateListener {
        void createUser(int position);
    }
    private ArrayList<User> users;
    private Context context;
    private String eventId;

    /**
     * Constructor for the UserArrayAdaptor
     * @param context The context of the app
     * @param users The list of users
     * @param eventId The ID of the event
     */
    public UserArrayAdaptor(Context context, ArrayList<User> users, String eventId) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
        this.eventId = eventId;
    }

    /**
     * Get the view of the user
     * @param position the position of the user
     * @param convertView the view of the user
     * @param parent the parent of the view
     * @return the view of the user
     * Reference: Gemini
     * Prompt: How could I update the database function to be callable in the UserArrayAdaptor class so that I
     *          could set the text of user_number_of_checkins to the number of time the user has checked into a
     *          specific event without introducing a new field numCheckIns in the user store?
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_user, parent,false);
        }
        User user = users.get(position);
        Database db = new Database(context);
        db.getUserPicture(user, (ImageView) view.findViewById(R.id.user_profile_image));

        ((TextView) view.findViewById(R.id.user_name_text)).setText(user.getName());
        if (user.getPhone().isEmpty()) {
            ((TextView) view.findViewById(R.id.user_phone_number_text)).setText("###-###-####");
        }
        else {
            ((TextView) view.findViewById(R.id.user_phone_number_text)).setText(user.getPhone());
        }

        ((TextView) view.findViewById(R.id.user_number_of_checkins)).setText("searching...");

        View finalView = view;
        Database.getCheckInCountForUser(user.getId(), eventId, new Database.OnCheckInCountRetrievedListener() {
            /**
             * Called when the checkin count is retrieved and then displays the output to the user
             * @param checkInCount The number of times the user has checked into the event
             */
            @Override
            public void onCheckInCountRetrieved(long checkInCount) {
                String numCheckins;
                if (checkInCount == -1) {
                    numCheckins = "Error getting checkin count";
                } else if (checkInCount == 1) {
                    numCheckins = String.valueOf(checkInCount) + " checkin";
                } else {
                    numCheckins = String.valueOf(checkInCount) + " checkins";
                }
                ((TextView) finalView.findViewById(R.id.user_number_of_checkins)).setText(numCheckins);

            }
        });

        return finalView;
    }
}
