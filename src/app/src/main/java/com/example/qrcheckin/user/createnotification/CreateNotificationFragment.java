package com.example.qrcheckin.user.createnotification;

import static com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.qrcheckin.databinding.FragmentCreateNotificationBinding;
import com.example.qrcheckin.databinding.FragmentHostedEventBinding;
import com.example.qrcheckin.user.hostedevent.HostedEventViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateNotificationFragment extends Fragment {

    private FragmentCreateNotificationBinding binding;
    private Button createNotificationButton;
    private String event_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CreateNotificationViewModel createNotificationViewModel =
                new ViewModelProvider(this).get(CreateNotificationViewModel.class);

        binding = FragmentCreateNotificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText editText = binding.createNotificationMessage;
        createNotificationViewModel.getText().observe(getViewLifecycleOwner(), editText::setText);

        event_id = getArguments().getString("event_id");

        //Bundle bundle = new Bundle();
        //bundle.put()
        //Navigation.findNavController(requireView()).navigate(R.id.action_nav_event_to_nav_view_event, bundle);

        createNotificationButton = binding.createNotificationButton;
        createNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();

                sendNotification(message, event_id);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void sendNotification( String message, String event_id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, String> databaseEntry = new HashMap<>();
        databaseEntry.put("message", message);
        databaseEntry.put("time", "time entry" );
        db.collection("events").document(event_id).collection("notifications").add(databaseEntry);

    }
}
