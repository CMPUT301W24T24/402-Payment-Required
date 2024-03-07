package com.example.qrcheckin.user.exploreevent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Creates a specialized ViewModel for the explore event fragment
 */
public class ExploreEventViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    /**
     * Initializes the ExploreEventViewModel class
     */
    public ExploreEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is exploreEvent fragment");
    }

    public LiveData<String> getText() { return mText; }
}
