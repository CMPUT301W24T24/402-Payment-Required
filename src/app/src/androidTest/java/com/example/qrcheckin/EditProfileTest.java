package com.example.qrcheckin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.qrcheckin.user.home.HomeFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditProfileTest {

    @Test
    public void testActivitySwitch() {
        // Create a TestNavHostController
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        // Create a graphical FragmentScenario for the TitleScreen
        FragmentScenario<HomeFragment> titleScenario = FragmentScenario.launchInContainer(HomeFragment.class);

        titleScenario.onFragment(fragment -> {
            navController.setGraph(R.navigation.);
            Navigation.setViewNavController(fragment.requireView(), navController);
        });
        // Verify that performing a click changes the NavController’s state
        onView(ViewMatchers.withId(R.id.nav_profile)).perform(ViewActions.click());
    }
}
//        assertThat(navController.).isEqualTo(R.id.nav_profile);


