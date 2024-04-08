package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventList;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;
import com.google.type.DateTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

public class EventListTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    QRCheckInApplication app;
    Database db;
    User currentUser;


    @Before
    public void getApplication() throws InterruptedException {

        // Get the application
        Log.d("QRCheckIn", "getApplication");
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context appContext = instrumentation.getTargetContext();
        app = (QRCheckInApplication) appContext.getApplicationContext();
        if (app == null) {
            app = (QRCheckInApplication) app.getApplicationContext();
        }
        if (app == null) {
            Log.d("QRCheckIn", "The app is null");
        }
        Thread.sleep(2500);

        db = new Database();
        currentUser = app.getCurrentUser();
    }

    private Event mockEvent() {
        User user = currentUser;
        UserList list = new UserList();
        list.add(user);
        Event testEvent = new Event("1", user, "TestEvent", "TestDescription", "TestRef", new Date(), "TestLocation", 0.0, 0.0, "Test", "Test", "Test", "Test", false, 10, list);
        return testEvent;
    }

    @Test
    public void testAdd() {
        EventList eventList = new EventList(); // Create an instance of EventList
        Event mockEvent = mockEvent();
        eventList.add(mockEvent); // Call the add method on the instance
        assertEquals(1, eventList.countEvents());
    }

    @Test
    public void testAddException() {
        EventList eventList = new EventList();
        Event mockEvent = mockEvent();
        eventList.add(mockEvent);
        assertThrows(IllegalArgumentException.class, () -> {
            eventList.add(mockEvent);
        });
    }

    @Test
    public void testHas() {
        EventList eventList = new EventList();
        Event mockEvent = mockEvent();
        assertFalse(eventList.hasEvent(mockEvent));
        eventList.add(mockEvent); //add the event in the event list
        assertTrue(eventList.hasEvent(mockEvent));
    }

    @Test
    public void testRemoveEvent() {
        EventList eventList = new EventList();
        Event mockEvent = mockEvent();
        eventList.add(mockEvent);
        assertEquals(1, eventList.countEvents());
        eventList.removeEvent(mockEvent);
        assertEquals(0, eventList.countEvents());
    }

    @Test
    public void testRemoveEventException() {
        EventList eventList = new EventList();
        Event mockEvent = mockEvent();
        assertThrows(IllegalArgumentException.class, () -> {
            eventList.removeEvent(mockEvent);
        });
    }

    @Test
    public void testCountEvents() {
        EventList eventList = new EventList();
        assertEquals(0, eventList.countEvents());
        eventList.add(mockEvent());
        assertEquals(1, eventList.countEvents());
    }
}