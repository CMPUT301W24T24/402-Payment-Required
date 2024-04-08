package com.example.qrcheckin.user.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.qrcheckin.core.User;

/**
 * ViewModel that displays the ProfileFragment
 */
public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> userLiveData;

    /**
     * Constructor for ProfileViewModel
     */
    public ProfileViewModel() {
        userLiveData = new MutableLiveData<>();
    }

    /**
     * Gets the user live data
     * @return the users live data
     */
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }


    /**
     * Method to update the user data in the ViewModel
     * @param user the user to set the data for
     */
    public void setUser(User user) {
        userLiveData.setValue(user);
    }
}
