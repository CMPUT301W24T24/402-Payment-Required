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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * CreateNotificationFragment is the Fragment an event organizer will see if they
 * want to send a notification. The user has a space to write text that will be sent
 * and a button to send that message.
 */
public class CreateNotificationFragment extends Fragment {

    /**
     * binding of the Fragment
     */
    private FragmentCreateNotificationBinding binding;
    /**
     * Button that will send the notificatio when pushed
     */
    private Button createNotificationButton;
    /**
     * id of the event the notification is being sent from
     */
    private String event_id;

    /**
     * onCreateView initializes the button and supporting data that will be used in the Fragment
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View
     */
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

                pushNotification(message, event_id);
            }
        });

        return root;
    }

    /**
     * calls superclass to destroy the view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * sendNotificaiton(message, event_id) sends a notification to the Firestore
     * which will be sent to all attendees of the event
     *
     * @param message the message of the notification
     * @param event_id the eventID the notification came from
     */
    public void pushNotification( String message, String event_id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> databaseEntry = new HashMap<>();
        databaseEntry.put("message", message);
        FieldValue serverTimestamp = FieldValue.serverTimestamp();
        databaseEntry.put("time", serverTimestamp);
        db.collection("events").document(event_id).collection("notifications").add(databaseEntry);

    }
}
