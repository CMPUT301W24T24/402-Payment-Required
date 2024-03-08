package com.example.qrcheckin.user.notifications;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.Notification;
import com.example.qrcheckin.core.NotificationArrayAdapter;
import com.example.qrcheckin.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private NotificationArrayAdapter nArrayAdapter;
    /**
     * ArrayList of Collection Reference objects
     * each collection reference is referring to a collection of notifications from user's event
     */
    private ArrayList<CollectionReference> fireEventNotifs;
    /**
     * notificationsMap allows a space for listeners of the firebase to
     */
    private Map<String, ArrayList<Notification>> notificationsMap; // being changed by listener
    /**
     * notifications is the list of notifications to be displayed
     * by the ArrayAdapter
     */
    private ArrayList<Notification> notifications;    // update from links
    private Map<String, String> eventsMap;
    private FirebaseFirestore db;
    private String currentUser;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        NotificationsViewModel NotificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notificationsMap = new HashMap<>();
        notifications = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        notificationViewList = binding.notificationsList;
        nArrayAdapter = new NotificationArrayAdapter(getActivity().getApplicationContext(), notifications);
        notificationViewList.setAdapter(nArrayAdapter);

        fireEventNotifs = new ArrayList<CollectionReference>();

        // get User
        currentUser = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser().getId();

        eventsMap = new HashMap<>();

        // get names of events in database and set listeners to them
        getEventNames();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getEventNames() {

        ArrayList<String> eventList = new ArrayList<>();

        db.collection("SignUpTable")
                .whereEqualTo("user_id", currentUser).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> signupDocs = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot signupDoc: signupDocs) {
                            eventList.add((String) signupDoc.get("event_id"));
                        }
                        Log.d("Firestore", "successful signup get." +
                                " signups: " + eventList.toString() +
                                ", # events signed up: " + signupDocs.size() +
                                ", user_id " + currentUser);

                        getNames(eventList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "unsuccessful signup get" +
                                " user_id " + currentUser);
                    }
                });
    }

    private void getNames(ArrayList<String> eventList) {
        // for each signed up event
        for (String event: eventList) {
            db.collection("events").document(event).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    eventsMap.put(event, documentSnapshot.getString("name"));
                    Log.d("Firestore", "successful event name get");

                    setListeners(event);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Firestore", "unsuccessful event name get");
                }
            });
        }
    }

    private void setListeners(String eventID) {
        // add listeners to notification collections of events

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
                    ArrayList<Notification> notifList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        //
                        notifList.add(new Notification(
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
                    Log.d("Notification", "received new notification. Displayed in notifications fragment");
                }
            }
        });
        fireEventNotifs.add(notificationRef);
    }
}