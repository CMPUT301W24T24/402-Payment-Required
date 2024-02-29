package com.example.qrcheckin.core;

import java.util.Objects;

/**
 * An object that keeps track of the event data
 */
public class Event {

    private String id;
    private String name;
    private User owner;
    private UserList attendees;

    /**
     * The constructor for the event class
     * @param id the id of the event
     * @param name the name of the event
     * @param owner who is hosting the event
     * @param attendees a list of those attending the event
     */
    public Event(String id, String name, User owner, UserList attendees) {
        this.id = id;
        this.name = name;
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
    public String getName() {
        return name;
    }

    /**
     * A method which sets the name of the event
     * @param name the new name of the event
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * This method compares two city objects based on event id
     *
     * @param o the object to be compared.
     * @return an integer specifying the comparison between events
     */
    public int compareTo(Object o) {
        Event event = (Event) o;
        return this.id.compareTo(event.getId());
    }

    /**
     * This method is used to compare two event objects if they have the same id
     * @param o object need to be compared
     * @return 'true' if both cities have the id; otherwise 'false'
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(id, event.getId());
    }

    /**
     * This method generates a hash code for an event object with the same id producing the same hash code
     * @return an integer value to quickly identify the city object in the hash table
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


