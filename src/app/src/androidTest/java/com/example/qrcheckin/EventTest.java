package com.example.qrcheckin;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.services.events.TimeStamp;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@RunWith(AndroidJUnit4.class)


public class EventTest {
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

    public Event getMockEvent() {
        return new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
    }


    @Test
    public void checkInTest() throws InterruptedException {
        Thread.sleep(3000);
        Event mockEvent = getMockEvent();
        db.addEvent(mockEvent);
        User user = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", false, false);
        assertFalse(mockEvent.checkIn(user));
        Boolean checkInVerification = mockEvent.checkIn(currentUser, 0.00, 0.00);
        assertTrue(checkInVerification);
    }

    @Test
    public void setGeolocationDataTest() throws InterruptedException {
        Thread.sleep(3000);
        Event mockEvent = getMockEvent();
        assertNotNull(mockEvent);
        assertEquals(Optional.ofNullable(mockEvent.getLocationGeoLat()), Optional.of(0.0));
        assertEquals(Optional.ofNullable(mockEvent.getLocationGeoLong()), Optional.of(0.0));

        mockEvent.setGeolocationData(12.346, 67.891);
        assertEquals(Optional.ofNullable(mockEvent.getLocationGeoLat()), Optional.of(12.346));
        assertEquals(Optional.ofNullable(mockEvent.getLocationGeoLong()), Optional.of(67.891));
    }

    //Using https://www.vcalc.com/wiki/vcalc/haversine-distance to determine the expected value
    @Test
    public void testHaversine() throws InterruptedException {
        Thread.sleep(5000);
        Event mockEvent = getMockEvent();
        //expected distance between New York City and Los Angeles
        double distance1 = mockEvent.haversine(51.5074, -0.1278, 40.7128, -74.0060);
        assertEquals(5570230.0, distance1, 1000.0); //allowing for 1km difference
        //expected distance between Ho Chi Minh City and Calgary
        double distance2 = mockEvent.haversine(10.762622, 106.660172, 51.049999, -114.066666);
        assertEquals(12101590.0, distance2, 1000.0); //allowing for 1km difference
        //expected distance between CCIS, University of Alberta and Southgate Mall
        double distance3 = mockEvent.haversine(53.5282, 113.5257, 53.4855, 113.5141);
        assertEquals(4810.0, distance3, 100.0); //allowing for 100m difference
        double distance4 = mockEvent.haversine(0.0, 0.0, 0.0, 0.0);
        assertEquals(0, distance4, 0); //allowing for 0m difference
    }

