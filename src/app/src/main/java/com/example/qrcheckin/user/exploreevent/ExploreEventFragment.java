package com.example.qrcheckin.user.exploreevent;

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
import android.widget.ListView;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.databinding.FragmentExploreEventBinding;

import java.io.Serializable;

public class ExploreEventFragment extends Fragment {

    private FragmentExploreEventBinding binding;
    private ListView listView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ExploreEventViewModel exploreEventViewModel =
                new ViewModelProvider(this).get(ExploreEventViewModel.class);

        binding = FragmentExploreEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.exploreEventListView;
        exploreEventViewModel.initializeAdaptor(getContext());

        exploreEventViewModel.getEventList().observe(getViewLifecycleOwner(), listView::setAdapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) listView.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", (Serializable) event);
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_explore_event_to_nav_view_event, bundle);
            }
        });
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