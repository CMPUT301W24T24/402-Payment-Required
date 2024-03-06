package com.example.qrcheckin.user.browseevent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Creates a specialized ViewModel for the browse event fragment
 */
public class BrowseEventViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    /**
     * Initializes the BrowseEventViewModel class
     */
    public BrowseEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is browseEvent fragment");
    }

    public LiveData<String> getText() { return mText; }
}
