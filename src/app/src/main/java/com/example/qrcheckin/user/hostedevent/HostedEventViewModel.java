package com.example.qrcheckin.user.hostedevent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HostedEventViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public HostedEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is hostedEvent fragment");
    }

    public LiveData<String> getText() { return mText; }
}