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

/**
 * An array adapter for displaying notification in formation and connecting it with the ui
 */
public class NotificationArrayAdapter extends ArrayAdapter {

    private List<Notification> notifications;

    private Context context;

    /**
     * A constuctor for the adapter
     * @param context the context
     * @param notifications the notifications being displayed
     */
    public NotificationArrayAdapter(Context context, ArrayList<Notification> notifications){
        super(context,0, notifications);
        this.notifications = notifications;
        this.context = context;
    }

    /**
     * When the view is created
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     */
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
