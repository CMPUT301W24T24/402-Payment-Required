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
 * A ViewModel used to display the list of events to explore
 */
public class ExploreEventViewModel extends ViewModel {
    /**
     * Initializes the ExploreEventViewModel class
     */
    public ExploreEventViewModel() {
    }
}