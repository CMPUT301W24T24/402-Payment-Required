package com.example.qrcheckin;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

@RunWith(AndroidJUnit4.class)

@SmallTest
public class AttendeesSignedUpTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
    /*@Test
    public void testSignedUpAttendeeViewable() throws InterruptedException {
        // add an event with the organizer signed up to the event
        Database db = new Database();
        User currentUser = new User("jV5pjz2lR2j7jbC9EozX","Bennet","","","",true,true);
        ((QRCheckInApplication) getApplicationContext()).setCurrentUser(currentUser);
        Event newEvent = new Event(currentUser, "New Event", "Event description", "", new Date(), "", null, null, "", "", "", "", true, 100);
        db.addEvent(newEvent);
        db.signUpUser(currentUser, newEvent);

        // Test the user is viewable
        // onView(withId(R.id.button_add)).perform(click());
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.app_bar_main)).perform(click());

        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.nav_host_event)).perform(click()); // open the organizers list of hosted events

        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.hosted_event_list_view)).perform(click()); // click on the event

        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.view_event_sign_up)).perform(click()); // click on all sign ups to the event
        onView(withText("New Event")).check(matches(isDisplayed())); //
    }*/


}
