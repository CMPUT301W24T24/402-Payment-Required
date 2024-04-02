package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;

import org.junit.Test;

import java.util.Date;

public class EventTest {
    private Event mockEvent() {
        User user = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", true, false);
        UserList list = new UserList();
        list.add(user);
        Event testEvent = new Event("1", user, "TestEvent", "TestDescription", "TestRef", new Date(), "TestLocation", 0.0, 0.0, "Test", "Test", "Test", "Test", true, 10, list);
        return testEvent;
    }

    //TODO: add a valid thing in the database to check because database doesn't have the database
    @Test
    public void checkInTest() {
        Event event = mockEvent();
        User user = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", true, false);
        assertTrue(event.checkIn(user, 0.0, 0.0));
    }

    @Test
    public void haversineTest() {
        Event event = mockEvent();
        User user = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", true, false);
        assertEquals(0.0, event.haversine());
    }
}
