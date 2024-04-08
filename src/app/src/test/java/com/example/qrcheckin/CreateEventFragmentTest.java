package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.EventList;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;
import com.example.qrcheckin.user.createevent.CreateEventFragment;

import org.checkerframework.checker.units.qual.C;
import org.junit.Test;

/**
 * Tests the methods of the CreateEventFragment class
 */
public class CreateEventFragmentTest {
    @Test
    /**
     * This test function ensures getAlphaNumericString is generating a string
     */
    public void testGenerateRandomNumber() {
        CreateEventFragment createEventFragment = new CreateEventFragment();
        String randomNum = createEventFragment.getAlphaNumericString(20);
        assertNotNull(randomNum);
    }
}

