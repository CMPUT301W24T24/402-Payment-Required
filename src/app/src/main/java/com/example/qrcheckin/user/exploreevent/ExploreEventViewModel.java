package com.example.qrcheckin.user.exploreevent;

import static com.example.qrcheckin.core.Database.onEventListChanged;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.QRCheckInApplication;
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

public class ExploreEventViewModel extends ViewModel {
    private final MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor;
    private final ArrayList<Event> eventList;
    private User currentUser;
    /**
     * Initializes the ExploreEventViewModel class
     */
    public ExploreEventViewModel() {
        mEventArrayAdaptor = new MutableLiveData<EventArrayAdaptor>();
        eventList = new ArrayList<Event>();
    }
    public void initializeAdaptor(Context context) {
        mEventArrayAdaptor.setValue(new EventArrayAdaptor(context, eventList));
        onEventListChanged(eventList, mEventArrayAdaptor, ((QRCheckInApplication) context.getApplicationContext()).getCurrentUser().getId());
    }

    public LiveData<EventArrayAdaptor> getEventList() {
        return mEventArrayAdaptor;
    }
}