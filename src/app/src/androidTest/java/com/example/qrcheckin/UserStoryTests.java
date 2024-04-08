package com.example.qrcheckin;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressMenuKey;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.CursorMatchers.withRowString;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import android.app.NotificationManager;
import android.content.Context;
import android.view.View;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.AdapterViewProtocol;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.N;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.AllOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

public class UserStoryTests {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

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
        return new Event("Tina's hosted exciting event", currentUser, "Event for delete Event", "hey, what is this event for other than testing?", "", new Date(), "", 0.00, 0.00, "", "", true, 100, new UserList());
    }
    @Test
    public void createEvent() throws InterruptedException, UiObjectNotFoundException {
        Thread.sleep(1000);


        // Reference: ChatGPT
        // prompt: how do I get Junit in android studio to click on the "Allow" button when a notification permissions reuest is displayed

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowPermissions = device.findObject(new UiSelector().text("While using the app"));
        if (allowPermissions.exists()) {
            allowPermissions.click();
        }

        UiObject allowNPermissions = device.findObject(new UiSelector().text("Allow"));
        if (allowNPermissions.exists()) {
            allowNPermissions.click();
        }


        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_host_event));

        Thread.sleep(1000);

        onView(withId(R.id.event_add_fab)).perform(click());

        Thread.sleep(1000);

        UiObject allowPPermissions = device.findObject(new UiSelector().text("Change to precise location"));
        if (allowPPermissions.exists()) {
            allowPPermissions.click();
        }

        Thread.sleep(1000);

        onView(withId(R.id.text_create_event_title)).perform(ViewActions.typeText("createEventTestEvent"));
        onView(withId(R.id.text_create_event_description)).perform(ViewActions.typeText("This is an event that was created by createEvent() test"));

        onView(withId(R.id.create_event_attend_limit))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.create_event_attend_limit)).perform(ViewActions.typeText("4000"));

        onView(withId(R.id.button_create_event_generate_qr_checkin))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.button_create_event_generate_qr_checkin)).perform(click());

        onView(withId(R.id.imageview_create_event_checkin_qr))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.button_create_event_generate_qr_description))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.button_create_event_generate_qr_description)).perform(click());

        onView(withId(R.id.imageview_create_event_description_qr))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.button_create_event_submit))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.button_create_event_submit)).perform(click());

        Thread.sleep(1000);

        onView(withText("createEventTestEvent")).perform(click());
        onView(withText("createEventTestEvent")).check(matches(isDisplayed()));
        onView(withText("This is an event that was created by createEvent() test")).check(matches(isDisplayed()));
        onView(withText("4000")).check(matches(isDisplayed()));

        onView(withId(R.id.edit_event_check_in_code))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void checkAttendees() throws UiObjectNotFoundException, InterruptedException {

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowPermissions = device.findObject(new UiSelector().text("While using the app"));
        if (allowPermissions.exists()) {
            allowPermissions.click();
        }

        UiObject allowNPermissions = device.findObject(new UiSelector().text("Allow"));
        if (allowNPermissions.exists()) {
            allowNPermissions.click();
        }

        String eventName = "" + System.currentTimeMillis();

        eventName = eventName.substring(eventName.length() - 7, eventName.length());

        eventName = eventName + "chAtt";

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_host_event));

        Thread.sleep(2000);

        onView(withId(R.id.event_add_fab)).perform(click());

        Thread.sleep(1000);

        UiObject allowPPermissions = device.findObject(new UiSelector().text("Change to precise location"));
        if (allowPPermissions.exists()) {
            allowPPermissions.click();
        }

        Thread.sleep(1000);

        onView(withId(R.id.text_create_event_title)).perform(ViewActions.typeText(eventName));
        onView(withId(R.id.button_create_event_submit))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.button_create_event_submit)).perform(click());

        Thread.sleep(2500);

        onView(withText(eventName)).perform(click());
        onView(withId(R.id.edit_event_show_sign_ups)).perform(click());

        onView(withText("No one has signed up for this event")).check(matches(isDisplayed()));
    }

    @Test
    public void EditProfile() throws UiObjectNotFoundException, InterruptedException {

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowPermissions = device.findObject(new UiSelector().text("While using the app"));
        if (allowPermissions.exists()) {
            allowPermissions.click();
        }

        UiObject allowNPermissions = device.findObject(new UiSelector().text("Allow"));
        if (allowNPermissions.exists()) {
            allowNPermissions.click();
        }

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));

        Thread.sleep(1000);

        onView(withId(R.id.profile_name)).check(matches(isDisplayed()));

        onView(withId(R.id.profile_name)).perform(ViewActions.clearText());
        onView(withId(R.id.profile_name)).perform(ViewActions.typeText("Test User Please Delete"));
        onView(withId(R.id.profile_email)).perform(ViewActions.typeText("test@email.com"));
        onView(withId(R.id.profile_phone_number)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.profile_phone_number)).perform(ViewActions.typeText("1234567890"));
        onView(withId(R.id.profile_geolocation)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.profile_geolocation)).perform(click());
        onView(withId(R.id.profile_homepage_link)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.profile_homepage_link)).perform(ViewActions.typeText("my homepage link"));

        onView(withId(R.id.profile_save_button)).perform(ViewActions.scrollTo()).check(matches(isDisplayed()));
        onView(withId(R.id.profile_save_button)).perform(click());

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_home));

        Thread.sleep(1000);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));

        onView(withText("Anonymous User")).check(doesNotExist());
        onView(withId(R.id.profile_name)).check(matches(withText("Test User Please Delete")));
        onView(withText("test@email.com")).check(matches(isDisplayed()));
        onView(withText("1234567890")).check(matches(isDisplayed()));
        onView(withId(R.id.profile_geolocation)).check(matches(isChecked()));
        onView(withId(R.id.profile_homepage_link)).check(matches(withText("my homepage link")));
    }

    @Test
    public void sendNotification() throws UiObjectNotFoundException, InterruptedException {

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowPermissions = device.findObject(new UiSelector().text("While using the app"));
        if (allowPermissions.exists()) {
            allowPermissions.click();
        }

        UiObject allowNPermissions = device.findObject(new UiSelector().text("Allow"));
        if (allowNPermissions.exists()) {
            allowNPermissions.click();
        }
        String eventName = "" + System.currentTimeMillis();

        eventName = eventName.substring(eventName.length() - 7, eventName.length());

        eventName = eventName + "chAtt";

        Thread.sleep(1000);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_host_event));

        Thread.sleep(2000);

        onView(withId(R.id.event_add_fab)).perform(click());

        Thread.sleep(1000);

        UiObject allowPPermissions = device.findObject(new UiSelector().text("Change to precise location"));
        if (allowPPermissions.exists()) {
            allowPPermissions.click();
        }

        Thread.sleep(1000);

        onView(withId(R.id.text_create_event_title)).perform(ViewActions.typeText(eventName));
        onView(withId(R.id.button_create_event_submit))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.button_create_event_submit)).perform(click());

        Thread.sleep(2000);

        onView(withText(eventName)).perform(click());

        onView(withId(R.id.edit_event_notify_attendees)).perform(click());
        onView(withId(R.id.create_notification_message)).perform(ViewActions.typeText("This is my Notification"));
        onView(withId(R.id.create_notification_button)).perform(click());

        onView(withId(R.id.edit_event_notify_attendees)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_explore_event));

        Thread.sleep(1000);

        onView(withId(R.id.explore_event_search_bar)).perform(ViewActions.typeText(eventName));
        onView(withId(R.id.explore_event_search_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.explore_event_search_bar)).perform(ViewActions.clearText());
        onView(withText(eventName))
                .perform(click());

        onView(withText("Sign Me Up")).perform(click());

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notifications));

        onView(withText("This is my Notification")).check(matches(isDisplayed()));
    }

    @Test
    public void pushNotification() throws UiObjectNotFoundException, InterruptedException {

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowPermissions = device.findObject(new UiSelector().text("While using the app"));
        if (allowPermissions.exists()) {
            allowPermissions.click();
        }

        UiObject allowNPermissions = device.findObject(new UiSelector().text("Allow"));
        if (allowNPermissions.exists()) {
            allowNPermissions.click();
        }
        String eventName = "" + System.currentTimeMillis();

        eventName = eventName.substring(eventName.length() - 7, eventName.length());

        eventName = eventName + "chAtt";

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_host_event));

        Thread.sleep(2000);

        onView(withId(R.id.event_add_fab)).perform(click());

        Thread.sleep(1000);

        UiObject allowPPermissions = device.findObject(new UiSelector().text("Change to precise location"));
        if (allowPPermissions.exists()) {
            allowPPermissions.click();
        }

        Thread.sleep(1000);

        onView(withId(R.id.text_create_event_title)).perform(ViewActions.typeText(eventName));
        onView(withId(R.id.button_create_event_submit))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.button_create_event_submit)).perform(click());

        Thread.sleep(2000);

        onView(withText(eventName)).perform(click());

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_explore_event));

        Thread.sleep(1000);

        onView(withId(R.id.explore_event_search_bar)).perform(ViewActions.typeText(eventName));
        onView(withId(R.id.explore_event_search_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.explore_event_search_bar)).perform(ViewActions.clearText());
        onView(withText(eventName))
                .perform(click());

        onView(withText("Sign Me Up")).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_host_event));
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());

        Thread.sleep(1000);

        onView(withId(R.id.edit_event_notify_attendees)).perform(click());
        onView(withId(R.id.create_notification_message)).perform(ViewActions.typeText("This is my Notification"));
        onView(withId(R.id.create_notification_button)).perform(click());

        onView(withId(R.id.edit_event_notify_attendees)).check(matches(isDisplayed()));

        NotificationManager notificationManager = (NotificationManager) getTargetContext().getSystemService(Context.NOTIFICATION_SERVICE);

        assert (notificationManager.getActiveNotifications().length == 1);
