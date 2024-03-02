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
import com.google.android.gms.tasks.OnFailureListener;
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
    private Map<String, String> eventsMap;
    private FirebaseFirestore db;
    private String currentUser;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        NotificationsViewModel NotificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        NotificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        notificationsMap = new HashMap<>();
        notifications = new ArrayList<>();
        notifications.add(new notification("first", "time", "nammmmeee", "evenid"));
        db = FirebaseFirestore.getInstance();

        notificationViewList = binding.notificationsList;
        nArrayAdapter = new notificationArrayAdapter(getActivity().getApplicationContext(), notifications);
        notificationViewList.setAdapter(nArrayAdapter);

        // https://stackoverflow.com/questions/69164648/adding-a-listview-in-a-fragment

        fireEventNotifs = new ArrayList<CollectionReference>();

        // get User
        currentUser = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser().getId();

        eventsMap = new HashMap<>();

        // get names of events in database
        getEventNames();

        // add listeners to notification collections of events
        for (String eventID: eventsMap.keySet()) {

            // create new collection reference and put into fireEventsNotifs
            CollectionReference notificationRef = db
                    .collection("events")
                    .document(eventID)
                    .collection("notifications");

            Log.d("Notifications", "getting notifications for event with id: " + eventID);

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

                        ArrayList<notification> testArray = new ArrayList<>();
                        testArray.add(new notification("test message", "time", "name", "id"));
                        notificationsMap.put("100", testArray);

                        notifications.clear();
                        for (String nk: notificationsMap.keySet()) {
                            notifications.addAll(notificationsMap.get(nk));
                        }
                        Log.d("Notifications", "Array is: " + notifications.toString());
                        // notifications.sort(); Sort by time of notification
                        nArrayAdapter.notifyDataSetChanged();
                    }
                }
            });
            fireEventNotifs.add(notificationRef);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getEventNames() {

        ArrayList<String> eventList = new ArrayList<>();
        final Map<String, String>[] eventsMap = new Map[]{new HashMap<>()};

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
        Map<String, String> eMap = new HashMap<>();
        // for each signed up event
        for (String event: eventList) {
            db.collection("events").document(event).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    eventsMap.put(event, documentSnapshot.getString("name"));
                    Log.d("Firestore", "successful event name get");

                    setListeners();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Firestore", "unsuccessful event name get");
                }
            });
        }
    }

    private void setListeners() {
        // add listeners to notification collections of events
        for (String eventID: eventsMap.keySet()) {

            // create new collection reference and put into fireEventsNotifs
            CollectionReference notificationRef = db
                    .collection("events")
                    .document(eventID)
                    .collection("notifications");

            Log.d("Notifications", "getting notifications for event with id: " + eventID);

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
                        Log.d("Notifications", "Array is: " + notifications.toString());
                        // notifications.sort(); Sort by time of notification
                        nArrayAdapter.notifyDataSetChanged();
                    }
                }
            });
            fireEventNotifs.add(notificationRef);
        }
    }
}