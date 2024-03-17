package com.example.qrcheckin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class NotificationTesting {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);


    // From CHATGPT
    // prompt: how do I create a unit test with Junit that interacts
    // with my navigation view in mainactivity from any fragment
    @Test
    public void testNavigationNotificationInteraction() throws InterruptedException {

        Thread.sleep(2500);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        // Click on a navigation item
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_notifications));

        Thread.sleep(2500);

        // Check that the correct fragment is displayed
        onView(withId(R.id.notifications_list))
                .check(matches(isDisplayed()));
    }
}
