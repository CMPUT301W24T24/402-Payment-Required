package com.example.qrcheckin.user.exploreEvent;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qrcheckin.R;
import com.example.qrcheckin.databinding.FragmentExploreEventBinding; // Correct import
import com.example.qrcheckin.user.exploreEvent.ExploreEventViewModel;

public class ExploreEventFragment extends Fragment {
    private FragmentExploreEventBinding binding; // Correct binding class

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExploreEventViewModel eventViewModel = new ViewModelProvider(this).get(ExploreEventViewModel.class);

        binding = FragmentExploreEventBinding.inflate(inflater, container, false); // Correct binding inflation
        View root = binding.getRoot();

        final TextView textView = binding.textExploreEvent; // Correct text view reference
        eventViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
