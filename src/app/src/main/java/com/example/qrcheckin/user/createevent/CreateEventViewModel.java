package com.example.qrcheckin.user.createevent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateEventViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public CreateEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is create event fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
