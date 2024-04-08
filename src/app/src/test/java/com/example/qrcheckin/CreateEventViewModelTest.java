package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.lifecycle.MutableLiveData;

import com.example.qrcheckin.user.createevent.CreateEventFragment;
import com.example.qrcheckin.user.createevent.CreateEventViewModel;

import org.junit.Test;

/**
 * Tests the methods of the CreateEventViewModel class
 */
public class CreateEventViewModelTest {
    CreateEventViewModel createEventViewModel;
    private void setUp () {
        MutableLiveData<String> bannerRef = new MutableLiveData<>("Banner Ref");
        MutableLiveData<String> eventTitle = new MutableLiveData<>("Event Title");
        MutableLiveData<String> eventDate = new MutableLiveData<>("Event Date");
        MutableLiveData<String> eventTime = new MutableLiveData<>("Event Time");
        MutableLiveData<String> eventDescription = new MutableLiveData<>("Event Description");
        createEventViewModel = new CreateEventViewModel(bannerRef,eventTitle,eventDate,eventTime,eventDescription);

    }

    @Test
    /**
     * This test function ensures getBannerRef is returning the correct string
     */
    public void testGetBannerRef() {
        setUp();
        String bannerRef = createEventViewModel.getBannerRef().getValue();
        assertEquals(bannerRef,"Banner Ref");
    }

    @Test
    /**
     * This test function ensures getEventTitle is returning the correct string
     */
    public void testGetEventTitle() {
        setUp();
        String eventTitle = createEventViewModel.getEventTitle().getValue();
        assertEquals(eventTitle,"Event Title");
    }

    @Test
    /**
     * This test function ensures getEventDate is returning the correct string
     */
    public void testGetEventDate() {
        setUp();
        String eventDate = createEventViewModel.getEventDate().getValue();
        assertEquals(eventDate,"Event Date");
    }

    @Test
    /**
     * This test function ensures getEventTime is returning the correct string
     */
    public void testGetEventTime() {
        setUp();
        String eventTime = createEventViewModel.getEventTime().getValue();
        assertEquals(eventTime,"Event Time");
    }

    @Test
    /**
     * This test function ensures getEventDescription is returning the correct string
     */
    public void testGetEventDescription() {
        setUp();
        String eventDescription = createEventViewModel.getEventDescription().getValue();
        assertEquals(eventDescription,"Event Description");
    }
}

