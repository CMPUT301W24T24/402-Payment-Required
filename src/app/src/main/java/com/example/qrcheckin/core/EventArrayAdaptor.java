package com.example.qrcheckin.core;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrcheckin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * An array adapter for displaying event information and connecting it with the ui
 */
public class EventArrayAdaptor extends ArrayAdapter<Event> {
    interface CreateListener {
        void createEvent(int position);
    }
    private ArrayList<Event> events;
    private Context context;

    /**
     * Constructor for the EventArrayAdaptor
     * @param context the context of the app
     * @param events the list of events
     */
    public EventArrayAdaptor(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
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
            view = LayoutInflater.from(context).inflate(R.layout.content_event, parent,false);
        }

        Event event = events.get(position);

        // set the text details of the event
        ((TextView) view.findViewById(R.id.event_name_text)).setText(event.getName());
        ((TextView) view.findViewById(R.id.event_date_time_text)).setText(event.getTime().toString());
        ((TextView) view.findViewById(R.id.event_status_check_in_text)).setText(event.isCurrentUserCheckedIn() ? "Checked In" : "Not Checked In");
        ((TextView) view.findViewById(R.id.event_status_sign_up_text)).setText(event.isCurrentUserSignedUp() ? "Signed Up" : "Not Signed Up");
        // null if attendee counting not implemented yet
        Integer attamt = event.getAttendeeAmount();
        if (attamt != null) {
            ((TextView) view.findViewById(R.id.event_number_of_attendees_text)).setText("Attendees: " + attamt.toString());
        }

        return view;
    }
}