//        Thread.sleep(5000);
    }

    @Test
    public void adminShowEvent() throws UiObjectNotFoundException, InterruptedException{
        activityRule.getScenario().onActivity(Database::displayAdminDrawer);
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowPermissions = device.findObject(new UiSelector().text("While using the app"));
        if (allowPermissions.exists()) {
            allowPermissions.click();
        }

        UiObject allowNPermissions = device.findObject(new UiSelector().text("Allow"));
        if (allowNPermissions.exists()) {
            allowNPermissions.click();
        }
        String time = String.valueOf(System.currentTimeMillis());
        String eventName = "Tina's TestEvent For adminShowEvent" + time;
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_host_event));
        Thread.sleep(2000);
        onView(withId(R.id.event_add_fab)).perform(click());
        Thread.sleep(1000);
        UiObject allowPPermissions = device.findObject(new UiSelector().text("Change to precise location"));
        if (allowPPermissions.exists()) {
            allowPPermissions.click();
        }
        Thread.sleep(1000);

        onView(withId(R.id.text_create_event_title)).perform(ViewActions.typeText(eventName));
        Thread.sleep(1000);
        onView(withId(R.id.button_create_event_submit))
                .perform(ViewActions.scrollTo())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.button_create_event_submit)).perform(click());
        Thread.sleep(2500);
        //Goto the admin all profiles and check
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_all_event));
        Thread.sleep(2500);
        onView(withId(R.id.all_event_search_bar)).perform(ViewActions.typeText(eventName));
        Thread.sleep(2000);
        onView(withId(R.id.all_event_search_button)).perform(click());
        Thread.sleep(3000);
        onData(withId(R.id.all_event_listview))
                .inAdapterView(CoreMatchers.is(withText(eventName)))
                .atPosition(0)
                .perform(click());
        Thread.sleep(2000);


    }

    @Test
    public void adminDeleteEvent() throws UiObjectNotFoundException, InterruptedException{
        activityRule.getScenario().onActivity(Database::displayAdminDrawer);
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowPermissions = device.findObject(new UiSelector().text("While using the app"));
        if (allowPermissions.exists()) {
            allowPermissions.click();
        }
        Thread.sleep(2000);

        UiObject allowNPermissions = device.findObject(new UiSelector().text("Allow"));
        if (allowNPermissions.exists()) {
            allowNPermissions.click();
        }
        Thread.sleep(2000);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        Thread.sleep(2000);
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_all_event));
        Thread.sleep(2000);
    }
}