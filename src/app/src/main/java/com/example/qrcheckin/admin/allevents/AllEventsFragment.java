package com.example.qrcheckin.admin.allevents;

import static com.example.qrcheckin.core.Database.onEventListChanged;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
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
import android.widget.TextView;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventArrayAdaptor;
import com.example.qrcheckin.databinding.FragmentAllEventsBinding;
import com.example.qrcheckin.databinding.FragmentEventBinding;
import com.example.qrcheckin.user.myevent.EventFragment;
import com.example.qrcheckin.user.myevent.EventViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * The fragment class for all events which can only be seen by admins
 * This has not been worked on yet
 */
public class AllEventsFragment extends Fragment {

    private FragmentAllEventsBinding binding;

    private ListView listView;

    /**
     * Inflates the layout for AllEventsFragment and sets up the UI and click listeners
     * for the list view and search button
     * @param inflater The LayoutInflater object used to inflate the xml
     * @param container The ViewGroup that the inflated view will be added to
     * @param savedInstanceState A Bundle containing state data from the previous instance
     * @return the root view of the inflated layout
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AllEventsViewModel allEventsViewModel =
                new ViewModelProvider(this).get(AllEventsViewModel.class);

        binding = FragmentAllEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listView = binding.allEventListview;

        ArrayList<Event> events = new ArrayList<>();
        // TODO: refactor MutableLiveData to only array adaptor
        MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor = new MutableLiveData<>(new EventArrayAdaptor(requireContext(), events, true));

        // TODO: Change the type to "all"
        onEventListChanged(events, mEventArrayAdaptor, ((QRCheckInApplication) requireContext().getApplicationContext()).getCurrentUser().getId(), "explore", null);
        listView.setAdapter(mEventArrayAdaptor.getValue());

        /**
         * Allows admins to click on items in the event list view
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) listView.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", event);
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_all_event_to_nav_view_event, bundle);
            }
        });

        /**
         * Deletes events from the event list view if the admin long clicks on an event in the event list
         */
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) listView.getItemAtPosition(position);
                DeleteEventFragment deleteEventFragment = new DeleteEventFragment(event);
                deleteEventFragment.show(getParentFragmentManager(), "delete event");
                return true;
            }
        });

        /**
         * Allows the admin to click on the search button and see a filtered list of events
         */
        binding.allEventSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Event> events = new ArrayList<>();
                // TODO: refactor MutableLiveData to only array adaptor
                MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor = new MutableLiveData<>(new EventArrayAdaptor(requireContext(), events));
                String search = binding.allEventSearchBar.getText().toString();
                onEventListChanged(events, mEventArrayAdaptor, ((QRCheckInApplication) requireContext().getApplicationContext()).getCurrentUser().getId(), "explore", search);
                listView.setAdapter(mEventArrayAdaptor.getValue());
            }
        });

        return root;
    }

    /**
     * Sets the binding to null when the view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}