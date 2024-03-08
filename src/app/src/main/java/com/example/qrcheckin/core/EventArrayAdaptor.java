package com.example.qrcheckin.core;

import android.content.Context;
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

public class EventArrayAdaptor extends ArrayAdapter<Event> {
    interface CreateListener {
        void createEvent(int position);
    }
    private ArrayList<Event> events;
    private Context context;


    public EventArrayAdaptor(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_event, parent,false);
        }

        Event event = events.get(position);

        ((TextView) view.findViewById(R.id.event_name_text)).setText(event.getName());
        ((TextView) view.findViewById(R.id.event_date_time_text)).setText(event.getTime().toString());
        ((TextView) view.findViewById(R.id.event_status_check_in_text)).setText(event.isCurrentUserCheckedIn() ? "Checked In" : "Not Checked In");
        ((TextView) view.findViewById(R.id.event_status_sign_up_text)).setText(event.isCurrentUserSignedUp() ? "Signed Up" : "Not Signed Up");

        return view;
    }
}
