package com.example.qrcheckin.user.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * View Model used to display te Home page
 */
public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    /**
     * Constructs the home page ViewModel
     */
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    /**
     * returns the LiveData text
     * @return LiveData text of the home page
     */
    public LiveData<String> getText() { return mText; }
}