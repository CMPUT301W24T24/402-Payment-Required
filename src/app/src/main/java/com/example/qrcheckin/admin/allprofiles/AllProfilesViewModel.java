package com.example.qrcheckin.admin.allprofiles;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.core.ProfileArrayAdapter;

/**
 * The viewmodel class for all profiles which can only be seen by admins
 *
 */
public class AllProfilesViewModel extends ViewModel {
    private final MutableLiveData<ProfileArrayAdapter> mProfileArrayAdapter;

    public AllProfilesViewModel() {
        mProfileArrayAdapter = new MutableLiveData<>();
        //mProfileArrayAdapter.setValue("This is all profile fragment");
    }

    public MutableLiveData<ProfileArrayAdapter> getText() {
        return mProfileArrayAdapter;
    }

    public MutableLiveData<ProfileArrayAdapter> getProfileList() {
        return mProfileArrayAdapter;
    }
}