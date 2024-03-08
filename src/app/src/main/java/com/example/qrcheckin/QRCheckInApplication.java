package com.example.qrcheckin;

import android.app.Application;

import com.example.qrcheckin.core.User;

/**
 * This class allows for a user to be accessed globally
 * Reference: https://stackoverflow.com/questions/1944656/android-global-variable global variable reference, Jeff Gilfelt, PLNech, accessed 2024-02-28
 */
public class QRCheckInApplication extends Application {
    private User currentUser;

    /**
     * Gets the current user using the app
     * @return the current user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user
     * @param currentUser the current user
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
