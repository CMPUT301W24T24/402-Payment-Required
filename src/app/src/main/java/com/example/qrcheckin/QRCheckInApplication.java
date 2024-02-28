package com.example.qrcheckin;

import android.app.Application;

import com.example.qrcheckin.core.User;

public class QRCheckInApplication extends Application {
    private User currentUser;
    public User getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
