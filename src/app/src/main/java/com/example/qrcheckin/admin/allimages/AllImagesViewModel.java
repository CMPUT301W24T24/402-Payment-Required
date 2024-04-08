package com.example.qrcheckin.admin.allimages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * The viewmodel class for all images which can only be seen by admins
 * This has not been worked on yet
 */
public class AllImagesViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    /**
     * Initializes the AllImagesViewModel
     */
    public AllImagesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is all images fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}