package com.example.qrcheckin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class AttendeesSignedUpTesting {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void testSignedUpAttendeeViewable () throws Exception {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open)
    }
}
