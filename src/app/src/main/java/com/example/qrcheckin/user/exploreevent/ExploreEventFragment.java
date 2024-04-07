package com.example.qrcheckin.user.exploreevent;

import static com.example.qrcheckin.core.Database.onEventListChanged;

import androidx.lifecycle.MutableLiveData;
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

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventArrayAdaptor;
import com.example.qrcheckin.databinding.FragmentExploreEventBinding;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The fragment for exploring available events
 */
public class ExploreEventFragment extends Fragment {

    private FragmentExploreEventBinding binding;
    private ListView listView;

    /**
     * Creates the view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ExploreEventViewModel exploreEventViewModel =
                new ViewModelProvider(this).get(ExploreEventViewModel.class);

        binding = FragmentExploreEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String search = binding.exploreEventSearchBar.getText().toString();
        listView = binding.exploreEventListView;
        ArrayList<Event> events = new ArrayList<>();
        // TODO: refactor MutableLiveData to only array adaptor
        MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor = new MutableLiveData<>(new EventArrayAdaptor(requireContext(), events));

        onEventListChanged(events, mEventArrayAdaptor, ((QRCheckInApplication) requireContext().getApplicationContext()).getCurrentUser().getId(), "explore", search.isEmpty() ? null : search);
        listView.setAdapter(mEventArrayAdaptor.getValue());

        binding.exploreEventSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Event> events = new ArrayList<>();
                // TODO: refactor MutableLiveData to only array adaptor
                MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor = new MutableLiveData<>(new EventArrayAdaptor(requireContext(), events));
                String search = binding.exploreEventSearchBar.getText().toString();
                onEventListChanged(events, mEventArrayAdaptor, ((QRCheckInApplication) requireContext().getApplicationContext()).getCurrentUser().getId(), "explore", search);
                listView.setAdapter(mEventArrayAdaptor.getValue());
            }
        });

        return root;
    }

    /**
     * When the view is created
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
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