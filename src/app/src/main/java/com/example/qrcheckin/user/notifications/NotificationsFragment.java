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
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
     * db is a FirestoreFirebase object
     */
    private FirebaseFirestore db;
    /**
     * currentUser is the ID of the current user
     */
    private String currentUser;

    private Map<String, String> eventIdToName;
    private Set<ListenerRegistration> notificationCollections;
    private Map<String, Boolean> eventMap;
    private Map<String, Boolean> eventMapSignup;
    private Map<String, Boolean> eventMapCheckin;
    private FirebaseFirestore notidb;
    private ListenerRegistration signups;
    private ListenerRegistration checkins;
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


        // get names of events in database and set listeners to them
        createNotificationListeners();
        return root;
    }

    /**
     *  onDestroyView calls the superclass method
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        checkins.remove();
        signups.remove();
        binding = null;
    }

    /**
     * Creates listeners on the notification collection of each event
     * the user has signed up for
     */
    public void createNotificationListeners() {
        eventMap = new HashMap<>();
        eventIdToName = new HashMap<>();
        eventMapSignup = new HashMap<>();
        eventMapCheckin = new HashMap<>();
        notificationCollections = new HashSet<>();
        notidb = FirebaseFirestore.getInstance();

        signupListener();
        checkinListener();
    }

    /**
     * creates listener for the signup table to display notifications for relevant events
     */
    public void signupListener() {
        signups = notidb.collection("signUpTable").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {

                try {
                    eventMapSignup.clear();
                    // add each event the user has signed up for
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (doc.get("user_id").toString().equals(currentUser) ) {

                            // add event to list
                            String event = doc.get("event_id").toString();
                            eventMapSignup.put(event, Boolean.FALSE);
                        }
                    }
                    eventMap.clear();
                    eventMap.putAll(eventMapCheckin);
                    eventMap.putAll(eventMapSignup);

                    // get names of events and set listeners
                    for (ListenerRegistration notiRef: notificationCollections) {
                        notiRef.remove();
                    }
                    Log.d("Notifications", "removed notification listeners");
                    notificationCollections.clear();
                    getNames();

                } catch (Exception e) {
                    Log.d("Notifications", e.toString());
                }
            }
        });
    }

    /**
     * creates listener for the checkin table to display notifications for relevant events
     */
    public void checkinListener() {
        checkins = notidb.collection("checkins").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {

                try {
                    eventMapCheckin.clear();
                    // add each event the user has signed up for
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (doc.get("user_id").toString().equals(currentUser) ) {

                            // add event to list
                            String event = doc.get("event_id").toString();
                            eventMapCheckin.put(event, Boolean.FALSE);
                        }
                    }
                    eventMap.clear();
                    eventMap.putAll(eventMapCheckin);
                    eventMap.putAll(eventMapSignup);

                    // get names of events and set listeners

                    for (ListenerRegistration notiRef: notificationCollections) {
                        notiRef.remove();
                    }
                    Log.d("Notifications", "removed notification listeners");
                    notificationCollections.clear();
                    getNames();

                } catch (Exception e) {
                    Log.d("Notifications", e.toString());
                }
            }
        });
    }

    /**
     * getNames will get the name of all events specified in the list of event IDs.
     * This will also set a listener to each events notification collection
     */
    private void getNames() {
        // for each signed up event
        Log.d("Notifications", "Amount of listeners: " + eventMap.size());

        for (String event: eventMap.keySet()) {
            notidb.collection("events").document(event).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    eventIdToName.put(event, documentSnapshot.getString("name"));
                    Log.d("Firestore", "successful event name get");

                    // next step in listener setup
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

                // run through all notifications
                if (querySnapshots != null) {
                    ArrayList<Notification> notifList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc: querySnapshots) {

                        // create new notification object to be displayed by list of notifications

                        String time;
                        Timestamp realTime = doc.getTimestamp("time");
                        if (realTime != null) {
                            time = "Sent @ " + realTime.toDate().toString();
                        } else {
                            time = "Sent @ TIME UNAVAILABLE";
                        }

                        notifList.add(new Notification(
                                doc.getString("message"),
                                 time,
                                eventIdToName.get(eventID),
                                eventID,
                                realTime));
                        notificationsMap.put(eventID, notifList);
                    }

                    // for each notification array in map, put into notifications list then update
                    notifications.clear();
                    for (String nk: notificationsMap.keySet()) {
                        notifications.addAll(notificationsMap.get(nk));
                    }
                    // Sort by time of notification
                    notifications.sort(notificationComparator);
                    // notify ArrayAdapter
                    nArrayAdapter.notifyDataSetChanged();
                }
            }
        });
        fireEventNotifs.add(notificationRef);
    }


    /**
     * Reference:
     * https://ioflood.com/blog/sort-array-java/#:~:text=Java%20provides%20a%20built%2Din,%2C%20string%2C%20or%20custom%20objects.
     * by Gabriel Ramuglia, Accessed 2024-03-28
     */
    Comparator<Notification> notificationComparator = new Comparator<Notification>() {
        @Override
        public int compare(Notification o1, Notification o2) {
            try {
                long d1 = o1.getRealTime().toDate().getTime();
                long d2 = o2.getRealTime().toDate().getTime();

                if (d1 < d2) {
                    return 1;
                }
                else if (d1 == d2) {
                    return 0;
                }
                else {
                    return -1;
                }
            }
            catch (Exception e) {
                Log.d("Notifications", "comparison error");
                return 0;
            }

        }
    };
}