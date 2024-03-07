package com.example.qrcheckin.user.createevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.databinding.FragmentCreateEventBinding;

public class CreateEventFragment extends Fragment {
    private FragmentCreateEventBinding binding;

    public static CreateEventFragment newInstance() {
        return new CreateEventFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CreateEventViewModel createEventViewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);

        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // banner
        final ImageView bannerImageView = binding.imageCreateEventBanner;
        //createEventViewModel.getText().observe(getViewLifecycleOwner(), bannerImageView::setImage...);

        // title
        final TextView textView = binding.textCreateEventTitle;
        createEventViewModel.getEventTitle().observe(getViewLifecycleOwner(), textView::setText);

        // date
        final TextView dateTextView = binding.dateCreateEvent;
        createEventViewModel.getEventDate().observe(getViewLifecycleOwner(), dateTextView::setText);

        // time
        final TextView timeTextView = binding.timeCreateEvent;
        createEventViewModel.getEventTime().observe(getViewLifecycleOwner(), timeTextView::setText);

        // description
        final TextView descriptionTextView = binding.textCreateEventDescription;
        createEventViewModel.getEventDescription().observe(getViewLifecycleOwner(), descriptionTextView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
