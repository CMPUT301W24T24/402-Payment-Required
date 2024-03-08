package com.example.qrcheckin.user.attendeessignedup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentAttendeesSignedUpBinding;

import java.io.Serializable;

public class AttendeesSignedUpFragment extends Fragment {
    private FragmentAttendeesSignedUpBinding binding;
    private ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AttendeesSignedUpViewModel attendeesSignedUpViewModel =
                new ViewModelProvider(this).get(AttendeesSignedUpViewModel.class);

        binding = FragmentAttendeesSignedUpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.hostedEventListView;
        attendeesSignedUpViewModel.initializeAdaptor(getContext());

        attendeesSignedUpViewModel.getEventList().observe(getViewLifecycleOwner(), listView::setAdapter);
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
                // TODO: define it to change to the view attendee profile page
                // Navigation.findNavController(requireView()).navigate(R.id.action_nav_attendeessignedup_to...., bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
