package com.example.qrcheckin.admin.allevents;

import androidx.lifecycle.ViewModel;
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
import com.example.qrcheckin.databinding.FragmentAllEventsBinding;
import com.example.qrcheckin.databinding.FragmentEventBinding;
import com.example.qrcheckin.user.myevent.EventViewModel;

public class AllEventsFragment extends Fragment {

    private FragmentAllEventsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AllEventsViewModel allEventsViewModel =
                new ViewModelProvider(this).get(AllEventsViewModel.class);

        binding = FragmentAllEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAllEvents;
        allEventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}