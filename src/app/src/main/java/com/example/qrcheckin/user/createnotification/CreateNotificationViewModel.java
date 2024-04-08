package com.example.qrcheckin.user.createnotification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * The ViewModel that shows a create notification view
 */
public class CreateNotificationViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    /**
     * Constructs CreateNotificationViewModel
     */
    public CreateNotificationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    /**
     * Gets MutableLiveData of the ViewModel
     * @return nText - the LiveData
     */
    public LiveData<String> getText() { return mText; }
}
