package com.example.qrcheckin.user.exploreevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.databinding.FragmentExploreEventBinding;

/**
 * Provides the functionality for the Browse Event Fragment
 */
public class ExploreEventFragment extends Fragment {
    private FragmentExploreEventBinding binding;

    /**
     * Creates a new instance of the Browse Event Fragment
     * @return a new ExploreEventFragment
     */
    public static ExploreEventFragment newInstance() {
        return new ExploreEventFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExploreEventViewModel exploreEventViewModel =
                new ViewModelProvider(this).get(ExploreEventViewModel.class);

        binding = FragmentExploreEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textExploreEvent;
        exploreEventViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    /**
     * A function used to destroy the view once the user left the app
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
