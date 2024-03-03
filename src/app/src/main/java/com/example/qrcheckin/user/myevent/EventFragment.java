package com.example.qrcheckin.user.myevent;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.EventArrayAdaptor;
import com.example.qrcheckin.core.EventList;
import com.example.qrcheckin.databinding.FragmentEventBinding;
import com.example.qrcheckin.databinding.FragmentHostedEventBinding;
import com.example.qrcheckin.user.hostedevent.HostedEventViewModel;

import java.util.ArrayList;

public class EventFragment extends Fragment {

    private FragmentEventBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventViewModel eventViewModel =
                new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ListView listView = binding.eventListView;
        eventViewModel.initializeAdaptor(getContext());

        eventViewModel.getEventList().observe(getViewLifecycleOwner(), listView::setAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}