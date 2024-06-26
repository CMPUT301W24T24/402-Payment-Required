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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;
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
    private Boolean allEvent;

    /**
     * Constructor for the EventArrayAdaptor
     * @param context the context of the app
     * @param events the list of events
     */
    public EventArrayAdaptor(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
        this.allEvent = false;
    }

    public EventArrayAdaptor(Context context, ArrayList<Event> events, Boolean allEvent) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
        this.allEvent = allEvent;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Get the view of the event
     * @param position the position of the event
     * @param convertView the view of the event
     * @param parent the parent of the view
     * @return the view of the event
     * References: https://developer.android.com/reference/android/view/View#setTag(java.lang.Object) Oracle Referenced: 2024-03-30
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        this.events.sort(eventComparator);

        if (view == null && !allEvent) {
            view = LayoutInflater.from(context).inflate(R.layout.content_event, parent,false);
        } else if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_all_event, parent,false);
        }

        Event event = events.get(position);


        
        if (!allEvent) {
            // set the text details of the event
            ((TextView) view.findViewById(R.id.event_name_text)).setText(event.getName());
            ((TextView) view.findViewById(R.id.event_date_time_text)).setText(event.getTime().toString());
            ((TextView) view.findViewById(R.id.event_status_check_in_text)).setText(event.isCurrentUserCheckedIn() ? "Checked In" : "Not Checked In");
            ((TextView) view.findViewById(R.id.event_status_sign_up_text)).setText(event.isCurrentUserSignedUp() ? "Signed Up" : "Not Signed Up");

            // null if attendee counting not implemented yet
            Integer attamt = event.getAttendeeAmount();
            if (attamt != null) {
                ((TextView) view.findViewById(R.id.event_number_of_attendees_text)).setText("Attendees: " + attamt);
            }
        } else {
            // set the text details of the event
            ((TextView) view.findViewById(R.id.all_event_name_text)).setText(event.getName());
            ((TextView) view.findViewById(R.id.all_event_date_time_text)).setText(event.getTime().toString());
            ((TextView) view.findViewById(R.id.all_event_host_name_text)).setText(event.getHost().getName());
        }


        // Set a uniquely identifiable tag for the event
        view.setTag(event.getId());

        return view;
    }

    /**
     * used to compare and sort events
     */
    Comparator<Event> eventComparator = new Comparator<Event>() {
        @Override
        public int compare(Event o1, Event o2) {
            return -o1.getTime().compareTo(o2.getTime());
        }
    };
}
