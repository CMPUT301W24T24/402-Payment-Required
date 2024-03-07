package com.example.qrcheckin.user.createevent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateEventViewModel extends ViewModel {
    private final MutableLiveData<String> bannerRef;
    private final MutableLiveData<String> eventTitle;
    private final MutableLiveData<String> eventDate;
    private final MutableLiveData<String> eventTime;
    private final MutableLiveData<String> eventDescription;

    public CreateEventViewModel() {
        bannerRef = new MutableLiveData<>();

        eventTitle = new MutableLiveData<>();
        eventTitle.setValue("Add your event Title");

        eventDate = new MutableLiveData<>();

        eventTime = new MutableLiveData<>();

        eventDescription = new MutableLiveData<>();
        eventDescription.setValue("Enter Event Description");

    }

    public MutableLiveData<String> getBannerRef() {
        return bannerRef;
    }

    public MutableLiveData<String> getEventTitle() {
        return eventTitle;
    }

    public MutableLiveData<String> getEventDate() {
        return eventDate;
    }

    public MutableLiveData<String> getEventTime() {
        return eventTime;
    }

    public MutableLiveData<String> getEventDescription() {
        return eventDescription;
    }
}
