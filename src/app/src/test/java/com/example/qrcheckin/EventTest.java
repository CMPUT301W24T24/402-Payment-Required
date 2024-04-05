package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.Optional;

@RunWith(AndroidJUnit4.class) //TODO: check with dependency to do


public class EventTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    QRCheckInApplication app;
    @Before
    public void getApplication() {

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
    }

    public Event getMockEvent() {
        return new Event(currentUser, "Event for unit test", "", "", new Date(), "",0.00, 0.00, "", "", "", "", true, 100);
    }

    Database db = new Database();
    User currentUser = app.getCurrentUser();

    @Test
    public void checkInTest() {
        Event mockEvent = getMockEvent();
        db.addEvent(mockEvent);
        Boolean checkInVerification = mockEvent.checkIn(currentUser, 0.00, 0.00);
        assertTrue(checkInVerification);
    }

    @Test
    public void setGeolocationDataTest() {
        Event mockEvent = getMockEvent();
        assertNotNull(mockEvent);
        assertEquals(Optional.ofNullable(mockEvent.getLocationGeoLat()), 0);
        assertEquals(Optional.ofNullable(mockEvent.getLocationGeoLong()), 0);
        mockEvent.setGeolocationData(12.346, 67.891);
        assertEquals(Optional.ofNullable(mockEvent.getLocationGeoLat()), 12.346);
        assertEquals(Optional.ofNullable(mockEvent.getLocationGeoLong()), 67.891);
    }

    @Test
    public void setCurrentUserSignedUpTest() {
        //
    }
}
