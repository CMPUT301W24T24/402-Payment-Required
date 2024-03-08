package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;

import com.example.qrcheckin.core.Notification;

import org.junit.Test;

public class NotificationUnitTests {



    @Test
    public void getMessageTest() {
        Notification n = new Notification("my message", "time o'clock", "cool event", "gf73eifhf");
        assertEquals(n.getMessage(), "my message");
    }

    @Test
    public void getTimeTest() {
        Notification n = new Notification("my message", "time o'clock", "cool event", "gf73eifhf");
        assertEquals(n.getTime(), "time o'clock");
    }

    @Test
    public void getEventNameTest() {
        Notification n = new Notification("my message", "time o'clock", "cool event", "gf73eifhf");
        assertEquals(n.getEventName(), "cool event");
    }

    @Test
    public void getEventIDTest() {
        Notification n = new Notification("my message", "time o'clock", "cool event", "gf73eifhf");
        assertEquals(n.getEventId(), "gf73eifhf");
    }
}
