package com.example.qrcheckin.core;

/**
 * An object that keeps track of the event data
 */
public class Event {

    private String id;
    private String eventName;
    private User owner;
    private UserList attendees;

    /**
     * The constructor for the event class
     * @param id the id of the event
     * @param eventName the name of the event
     * @param owner who is hosting the event
     * @param attendees a list of those attending the event
     */
    public Event(String id, String eventName, User owner, UserList attendees) {
        this.id = id;
        this.eventName = eventName;
        this.owner = owner;
        this.attendees = attendees;
    }

    /**
     * The method returns the id of the event
     * @return The id of the event
     */
    public String getId() {
        return id;
    }

    /**
     * A method which returns the name of the event
     * @return
     * The name of the event
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * A method which sets the name of the event
     * @param eventName the new name of the event
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * A method which returns the owner of the event
     * @return
     * User who is hosting the event
     */
    public User getOwner() {
        return owner;
    }

    /**
     * A method which sets the host of the event
     * @param owner the User hosting the event
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * A method which returns the list of the users attending the event
     * @return
     * List of users attending the event
     */
    public UserList getAttendees() {
        return attendees;
    }

    /**
     * A method which sets the list of users attending the event (idk why we would use this)
     * @param attendees the list of users attending the event
     */
    public void setAttendees(UserList attendees) {
        this.attendees = attendees;
    }
}
