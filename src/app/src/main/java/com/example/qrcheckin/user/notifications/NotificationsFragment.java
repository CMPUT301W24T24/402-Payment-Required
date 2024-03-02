package com.example.qrcheckin.user.notifications;

import androidx.lifecycle.Observer;
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

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.notification;
import com.example.qrcheckin.core.notificationArrayAdapter;
import com.example.qrcheckin.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    /**
     * ListView of notifications being listed
     */
    private ListView notificationViewList;
    /**
     * binding for notification fragment
     */
    private FragmentNotificationsBinding binding;
    /**
     * Array Adapter connected to notificationViewList
     * and notifications (the data list)
     */
    private notificationArrayAdapter nArrayAdapter;
    /**
     * ArrayList of Collection Reference objects
     * each collection reference is referring to a collection of notifications from user's event
     */
    private ArrayList<CollectionReference> fireEventNotifs;
    /**
     * notificationsMap allows a space for listeners of the firebase to
     */
    private Map<String, ArrayList<notification>> notificationsMap; // being changed by listener
    /**
     * notifications is the list of notifications to be displayed
     * by the ArrayAdapter
     */
    private ArrayList<notification> notifications;    // update from links



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        NotificationsViewModel NotificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        NotificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        notificationsMap = new HashMap<>();
        notifications = new ArrayList<>();

        notificationViewList = binding.notificationsList;
        nArrayAdapter = new notificationArrayAdapter(getActivity().getApplicationContext(), notifications);
        notificationViewList.setAdapter(nArrayAdapter);

        // https://stackoverflow.com/questions/69164648/adding-a-listview-in-a-fragment

        fireEventNotifs = new ArrayList<CollectionReference>();

        // get User
        String currentUser = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser().getId();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // get all events User has signed up for
        ArrayList<String> eventList = new ArrayList<>();
        db.collection("SignUpTable")
                .whereArrayContains("user_id", currentUser).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> signupDocs = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot signupDoc: signupDocs) {
                            eventList.add((String) signupDoc.get("event_id"));
                        }
                    }
                });

        // get names of events in database
        Map<String, String> eventsMap = new HashMap<>();
        for (String event: eventList) {
            db.collection("events").document(event).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    eventsMap.put(documentSnapshot.getString("name"), event);
                }
            });
        }

        // add listeners to notification collections of events
        for (String eventID: eventList) {

            // create new collection reference and put into fireEventsNotifs
            CollectionReference notificationRef = db
                    .collection("events")
                    .document(eventID)
                    .collection("notifications");

            // add listener to new collection
            notificationRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("Firestore", error.toString());
                        return;
                    }

                    if (querySnapshots != null) {
                        ArrayList<notification> notifList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc: querySnapshots) {
                            //
                            notifList.add(new notification(
                                    doc.getString("message"),
                                    doc.getString("time"),
                                    eventsMap.get(eventID),
                                    eventID));
                            notificationsMap.put(eventID, notifList);
                        }

                        // for each notification array in map, put into notifications list then update
                        notifications.clear();
                        for (String nk: notificationsMap.keySet()) {
                            notifications.addAll(notificationsMap.get(nk));
                        }
                        // notifications.sort(); Sort by time of notification
                        nArrayAdapter.notifyDataSetChanged();
                    }
                }
            });
            fireEventNotifs.add(notificationRef);
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