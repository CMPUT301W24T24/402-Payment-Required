package com.example.qrcheckin.user.browseevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.databinding.FragmentBrowseEventsBinding;
import com.example.qrcheckin.databinding.FragmentHomeBinding;
import com.example.qrcheckin.user.home.HomeFragment;
import com.example.qrcheckin.user.home.HomeViewModel;

/**
 * Provides the functionality for the Browse Event Fragment
 */
public class BrowseEventFragment extends Fragment {
    private FragmentBrowseEventsBinding binding;

    /**
     * Creates a new instance of the Browse Event Fragment
     * @return a new BrowseEventFragment
     */
    public static BrowseEventFragment newInstance() {
        return new BrowseEventFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BrowseEventViewModel browseEventViewModel =
                new ViewModelProvider(this).get(BrowseEventViewModel.class);

        binding = FragmentBrowseEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textBrowseEvent;
        browseEventViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
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
