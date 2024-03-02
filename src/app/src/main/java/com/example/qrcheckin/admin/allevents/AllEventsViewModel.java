package com.example.qrcheckin.admin.allevents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllEventsViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public AllEventsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is all events fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}