package com.example.qrcheckin.user.hostedevent;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.databinding.FragmentHostedEventBinding;

import java.io.Serializable;

public class HostedEventFragment extends Fragment {

    private FragmentHostedEventBinding binding;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HostedEventViewModel hostedEventViewModel =
                new ViewModelProvider(this).get(HostedEventViewModel.class);

        binding = FragmentHostedEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.hostedEventListView;
        hostedEventViewModel.initializeAdaptor(getContext());

        hostedEventViewModel.getEventList().observe(getViewLifecycleOwner(), listView::setAdapter);
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) listView.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", (Serializable) event);
                // TODO: define it to change to the edit event page
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_host_event_to_nav_edit_event, bundle);
            }
        });

        Button fab = binding.eventAddFab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_event_to_nav_create_event);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}