package com.example.qrcheckin.user.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * A viewmodel class which connects to the Home fragment
 */
public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() { return mText; }
}