    @Test
    public void testGetId() {
        Event event = getMockEvent();
        assertEquals(event.getId(), "12345");
        Event event1 = new Event("", currentUser, "Event for unit test", "", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(event1.getId(), "");
    }

    @Test
    public void testSetId() {
        Event event = getMockEvent();
        assertEquals(event.getId(), "12345");
        event.setId("abd");
        assertEquals(event.getId(), "abd");
        event.setId("");
        assertEquals(event.getId(), "");
    }

    @Test
    public void testGetName() {
        Event event = getMockEvent();
        assertEquals(event.getName(), "Event for unit test");
        Event event1 = new Event("", currentUser, "", "", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(event1.getName(), "");
    }

    @Test
    public void testSetName() {
        Event event = getMockEvent();
        assertEquals(event.getName(), "Event for unit test");
        event.setName("");
        assertEquals(event.getName(), "");
        event.setName("I don't care");
        assertEquals(event.getName(), "I don't care");
    }

    @Test
    public void testGetOwner() {
        Event event = getMockEvent();
        assertEquals(currentUser, event.getOwner());
        User user1 = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", false, false);
        Event event1 = new Event("", user1, "", "", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertNotEquals(event1.getOwner(), currentUser);
    }

    @Test
    public void testSetOwner() {
        Event event = getMockEvent();
        assertEquals(currentUser, event.getOwner());
        User user1 = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", false, false);
        event.setOwner(user1);
        assertEquals(user1, event.getOwner());
    }

    @Test
    public void testGetAttendees() {
        Event event = getMockEvent();
        UserList userList = new UserList();
        assertEquals(event.getAttendees().countUsers(), userList.countUsers());
        User user1 = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", false, false);
        userList.add(user1);
        Event event1 = new Event("12345", currentUser, "Event for unit test", "", "", new Date(), "", 0.00, 0.00, "", "", true, 100, userList);
        assertEquals(event1.getAttendees().countUsers(), userList.countUsers());
        userList.add(currentUser);
        Event event2 = new Event("12345", currentUser, "Event for unit test", "", "", new Date(), "", 0.00, 0.00, "", "", true, 100, userList);
        assertEquals(event2.getAttendees().countUsers(), userList.countUsers());
    }

    @Test
    public void testSetAttendees() {
        Event event = getMockEvent();
        UserList userList = new UserList();
        assertEquals(event.getAttendees().countUsers(), userList.countUsers());
        User user1 = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", false, false);
        userList.add(user1);
        event.setAttendees(userList);
        assertEquals(event.getAttendees().countUsers(), userList.countUsers());
        userList.add(currentUser);
        event.setAttendees(userList);
        assertEquals(event.getAttendees().countUsers(), userList.countUsers());
        User user2 = new User("345", "Tina2", "ngocthuy@gmail.com", "12344555", "123", false, false);
        userList.add(user2);
        assertEquals(event.getAttendees().countUsers(), userList.countUsers());

    }

    @Test
    public void testGetAttendeeAmount() {
        Event event = new Event("1234567890", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(Optional.of(0), Optional.ofNullable(event.getAttendeeAmount()));
    }

    @Test
    public void testSetAttendeeAmount() {
        Event event = new Event("helloWorld123456", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(Optional.of(0), Optional.ofNullable(event.getAttendeeAmount()));
        event.setAttendeeAmount(1);
        assertEquals(Optional.of(1), Optional.ofNullable(event.getAttendeeAmount()));
        event.setAttendeeAmount(10);
        assertEquals(Optional.of(10), Optional.ofNullable(event.getAttendeeAmount()));

    }

    @Test
    public void testGetHost() {
        Event event = getMockEvent();
        assertEquals(currentUser, event.getHost());
        User user1 = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", false, false);
        Event event1 = new Event("12345", user1, "Event for unit test", "", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(user1, event1.getHost());
        Event event2 = new Event("12345", null, "Event for unit test", "", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(null, event2.getHost());
    }

    @Test
    public void testSetHost() {
        Event event = getMockEvent();
        assertEquals(currentUser, event.getHost());
        User user1 = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", false, false);
        event.setHost(user1);
        assertEquals(user1, event.getHost());
        event.setHost(null);
        assertEquals(null, event.getHost());
    }

    @Test
    public void testGetDescription() {
        Event event = getMockEvent();
        assertEquals("hey, what is this event for other than testing?", event.getDescription());
        Event event1 = new Event("12345", currentUser, "Event for unit test", "", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals("", event1.getDescription());
        Event event2 = new Event("12345", currentUser, "Event for unit test", null, "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(null, event2.getDescription());
    }

    @Test
    public void testSetDecription() {
        Event event = getMockEvent();
        assertEquals("hey, what is this event for other than testing?", event.getDescription());
        event.setDescription("");
        assertEquals("", event.getDescription());
        event.setDescription("how are you?!?");
        assertEquals("how are you?!?", event.getDescription());
    }

    @Test
    public void testGetPosterRef() {
        Event event = getMockEvent();
        assertEquals("", event.getPosterRef());
        Event event1 = new Event("12345", currentUser, "Event for unit test", "", "users/kcMZVbm6wAYlaKAct5Os", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(event1.getPosterRef(), "users/kcMZVbm6wAYlaKAct5Os");
        Event event2 = new Event("12345", currentUser, "Event for unit test", "", "users/skdjf32rpwue ", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(event2.getPosterRef(), "users/skdjf32rpwue ");

    }

    @Test
    public void testSetPosterRef() {
        Event event = getMockEvent();
        assertEquals("", event.getPosterRef());
        event.setPosterRef("users/kcMZVbm6wAYlaKAct5Os");
        assertEquals("users/kcMZVbm6wAYlaKAct5Os", event.getPosterRef());
        event.setPosterRef("users/lagbhjnkmsdfghjkl ");
        assertEquals("users/lagbhjnkmsdfghjkl ", event.getPosterRef());
    }

    @Test
    public void testGetTime() {
        Date currentDate = new Date();
        Event event = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", currentDate, "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals(event.getTime(), currentDate);
        assertNotEquals(event.getTime(), new Date());
    }

    @Test
    public void testSetTime() {
        Event event = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
        assertNotEquals(event.getTime(), new Date());
        Date currentTime = new Date();
        event.setTime(currentTime);
        assertEquals(event.getTime(), currentTime);
    }

    @Test
    public void testGetLocation() {
        Event event = getMockEvent();
        assertEquals("", event.getLocation());
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "CCIS", 0.00, 0.00, "", "", true, 100, new UserList());
        assertEquals("CCIS", event1.getLocation());
    }

    @Test
    public void testSetLocation() {
        Event event = getMockEvent();
        assertEquals("", event.getLocation());
        event.setLocation("CCIS");
        assertEquals("CCIS", event.getLocation());
    }

    @Test
    public void testGetLocationGeoLat() {
        Event event = getMockEvent();
        assertEquals(Optional.ofNullable(event.getLocationGeoLat()), Optional.of(0.0));
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 17.873, 0.00, "", "", true, 100, new UserList());
        assertEquals(Optional.ofNullable(event1.getLocationGeoLat()), Optional.of(17.873));
        Event event2 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", -500.0, 0.00, "", "", true, 100, new UserList());
        assertEquals(Optional.ofNullable(event2.getLocationGeoLat()), Optional.of(-500.0));
    }

    @Test
    public void testSetLocationGeoLat() {
        Event event = getMockEvent();
        assertEquals(Optional.ofNullable(event.getLocationGeoLat()), Optional.of(0.0));
        event.setLocationGeoLat(17.873);
        assertEquals(Optional.ofNullable(event.getLocationGeoLat()), Optional.of(17.873));
        event.setLocationGeoLat(-500.0);
        assertEquals(Optional.ofNullable(event.getLocationGeoLat()), Optional.of(-500.0));
    }

    @Test
    public void testGetLocationGeoLong() {
        Event event = getMockEvent();
        assertEquals(Optional.ofNullable(event.getLocationGeoLong()), Optional.of(0.0));
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, "", "", true, 100, new UserList());
        assertEquals(Optional.ofNullable(event1.getLocationGeoLong()), Optional.of(17.873));
        Event event2 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, -500.0, "", "", true, 100, new UserList());
        assertEquals(Optional.ofNullable(event2.getLocationGeoLong()), Optional.of(-500.0));
    }

    @Test
    public void testSetLocationGeoLong() {
        Event event = getMockEvent();
        assertEquals(Optional.ofNullable(event.getLocationGeoLong()), Optional.of(0.0));
        event.setLocationGeoLong(17.873);
        assertEquals(Optional.ofNullable(event.getLocationGeoLong()), Optional.of(17.873));
        event.setLocationGeoLong(-500.0);
        assertEquals(Optional.ofNullable(event.getLocationGeoLong()), Optional.of(-500.0));
    }

    @Test
    public void testGetCheckinId() {
        Event event = getMockEvent();
        assertEquals("", event.getCheckinId());
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, "adsfhj s", "", true, 100, new UserList());
        assertEquals("adsfhj s", event1.getCheckinId());
        Event event2 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, null, "", true, 100, new UserList());
        assertNull(event2.getCheckinId());
    }

    @Test
    public void testSetCheckinID() {
        Event event = getMockEvent();
        assertEquals("", event.getCheckinId());
        event.setCheckinId("adsfhj s");
        assertEquals("adsfhj s", event.getCheckinId());
        event.setCheckinId("qsdfghj");
        assertEquals("qsdfghj", event.getCheckinId());
    }

    //TODO: check whether getCheckinQR or setCheckinQR is ever used, whether they are different from checkinID

    @Test
    public void testGetPromoteID() {
        Event event = getMockEvent();
        assertEquals("", event.getPromoteId());
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, "", "adsfhj s", true, 100, new UserList());
        assertEquals("adsfhj s", event1.getPromoteId());
        Event event2 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, "", null, true, 100, new UserList());
        assertNull(event2.getPromoteId());
    }

    @Test
    public void testSetPromoteID() {
        Event event = getMockEvent();
        assertEquals("", event.getPromoteId());
        event.setPromoteId("adsfhj s");
        assertEquals("adsfhj s", event.getPromoteId());
        event.setPromoteId("qsdfghj");
        assertEquals("qsdfghj", event.getPromoteId());
    }

    @Test
    public void testGetGeo() {
        Event event = getMockEvent();
        assertTrue(event.getGeo());
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, "", "adsfhj s", false, 100, new UserList());
        assertFalse(event1.getGeo());
    }

    @Test
    public void testSetGeo() {
        Event event = getMockEvent();
        assertTrue(event.getGeo());
        event.setGeo(Boolean.FALSE);
        assertFalse(event.getGeo());
    }

    @Test
    public void testGetLimit() {
        Event event = getMockEvent();
        assertEquals(Optional.ofNullable(event.getLimit()), Optional.of(100));
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, "adsfhj s", "", true, 0, new UserList());
        assertEquals(Optional.ofNullable(event1.getLimit()), Optional.of(0));
    }

    @Test
    public void testSetLimit() {
        Event event = getMockEvent();
        assertEquals(Optional.ofNullable(event.getLimit()), Optional.of(100));
        event.setLimit(0);
        assertEquals(Optional.ofNullable(event.getLimit()), Optional.of(0));
        event.setLimit(200);
        assertEquals(Optional.ofNullable(event.getLimit()), Optional.of(200));
    }

    @Test
    public void testGetQRCodeFromID() {
        Event event = getMockEvent();
        Bitmap qrCode = event.getQRCodeFromID(800, 800);
        assertNull(qrCode);
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, "nhH4GxzOVjXuKK6vDiK9", "", true, 0, new UserList());
        Bitmap qrCode1 = event1.getQRCodeFromID(800, 800);
        assertNotNull(qrCode1);
        assertTrue(qrCode1 instanceof Bitmap);
    }

