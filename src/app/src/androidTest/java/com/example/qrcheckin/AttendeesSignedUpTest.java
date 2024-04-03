package com.example.qrcheckin;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.google.android.material.navigation.NavigationView;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Ordering;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

@RunWith(AndroidJUnit4.class)

/**
 * Tests the user interface of the Attendee Signed Up page
 */
@SmallTest
public class AttendeesSignedUpTest {
    QRCheckInApplication app;
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Initializes the application context
     * References: Gemini
     * Prompt: The QRCheckInApplication app = (QRCheckInApplication) scenario.getActivity().getApplication(); line of
     * code is giving me trouble. It highlights the .getActivity() red and gives this errror message:
     * "No candidates found for method call scenario.getActivity()"
     */

    @Before
    public void getApplication() {
        // Get the application
        Log.d("QRCheckIn", "getApplication");
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context appContext = instrumentation.getTargetContext();
        app = (QRCheckInApplication) appContext.getApplicationContext();
        if (app == null) {
            app = (QRCheckInApplication) getApplicationContext();
        }
        if (app == null) {
            Log.d("QRCheckIn", "The app is null");
        }
    }

    /**
     * Tests if one event is viewable
     * @throws InterruptedException The error thrown
     * References: https://stackoverflow.com/questions/35675296/how-to-catch-a-view-with-tag-by-espresso-in-android petey Viewed: 2024-03-30
     */

    @Test
    public void testSignedUpAttendeeViewable() throws InterruptedException {
        // add an event with the organizer signed up to the event
        Database db = new Database();
        Thread.sleep(2500);
        User currentUser = app.getCurrentUser();
        Event newEvent = new Event(currentUser, "Attendee Test Event", "Event description", "", new Date(), "", null, null, "", "", "", "", true, 100);
        db.addEvent(newEvent);
        db.signUpUser(currentUser, newEvent);
        Thread.sleep(2500);

        // Test the user is viewable
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        Thread.sleep(1000);

        // Click on hosted events
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_host_event));
        /*
        Thread.sleep(1000);

        // Click on hosted event view
        onView(withId(R.id.hosted_event_list_view)).perform(click((ViewAction) withTagValue(is((Object)newEvent.getId()))));

        Thread.sleep(1000);
        // Click on events signed up
        onView(withId(R.id.edit_event_show_sign_ups)).perform(click());
        */
    }


    // End here
    /*
    /**
     * This method is called when user is fetched from the database, interface method
     * It sets the user to the application and the user name and picture to the navigation view
     * @param user the user
     */
    /*@Override
    public void onUserFetched(User user) {
        currentUser = user;
        Log.d("Firestore", "User fetched: " + user.getDataString());

        // set the user to the application
        ((QRCheckInApplication) mainActivity.getApplication()).setCurrentUser(currentUser);

        // set the user name to the navigation view
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView navProfileName = headerView.findViewById(R.id.nav_profile_name);
        navProfileName.setText(currentUser.getName());

        ImageView navProfileImage = headerView.findViewById(R.id.nav_profile_pic);
        // Set the profile picture of the user to the XML view
        Database db = new Database();
        db.getUserPicture(currentUser, navProfileImage);

        // set the notification listeners for event user is signed up for
        mainActivity.createNotificationListeners();
        if (currentUser.isAdmin()) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_all_event).setVisible(true);
            menu.findItem(R.id.nav_all_images).setVisible(true);
            menu.findItem(R.id.nav_all_profile).setVisible(true);
        }
    }

    /**
     * @param id
     */
    /*@Override
    public void onUserAdded(String id) {
        currentUser.setId(id);
        writeIdToFile(id);
        onUserFetched(currentUser);
    }

    /**
     * Write the id to the file
     * <a href="https://stackoverflow.com/questions/11386441/filewriter-not-writing-to-file-in-android">Reference: Writing, Author: FoamyGuy, Accessed: 2024-02-27 </a>
     * <a href="https://developer.android.com/reference/java/io/FileWriter">FileWriter</a>
     * @param id the id of the user
     */
    /*private void writeIdToFile(String id) {
        File file = new File(getFilesDir(), "userDevice.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(id);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.e("login", "Can not write file: " + e.toString());
        }
    }*/
}
