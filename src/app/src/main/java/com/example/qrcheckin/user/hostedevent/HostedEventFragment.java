package com.example.qrcheckin.user.hostedevent;

import static com.example.qrcheckin.core.Database.onEventListChanged;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventArrayAdaptor;
import com.example.qrcheckin.databinding.FragmentHostedEventBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A fragment which contains a list of events the user is hosting
 */
public class HostedEventFragment extends Fragment {

    private FragmentHostedEventBinding binding;
    private ListView listView;

    /**
     * Creates a view
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HostedEventViewModel hostedEventViewModel =
                new ViewModelProvider(this).get(HostedEventViewModel.class);

        binding = FragmentHostedEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.hostedEventListView;
//        hostedEventViewModel.initializeAdaptor(getContext());
        ArrayList<Event> eventList = new ArrayList<>();
        // TODO: refactor MutableLiveData to only array adaptor
        MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor = new MutableLiveData<>(new EventArrayAdaptor(requireContext(), eventList));

        onEventListChanged(eventList, mEventArrayAdaptor, ((QRCheckInApplication) requireContext().getApplicationContext()).getCurrentUser().getId(), "hosted", null);
        listView.setAdapter(mEventArrayAdaptor.getValue());
//        hostedEventViewModel.getEventList().observe(getViewLifecycleOwner(), listView::setAdapter);
        return root;
    }

    /**
     * When a view is created
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) listView.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", (Serializable) event);
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

    /**
     * When the view is destroyed
     */
    @Override
    public void onDestroyView() {
        Log.d("HostedEventFragment", "onDestroyView");
        super.onDestroyView();
        binding = null;
    }

}