    @Test
    public void testIsCurrentUserSignedUp() {
        Event event = getMockEvent();
        assertFalse(event.isCurrentUserSignedUp());
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, "nhH4GxzOVjXuKK6vDiK9", "", true, 0, new UserList());
        assertFalse(event1.isCurrentUserCheckedIn());
    }

    @Test
    public void testSetCurrentUserSignedUp() {
        Event event = getMockEvent();
        assertFalse(event.isCurrentUserSignedUp());
        event.setCurrentUserSignedUp(true);
        assertTrue(event.isCurrentUserSignedUp());
        event.setCurrentUserSignedUp(false);
        assertFalse(event.isCurrentUserSignedUp());
    }

    @Test
    public void testIsCurrentUserCheckedIn() {
        Event event = getMockEvent();
        assertFalse(event.isCurrentUserCheckedIn());
        Event event1 = new Event("12345", currentUser, "Event for unit test", "hey, what is this event for other than testing?", "", new Date(), "", 0.0, 17.873, "nhH4GxzOVjXuKK6vDiK9", "", true, 0, new UserList());
        assertFalse(event1.isCurrentUserCheckedIn());

    }

    @Test
    public void testSetCurrentUserCheckedIn() {
        Event event = getMockEvent();
        assertFalse(event.isCurrentUserCheckedIn());
        event.setCurrentUserCheckedIn(true);
        assertTrue(event.isCurrentUserCheckedIn());
        event.setCurrentUserCheckedIn(false);
        assertFalse(event.isCurrentUserCheckedIn());
    }
}