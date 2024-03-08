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

public class CreateEventFragmentTest {
    @Test
    /**
     * This test function ensures gererateQRCode is generating a string
     */
    public void testGenerateQRCode() {
        CreateEventFragment createEventFragment = new CreateEventFragment();
        String qrCode = createEventFragment.generateQRCode();
        assertNotNull(qrCode);
    }

    @Test
    /**
     * This function tests that the getAlphaNumericString function is working
     */
    public void testRandomNumber() {
        CreateEventFragment createEventFragment = new CreateEventFragment();
        String randomNum = createEventFragment.getAlphaNumericString(20);

        // make sure its not null
        assertNotNull(randomNum);
        // make sure its the right length
        assertEquals(randomNum.length(), 20);
    }

}

