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

public class NotificationArrayAdapter extends ArrayAdapter {

    private List<Notification> notifications;

    private Context context;

    public NotificationArrayAdapter(Context context, ArrayList<Notification> notifications){
        super(context,0, notifications);
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.notification_layout, parent,false);
        }

        Notification notification = notifications.get(position);

        TextView message = view.findViewById(R.id.message_text);
        TextView eventName = view.findViewById(R.id.event_text);
        TextView time = view.findViewById(R.id.time_text);

        message.setText(notification.getMessage());
        eventName.setText(notification.getEventName());
        time.setText(notification.getTime());

        return view;
    }

}
