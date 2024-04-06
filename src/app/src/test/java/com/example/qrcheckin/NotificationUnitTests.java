package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;

import com.example.qrcheckin.core.Notification;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import org.junit.Test;

public class NotificationUnitTests {



    @Test
    public void getMessageTest() {
        Notification n = new Notification("my message", "time o'clock", "cool event", "gf73eifhf", new Timestamp(10000000, 100000));
        assertEquals(n.getMessage(), "my message");
    }

    @Test
    public void getTimeTest() {
        Notification n = new Notification("my message", "time o'clock", "cool event", "gf73eifhf", new Timestamp(10000000, 100000));
        assertEquals(n.getTime(), "time o'clock");
    }

    @Test
    public void getEventNameTest() {
        Notification n = new Notification("my message", "time o'clock", "cool event", "gf73eifhf", new Timestamp(10000000, 100000));
        assertEquals(n.getEventName(), "cool event");
    }

    @Test
    public void getEventIDTest() {
        Notification n = new Notification("my message", "time o'clock", "cool event", "gf73eifhf", new Timestamp(10000000, 100000));
        assertEquals(n.getEventId(), "gf73eifhf");
    }
    @Test
    public void getEventRealTimeTest() {
        Notification n = new Notification("my message", "time o'clock", "cool event", "gf73eifhf", new Timestamp(10000000, 100000));
        assertEquals(n.getRealTime(), new Timestamp(10000000, 100000));
    }
}
