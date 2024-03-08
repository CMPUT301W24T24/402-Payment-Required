package com.example.qrcheckin.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.core.User;

/**
 * ViewModel class for managing user profile data.
 * This class is responsible for keeping track of user data and providing it to the UI.
 */
public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> userLiveData;

    /**
     * Constructor to initialize the ViewModel.
     */
    public ProfileViewModel() {
        userLiveData = new MutableLiveData<>();
    }

    /**
     * Get the LiveData object containing user data.
     *
     * @return LiveData<User> representing the user data.
     */
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    /**
     * Set the value of user data.
     *
     * @param user The User object containing user information.
     */
    public void setUser(User user) {
        userLiveData.setValue(user);
    }
}
