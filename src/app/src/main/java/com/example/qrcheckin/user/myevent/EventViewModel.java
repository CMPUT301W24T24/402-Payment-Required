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

/**
 * Helps display the EventViewFragment
 */
public class EventViewModel extends ViewModel {
    /**
     * Initializes the EventViewModel
     */
    public EventViewModel() {
    }
}