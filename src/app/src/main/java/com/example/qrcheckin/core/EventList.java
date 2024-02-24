package com.example.qrcheckin.core;

import com.example.qrcheckin.core.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A class which keeps track of a list of event objects (all the events on the app)
 */
public class EventList {
    private List<Event> events = new ArrayList<>();

    /**
     * Adds a new event to the list if it does not exist
     * @param event the new event
     */
    public void add(Event event) {
        if (events.contains(event)) {
            throw new IllegalArgumentException("This event already exists.");
        }
        events.add(event);
    }

    /**
     * Checks if an event exists or not
     * @param event the event that is being checked
     * @return
     * true if the event exists, false if it does not
     */
    public Boolean hasEvent(Event event) {
        return events.contains(event);
    }

    /**
     * Removes an event from the list if it exists
     * @param event the event being removed
     */
    public void removeEvent(Event event) {
        if (!hasEvent(event)) {
            throw new IllegalArgumentException("This event already does not exist.");
        }
        events.remove(event);
    }

    /**
     * Returns the amount of events
     * @return the size of the event list
     */
    public int countEvents() {
        return events.size();
    }

}
