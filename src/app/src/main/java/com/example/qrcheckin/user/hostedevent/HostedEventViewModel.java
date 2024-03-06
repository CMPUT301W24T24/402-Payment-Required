package com.example.qrcheckin.user.hostedevent;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventArrayAdaptor;
import com.example.qrcheckin.core.IncrementableInt;
import com.example.qrcheckin.core.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class HostedEventViewModel extends ViewModel {
    private final MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor;
    private final ArrayList<Event> eventList;

    public HostedEventViewModel() {
        mEventArrayAdaptor = new MutableLiveData<EventArrayAdaptor>();
        eventList = new ArrayList<Event>();
        onEventListChanged();
    }

    public void initializeAdaptor(Context context) {
        mEventArrayAdaptor.setValue(new EventArrayAdaptor(context, eventList));
    }
    // TODO: Change to query only the events hosted by the user
    private void onEventListChanged() {
        CollectionReference cr = FirebaseFirestore.getInstance().collection("events");
        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    Log.d("Firestore", "Event list changed " + querySnapshots.size());
                    eventList.clear();
                    IncrementableInt i = new IncrementableInt(0);
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        fetchHost(doc, doc.getDocumentReference("host"), i, querySnapshots.size());
                    }
                }

            }
        });
    }

    private void fetchHost(QueryDocumentSnapshot doc, DocumentReference userRef, IncrementableInt i, int size) {
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot userDoc) {
                if (userDoc.exists()) {
                    User user = new User(userDoc.getId(),
                            userDoc.getString("name"),
                            userDoc.getString("email"),
                            userDoc.getString("phone"),
                            userDoc.getString("homepage"),
                            Boolean.TRUE.equals(userDoc.getBoolean("geo")),
                            Boolean.TRUE.equals(userDoc.getBoolean("admin")),
                            userDoc.getString("imageRef")
                    );
                    Event event = new Event(doc.getId(),
                            user,
                            doc.getString("name"),
                            doc.getString("description"),
                            doc.getString("posterRef"),
                            doc.getDate("time"),
                            doc.getString("location"),
                            doc.getDouble("location_geo_lat"),
                            doc.getDouble("location_geo_long"),
                            doc.getString("checkin_id"),
                            doc.getString("checkin_qr"),
                            doc.getString("promote_id"),
                            doc.getString("promote_qr"),
                            Boolean.TRUE.equals(doc.getBoolean("geo")),
                            Objects.requireNonNull(doc.getLong("limit")).intValue(),
                            null
                    );
                    Log.d("Firestore", "User fetched " + user.getName());
                    i.increment();
                    eventList.add(event);
                    if (i.getValue() == size) {
                        Objects.requireNonNull(mEventArrayAdaptor.getValue()).notifyDataSetChanged();
                    }
                } else {
                    Log.e("Firestore", "User not found");
                }
            }
        });
    }

    public LiveData<EventArrayAdaptor> getEventList() {
        return mEventArrayAdaptor;
    }
}