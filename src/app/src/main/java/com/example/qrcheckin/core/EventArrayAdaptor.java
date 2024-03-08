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

        TextView eventName = view.findViewById(R.id.event_name_text);

        eventName.setText(event.getName());

        return view;
    }
}
