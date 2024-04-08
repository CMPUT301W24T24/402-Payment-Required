package com.example.qrcheckin.user.createevent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * This class represents a CreateEventViewModel
 */
public class CreateEventViewModel extends ViewModel {
    private final MutableLiveData<String> bannerRef;
    private final MutableLiveData<String> eventTitle;
    private final MutableLiveData<String> eventDate;
    private final MutableLiveData<String> eventTime;
    private final MutableLiveData<String> eventDescription;

    /**
     * Initializes the CreateEventViewModel class
     */
    public CreateEventViewModel() {
        bannerRef = new MutableLiveData<>();

        eventTitle = new MutableLiveData<>();
        eventTitle.setValue("Add your event Title");

        eventDate = new MutableLiveData<>();

        eventTime = new MutableLiveData<>();

        eventDescription = new MutableLiveData<>();
        eventDescription.setValue("Enter Event Description");

    }

    /**
     * An initialization that allows mock injection for test cases
     */
    public CreateEventViewModel(MutableLiveData<String> bannerMock, MutableLiveData<String> eventTitleMock, MutableLiveData<String> eventDateMock, MutableLiveData<String> eventTimeMock, MutableLiveData<String> eventDescriptionMock) {
        bannerRef = bannerMock;
        eventTitle = eventTitleMock;
        eventDate = eventDateMock;
        eventTime = eventTimeMock;
        eventDescription = eventDescriptionMock;
    }

    /**
     * Gets the banner reference
     * @return bannerRef
     */
    public MutableLiveData<String> getBannerRef() {
        return bannerRef;
    }

    /**
     * Gets the events title
     * @return - eventTitle
     */
    public MutableLiveData<String> getEventTitle() {
        return eventTitle;
    }

    /**
     * Gets the event date
     * @return - eventDate
     */
    public MutableLiveData<String> getEventDate() {
        return eventDate;
    }

    /**
     * Gets the event time
     * @return - eventTime
     */
    public MutableLiveData<String> getEventTime() {
        return eventTime;
    }

    /**
     * Gets the event description
     * @return - eventDescription
     */
    public MutableLiveData<String> getEventDescription() {
        return eventDescription;
    }

}
