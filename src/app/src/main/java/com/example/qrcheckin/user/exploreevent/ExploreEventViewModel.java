package com.example.qrcheckin.user.exploreevent;

import static com.example.qrcheckin.core.Database.onEventListChanged;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventArrayAdaptor;
import com.example.qrcheckin.core.User;

import java.util.ArrayList;

/**
 * An unused viewmodel class for the ExploreEvent fragment
 */
public class ExploreEventViewModel extends ViewModel {
//    private final MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor;
//    private final ArrayList<Event> eventList;
//    private User currentUser;
    /**
     * Initializes the ExploreEventViewModel class
     */
    public ExploreEventViewModel() {
//        mEventArrayAdaptor = new MutableLiveData<EventArrayAdaptor>();
//        eventList = new ArrayList<Event>();
    }
//    public void initializeAdaptor(Context context) {
//        mEventArrayAdaptor.setValue(new EventArrayAdaptor(context, eventList));
//        onEventListChanged(eventList, mEventArrayAdaptor, ((QRCheckInApplication) context.getApplicationContext()).getCurrentUser().getId(), "explore");
//    }

//    public LiveData<EventArrayAdaptor> getEventList() {
//        return mEventArrayAdaptor;
//    }
}