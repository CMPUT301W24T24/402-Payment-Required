package com.example.qrcheckin.user.hostedevent;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventArrayAdaptor;

import java.util.ArrayList;
import java.util.Objects;

/**
 * An unused viewmodel class which connects to the HostedEvent fragment
 */
public class HostedEventViewModel extends ViewModel {
//    private final MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor;
//    private final ArrayList<Event> eventList;

    public HostedEventViewModel() {
//        mEventArrayAdaptor = new MutableLiveData<EventArrayAdaptor>();
//        eventList = new ArrayList<Event>();
    }

//    public void initializeAdaptor(Context context) {
//        mEventArrayAdaptor.setValue(new EventArrayAdaptor(context, eventList));
//        onEventListChanged(eventList, mEventArrayAdaptor, ((QRCheckInApplication) context.getApplicationContext()).getCurrentUser().getId(), "hosted");
//    }

//    public LiveData<EventArrayAdaptor> getEventList() {
//        return mEventArrayAdaptor;
//    }
}