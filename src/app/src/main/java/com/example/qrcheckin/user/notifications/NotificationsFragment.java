package com.example.qrcheckin.user.notifications;


import androidx.lifecycle.ViewModel;
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
import android.widget.TextView;

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
import java.util.Objects;

/**
 * NotificationsFragment is the Fragment displayed when a user wants to view
 * a list of all the notifications for events they have signed up for
 */
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
    /**
     * eventsMap maps the ID of an event to the name of it.
     */
    private Map<String, String> eventsMap;
    /**
     * db is a FirestoreFirebase object
     */
    private FirebaseFirestore db;
    /**
     * currentUser is the ID of the current user
     */
    private String currentUser;

    /**
     * onCreateView initializes the attributes the Notification Fragment uses
     * and sets listeners to the notification collections in case of new notifications
     * while on fragment
     *
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

    /**
     *  onDestroyView calls the superclass method
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * getEventNames queries the Firestore and gets events the user has signed up for
     * and stores them in eventList for the next step in getNames(eventList)
     */
    private void getEventNames() {

        ArrayList<String> eventList = new ArrayList<>();

        db.collection("signUpTable")
                .whereEqualTo("user_id", currentUser).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // iterate through events the user has signed up for
                        List<DocumentSnapshot> signupDocs = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot signupDoc: signupDocs) {
                            eventList.add((String) signupDoc.get("event_id"));
                        }
                        Log.d("Firestore", "successful signup get." +
                                " signups: " + eventList.toString() +
                                ", # events signed up: " + signupDocs.size() +
                                ", user_id " + currentUser);

                        // get the names and add listeners of each event
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

    /**
     * getNames(eventList) will attach listeners to each event in eventList.
     * eventsMap will also set each eventID to its name.
     * Finally, setListener(event) will be called and set the listener for the event
     *
     * @param eventList the list of events that will have listeners set on their
     *                  notification collections
     */
    private void getNames(ArrayList<String> eventList) {

        // for each signed up event
        for (String event: eventList) {
            db.collection("events").document(event).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    // map the eventID to its name for later reference
                    eventsMap.put(event, documentSnapshot.getString("name"));
                    Log.d("Firestore", "successful event name get");

                    // set listener for event
                    setListener(event);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Firestore", "unsuccessful event name get");
                }
            });
        }
    }

    /**
     * SetListener(eventID) will attach a listener to the notifications collection of
     * the event. This is meant to look at any new notifications sent by event hosts
     * and display them to the screen
     *
     * @param eventID the id of the event that will have its notification collection
     *                listened to and displayed on fragment
     */
    private void setListener(String eventID) {

        // create new collection reference and put into fireEventsNotifs
        CollectionReference notificationRef = db
                .collection("events")
                .document(eventID)
                .collection("notifications");

        // add listener to notification collection
        notificationRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }

                // run through all notifications and set them to a temporary map
                if (querySnapshots != null) {
                    ArrayList<Notification> notifList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc: querySnapshots) {

                        // create new notification object to be displayed by list of notifications
                        notifList.add(new Notification(
                                doc.getString("message"),
                                 "Sent @ " + doc.getTimestamp("time").toDate().toString(),
                                eventsMap.get(eventID),
                                eventID));
                        notificationsMap.put(eventID, notifList);
                    }

                    // for each notification array in map, put into notifications list then update
                    notifications.clear();
                    for (String nk: notificationsMap.keySet()) {
                        notifications.addAll(notificationsMap.get(nk));
                    }
                    // future \/ \/ \/
                    // notifications.sort(); Sort by time of notification

                    // notify ArrayAdapter
                    nArrayAdapter.notifyDataSetChanged();
                }
            }
        });
        fireEventNotifs.add(notificationRef);
    }
}