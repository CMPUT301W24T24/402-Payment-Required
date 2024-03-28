package com.example.qrcheckin.user.myevent;

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
import com.example.qrcheckin.databinding.FragmentEventBinding;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Fragment which contains a list of the events a user has checked into
 */
public class EventFragment extends Fragment {

    private FragmentEventBinding binding;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventViewModel eventViewModel =
                new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.eventListView;
//        eventViewModel.initializeAdaptor(getContext());
        ArrayList<Event> events = new ArrayList<>();
        // TODO: refactor MutableLiveData to only array adaptor
        MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor = new MutableLiveData<>(new EventArrayAdaptor(getContext(), events));

        onEventListChanged(events, mEventArrayAdaptor, ((QRCheckInApplication) requireContext().getApplicationContext()).getCurrentUser().getId(), "my");
        listView.setAdapter(mEventArrayAdaptor.getValue());
//        eventViewModel.getEventList().observe(getViewLifecycleOwner(), listView::setAdapter);

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
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_event_to_nav_view_event, bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}