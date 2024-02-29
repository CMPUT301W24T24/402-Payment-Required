package com.example.qrcheckin;

import android.app.Application;

import com.example.qrcheckin.core.User;

// https://stackoverflow.com/questions/1944656/android-global-variable global variable reference, Jeff Gilfelt, PLNech, accessed 2024-02-28
public class QRCheckInApplication extends Application {
    private User currentUser;
    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
