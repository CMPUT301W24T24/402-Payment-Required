package com.example.qrcheckin.user.exploreEvent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class ExploreEventViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ExploreEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is hostedEvent fragment");
    }

    public LiveData<String> getText() { return mText; }
}

