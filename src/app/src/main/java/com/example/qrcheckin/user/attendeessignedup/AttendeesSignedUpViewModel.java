package com.example.qrcheckin.user.attendeessignedup;

import static com.example.qrcheckin.core.Database.getUsersSignedUpToEvent;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventArrayAdaptor;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserArrayAdaptor;
import com.example.qrcheckin.core.UserList;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class AttendeesSignedUpViewModel extends ViewModel {
    private final MutableLiveData<UserArrayAdaptor> mUserArrayAdapter;
    private final ArrayList<User> userList;

    public AttendeesSignedUpViewModel() {
        mUserArrayAdapter = new MutableLiveData<UserArrayAdaptor>();
        userList = new ArrayList<User>();
    }

    /**
     * Initializes the UserArrayAdaptor to the list of users signed up to the event
     * @param context The context of the attendee signed up fragment
     * @param event The event that users have signed up to
     */
    public void initializeAdaptor(Context context, Event event) {
        mUserArrayAdapter.setValue(new UserArrayAdaptor(context, userList));
        getUsersSignedUpToEvent(userList, mUserArrayAdapter, event.getId());
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