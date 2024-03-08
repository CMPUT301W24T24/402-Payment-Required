package com.example.qrcheckin.user.attendeessignedup;

import static com.example.qrcheckin.core.Database.getUsersSignedUpToEvent;

import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.EventArrayAdaptor;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserArrayAdaptor;
import com.example.qrcheckin.core.UserList;

import java.util.ArrayList;

public class AttendeesSignedUpViewModel extends ViewModel {
    private final MutableLiveData<UserArrayAdaptor> mUserArrayAdapter;
    private final ArrayList<User> userList;

    public AttendeesSignedUpViewModel() {
        mUserArrayAdapter = new MutableLiveData<UserArrayAdaptor>();
        userList = new ArrayList<User>();
        // TODO: change the query to only hold the users signed up to the event
    }

    public void initializeAdaptor(Context context, Bundle bundle) {
        mUserArrayAdapter.setValue(new UserArrayAdaptor(context, userList));
        getUsersSignedUpToEvent(userList, mUserArrayAdapter, bundle.getString("event"));
    }

    public LiveData<UserArrayAdaptor> getUserList() {
        return mUserArrayAdapter;
    }
}
