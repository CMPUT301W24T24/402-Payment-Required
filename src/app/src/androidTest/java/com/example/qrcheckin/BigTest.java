package com.example.qrcheckin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressMenuKey;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.checkerframework.checker.units.qual.N;
import org.junit.Rule;
import org.junit.Test;

public class BigTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);


    // From CHATGPT
    // prompt: how do I create a unit test with Junit that interacts
    // with my navigation view in mainactivity from any fragment
    @Test
    public void testBigNavigation() throws InterruptedException, UiObjectNotFoundException {

        Thread.sleep(1000);

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
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notifications));

        // Check that the correct fragment is displayed
        onView(withId(R.id.notifications_list)).check(matches(isDisplayed()));

        Thread.sleep(1000);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_explore_event));
        Thread.sleep(1000);
        onView(withId(R.id.explore_event_list_view)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
        Thread.sleep(1000);
        onView(withId(R.id.profile_name)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.profile_name)).perform(ViewActions.clearText());
        onView(withId(R.id.profile_name)).perform(ViewActions.typeText("This is my name"));
        onView(withId(R.id.profile_email)).perform(ViewActions.clearText());
        onView(withId(R.id.profile_email)).perform(ViewActions.typeText("email@ua.ca"));

        Thread.sleep(1000);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_host_event));
        Thread.sleep(1000);
        onView(withId(R.id.event_add_fab)).check(matches(isDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
        Thread.sleep(1000);
        onView(withId(R.id.profile_name)).check(matches(isDisplayed()));
        onView(withText("This is my name")).check(doesNotExist());
        onView(withId(R.id.profile_name)).perform(ViewActions.clearText());
        onView(withId(R.id.profile_name)).perform(ViewActions.typeText("Name Change!"));
        onView(withId(R.id.profile_save_button)).perform(scrollTo());
        onView(withId(R.id.profile_save_button)).perform(click());

        Thread.sleep(1000);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_event));

        Thread.sleep(1000);

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        // Click on a navigation item
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));

        onView(withId(R.id.profile_name)).check(matches(withText("Name Change!")));
    }
}
