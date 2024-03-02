package com.example.qrcheckin.admin.allimages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllImagesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public AllImagesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is all images fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}