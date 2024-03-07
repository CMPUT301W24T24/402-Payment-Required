package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;

import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventList;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;

import org.junit.Test;

public class EventListTest {
    private Event mockEvent() {
        User user = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", false, false);
        UserList list = new UserList();
        list.add(user);
        return new Event("1", "Start", user, list);
    }

    @Test
    public void testAdd() {
        EventList eventList = new EventList(); // Create an instance of EventList
        Event mockEvent = mockEvent();
        eventList.add(mockEvent); // Call the add method on the instance
        assertEquals(1, eventList.countEvents());
    }
}

