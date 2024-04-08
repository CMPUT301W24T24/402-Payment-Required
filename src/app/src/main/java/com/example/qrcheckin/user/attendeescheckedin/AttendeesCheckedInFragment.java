package com.example.qrcheckin.user.attendeescheckedin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentAttendeesCheckedInBinding;
import com.example.qrcheckin.databinding.FragmentAttendeesSignedUpBinding;
import com.example.qrcheckin.user.attendeessignedup.AttendeesSignedUpViewModel;

import java.io.Serializable;

/**
 *  The attendees checked in fragment which contains a list of users that have checked into the organizers event
 */
public class AttendeesCheckedInFragment extends Fragment {
    private FragmentAttendeesCheckedInBinding binding;
    private ListView listView;
    private TextView textView;

    /**
     * Creates the view
     * References: https://stackoverflow.com/questions/20328944/android-listview-show-a-message-if-arraylist-of-arrayadapter-is-empty/20329154#20329154 nsL Accessed Mar 20 2023
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself but this can be used to generate the
     *                  LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The fragments bindings root
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AttendeesCheckedInViewModel attendeesCheckedInViewModel =
                new ViewModelProvider(this).get(AttendeesCheckedInViewModel.class);

        binding = FragmentAttendeesCheckedInBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.attendeesCheckedInListView;
        attendeesCheckedInViewModel.initializeAdaptor(getContext(), getArguments().getSerializable("event") != null ? (Event) getArguments().getSerializable("event") : null);
        attendeesCheckedInViewModel.getUserList().observe(getViewLifecycleOwner(), listView::setAdapter);

        textView = binding.checkinListEmptyTextView;
        listView.setEmptyView(textView);

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) listView.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", (Serializable) user);
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_attendeescheckedin_to_nav_view_profile, bundle);
            }
        });
    }

    /**
     * Destroys the view once the user has left the app
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
