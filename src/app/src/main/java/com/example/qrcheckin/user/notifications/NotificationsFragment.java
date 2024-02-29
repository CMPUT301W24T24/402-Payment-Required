package com.example.qrcheckin.user.notifications;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.notification;
import com.example.qrcheckin.core.notificationArrayAdapter;
import com.example.qrcheckin.databinding.FragmentNotificationsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private ListView notificationList;
    private FragmentNotificationsBinding binding;
    private ArrayList<notification> notifications;    // update from links
    private notificationArrayAdapter nArrayAdapter;
    private ArrayList<CollectionReference> fireEventNotifs;
    private Map<String, ArrayList<notification>> links; // being changed by listener


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel NotificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        NotificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        notifications = new ArrayList<>();

        notificationList =

        notificationList.setAdapter(nArrayAdapter);

        // https://stackoverflow.com/questions/69164648/adding-a-listview-in-a-fragment

        // attach ListView


        CollectionReference signups = FirebaseFirestore.getInstance().collection("SignUpTable");

        QuerySnapshot signupQuery = signups.get().getResult();

        for ( QueryDocumentSnapshot s: signupQuery) {
            if (s.get("user_id") == userID ) {

            }
        }


        // https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html
        // initialize all arrays that will be
        // append all relevant ????

        // need: list of collection references. Initialized

        // Create snapshot listener for each collection.
        // Do not have to worry about new listeners because user can't sign up for events on this page
        //


        for (String eventID: links.keySet()) { // for eventID in user events

            fireNotifs.document( eventID ).collection("notifications");
            fireNotifs.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("Firestore", error.toString());
                        return;
                    }

                    if (querySnapshots != null) {
                        links.get(eventID).clear();
                        for (QueryDocumentSnapshot doc: querySnapshots) {
                            String message = doc.getString("message");
                            String time = doc.getString("time");
                            links.get(eventID).add(new notification(message, time, eventName));
                        }

                        notifications = links.values(); // for each link in links put into notifications list then update
                        nArrayAdapter.notifyDataSetChanged();
                    }

                }
            });
        }
        // for each event in events user signed up to
        //    for notification in notifications of events
        //        ?? possibly skip notifications that user removed and store removed items to phone
        //        add notification to notifications

        // sort list of notifications so early ones are first
        // set linearLayout to display notifications


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}