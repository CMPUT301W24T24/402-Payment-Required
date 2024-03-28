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

public class DeleteImageFragment extends DialogFragment {
    private FragmentDeleteImageBinding binding;
    private Event event;
    private User user;
    private ImagesEventArrayAdaptor eventAdapter;
    private ImagesUserArrayAdaptor userAdapter;

    public DeleteImageFragment(@Nullable Event event, @Nullable User user, @Nullable ImagesEventArrayAdaptor eventAdapter) {
        this.event = event;
        this.user = user;
        this.eventAdapter = eventAdapter;
    }
    public DeleteImageFragment(@Nullable Event event, @Nullable User user, @Nullable ImagesUserArrayAdaptor userAdapter) {
        this.event = event;
        this.user = user;
        this.userAdapter = userAdapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = FragmentDeleteImageBinding.inflate(getLayoutInflater());
        binding.deleteImageNameText.setText(event != null ? "Event: " + event.getName() : "User: " + user.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(binding.getRoot())
                .setTitle("Delete Event")
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
