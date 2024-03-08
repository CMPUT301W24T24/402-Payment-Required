package com.example.qrcheckin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.ActionBar;
import android.content.Context;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toolbar;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static java.util.regex.Pattern.matches;

import com.example.qrcheckin.databinding.ActivityMainBinding;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.DrawerLayoutUtils;
import com.google.android.material.navigation.NavigationView;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.qrcheckin", appContext.getPackageName());
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);




//    @Test
//    public void clickOnYourNavigationItem_ShowsYourScreen() {
//        // Open Drawer to click on navigation.
//        onView(withId(R.id.drawer_layout))
//                //.check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
//                .perform(DrawerActions.open()); // Open Drawer
//
//        // Start the screen of your activity.
//        onView(withId(R.id.nav_view))
//                .perform(.navigateTo(R.id.your_navigation_menu_item));
//
//        // Check that you Activity was opened.
//        String expectedNoStatisticsText = InstrumentationRegistry.getTargetContext()
//                .getString(R.string.no_item_available);
//        onView(withId(R.id.no_statistics)).check(matches(withText(expectedNoStatisticsText)));
//    }



//    @Test
//    public void testNavigationViewInteraction() throws InterruptedException {
//        // Open the navigation drawer
//
//
//
//        // onView(withId(R.id.nav_host_fragment_content_main)).perform(click());
//
//        onData(is(instanceOf(ActionBar.class))).perform(click());
//
//        // onData(is(instanceOf(.class))).perform(click());
//
//        wait(10000);
//
//        // Click on a navigation item
//        //onView(withId(R.id.nav_view)).perform(NavigationView.navigateTo(R.id.nav_view));
//
//        // Check that the correct fragment is displayed
//        onView(withId(R.id.nav_view))
//                .check(matches(isDisplayed()));
//    }

}