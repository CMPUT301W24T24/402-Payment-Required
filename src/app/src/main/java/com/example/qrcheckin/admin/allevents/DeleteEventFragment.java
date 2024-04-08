package com.example.qrcheckin.admin.allevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.databinding.FragmentAllEventsBinding;
import com.example.qrcheckin.databinding.FragmentDeleteEventBinding;

/**
 * The fragment class that asks the admin if they would like to delete the event
 */
public class DeleteEventFragment extends DialogFragment {
    private FragmentDeleteEventBinding binding;
    private Event event;

    /**
     * Initializes the delete event fragment
     * @param event The event the admin is in the process of deleting
     */
    public DeleteEventFragment(Event event) {
        super();
        this.event = event;
    }

    /**
     * Inflates the layout for DeleteEventFragment and sets up the UI and click listeners
     * @param savedInstanceState A Bundle containing state data from the previous instance
     * @return the root view of the inflated layout
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentDeleteEventBinding.inflate(getLayoutInflater());
        ((TextView) binding.deleteEventNameText).setText(event.getName());
        ((TextView) binding.deleteEventDateTimeText).setText(event.getTime().toString());
        ((TextView) binding.deleteEventHostNameText).setText(event.getHost().getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(binding.getRoot())
                .setTitle("Delete Event")
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .setPositiveButton("Delete", (dialog, which) -> {
                    Database.deleteEvent(event.getId());
                })
                .create();
    }
}
