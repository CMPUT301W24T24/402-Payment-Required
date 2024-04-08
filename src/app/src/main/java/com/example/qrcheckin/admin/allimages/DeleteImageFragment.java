package com.example.qrcheckin.admin.allimages;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.ImagesEventArrayAdaptor;
import com.example.qrcheckin.core.ImagesUserArrayAdaptor;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentDeleteImageBinding;

import javax.annotation.Nullable;

/**
 * The fragment class that asks the admin if they would like to delete the image
 */
public class DeleteImageFragment extends DialogFragment {
    private FragmentDeleteImageBinding binding;
    private Event event;
    private User user;
    private ImagesEventArrayAdaptor eventAdapter;
    private ImagesUserArrayAdaptor userAdapter;

    /**
     * Initializes the DeleteImageFragment for images referenced by an event
     * @param event the event that the image is referenced by
     * @param user the user that the image is referenced by (null)
     * @param eventAdapter the ImagesEventArrayAdaptor to be used
     */
    public DeleteImageFragment(@Nullable Event event, @Nullable User user, @Nullable ImagesEventArrayAdaptor eventAdapter) {
        this.event = event;
        this.user = user;
        this.eventAdapter = eventAdapter;
    }

    /**
     * Initializes the DeleteImageFragment for images referenced by a user
     * @param event the event that the image is referenced by (null)
     * @param user the user that the image is referenced by
     * @param userAdapter the ImagesUserArrayAdaptor to be used
     */
    public DeleteImageFragment(@Nullable Event event, @Nullable User user, @Nullable ImagesUserArrayAdaptor userAdapter) {
        this.event = event;
        this.user = user;
        this.userAdapter = userAdapter;
    }

    /**
     * Inflates the layout for DeleteImageFragment and sets up the UI and click listeners
     * @return the root view of the inflated layout
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentDeleteImageBinding.inflate(getLayoutInflater());
        binding.deleteImageNameText.setText(event != null ? "Event: " + event.getName() : "User: " + user.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(binding.getRoot())
                .setTitle("Delete Image")
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (user != null) {
                        Database.deleteImage(user, userAdapter);
                    } else {
                        Database.deleteImage(event, eventAdapter);
                    }

                })
                .create();
    }
}
