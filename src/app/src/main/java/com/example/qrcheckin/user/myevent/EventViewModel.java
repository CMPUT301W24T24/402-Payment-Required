package com.example.qrcheckin.user.myevent;

import static com.example.qrcheckin.core.Database.onEventListChanged;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventArrayAdaptor;

import java.util.ArrayList;

public class EventViewModel extends ViewModel {
//    private final MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor;
//    private final ArrayList<Event> eventList;

    public EventViewModel() {
//        mEventArrayAdaptor = new MutableLiveData<EventArrayAdaptor>();
//        eventList = new ArrayList<Event>();
    }

//    public void initializeAdaptor(Context context) {
//        mEventArrayAdaptor.setValue(new EventArrayAdaptor(context, eventList));
//        onEventListChanged(eventList, mEventArrayAdaptor, ((QRCheckInApplication) context.getApplicationContext()).getCurrentUser().getId(), "my");
//    }

//    public LiveData<EventArrayAdaptor> getEventList() {
//        return mEventArrayAdaptor;
//    }
}