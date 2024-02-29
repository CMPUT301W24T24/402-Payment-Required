package com.example.qrcheckin.user.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.core.notification;

import java.util.ArrayList;

public class NotificationsViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public NotificationsViewModel(ArrayList<notification> notifArray) {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}