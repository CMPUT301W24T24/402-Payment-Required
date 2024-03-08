package com.example.qrcheckin.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.core.User;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> userLiveData;

    public ProfileViewModel() {
        userLiveData = new MutableLiveData<>();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    // Method to update the user data in the ViewModel
    public void setUser(User user) {
        userLiveData.setValue(user);
    }
}
