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
 * ViewModel that displays the the list of events the current user is hosting
 */
public class HostedEventViewModel extends ViewModel {
    public HostedEventViewModel() {
    }
}