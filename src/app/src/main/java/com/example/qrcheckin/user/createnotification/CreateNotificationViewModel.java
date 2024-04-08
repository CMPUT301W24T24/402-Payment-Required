package com.example.qrcheckin.user.createnotification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * A viewmodel class which connects with the CreateNotification fragment
 */
public class CreateNotificationViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public CreateNotificationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() { return mText; }
}
