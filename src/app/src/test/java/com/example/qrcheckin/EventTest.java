package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;

import org.junit.Test;

import java.util.Date;

public class EventTest {
    Database db = new Database();
    User currentUser = new User("e1sXj3vqg6x6qVXgK8tX", "Tina", "", "", "", true, true);
    ((QRCheckInApplication) getApplicationContext()).setCurrentUser(currentUser);
    Event mockEvent = new Event(currentUser, "Event for unit test", "", "", new Date(), "",null, null, "", "", "", "", true, 100);
    db.addEvent(mockEvent);



}
