package com.example.qrcheckin.admin.allprofiles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllProfilesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public AllProfilesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is all profile fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}