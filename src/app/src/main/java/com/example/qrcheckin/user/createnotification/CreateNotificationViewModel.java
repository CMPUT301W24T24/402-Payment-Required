package com.example.qrcheckin.user.createnotification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateNotificationViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public CreateNotificationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Write your notification here!");
    }

    public LiveData<String> getText() { return mText; }
}
