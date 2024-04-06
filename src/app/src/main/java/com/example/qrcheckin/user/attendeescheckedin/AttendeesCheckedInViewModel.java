package com.example.qrcheckin.user.attendeescheckedin;

import static com.example.qrcheckin.core.Database.getUsersCheckedIntoEvent;
import static com.example.qrcheckin.core.Database.getUsersSignedUpToEvent;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserArrayAdaptor;

import java.util.ArrayList;

public class AttendeesCheckedInViewModel extends ViewModel {
    private final MutableLiveData<UserArrayAdaptor> mUserArrayAdapter;
    private final ArrayList<User> userList;

    public AttendeesCheckedInViewModel() {
        mUserArrayAdapter = new MutableLiveData<UserArrayAdaptor>();
        userList = new ArrayList<User>();
    }

    /**
     * Initializes the UserArrayAdaptor to the list of users checked into the event
     * @param context The context of the attendee checked in fragment
     * @param event The event that users have checked into
     */
    public void initializeAdaptor(Context context, Event event) {
        mUserArrayAdapter.setValue(new UserArrayAdaptor(context, userList, event));
        getUsersCheckedIntoEvent(userList, mUserArrayAdapter, event.getId());
        Log.d("userList", userList.toString());
        Log.d("Event Bundle",event.getId());
    }

    /**
     * Gets the Mutable UserArrayAdapter list
     * @return the MutableLiveData list of UserArrayAdapters
     */
    public LiveData<UserArrayAdaptor> getUserList() {
        return mUserArrayAdapter;
    }
}
