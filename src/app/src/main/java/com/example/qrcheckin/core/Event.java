package com.example.qrcheckin.core;

import java.util.Date;
import java.util.Objects;

/**
 * An object that keeps track of the event data
 */
public class Event {

    private String id;
    private User host;
    private String name;
    private String description;
    private String posterRef;
    private Date time;
    private String location;
    private Double locationGeoLat;
    private Double locationGeoLong;
    private String checkinId;
    private String checkinRq;
    private String promoteId;
    private String promoteRq;
    private Boolean geo;
    private Integer limit;
    private UserList attendees;

    /**
     * A constructor for the event object
     * @param id the id of the event
     * @param host the user hosting the event
     * @param name the name of the event
     * @param description the description of the event
     * @param posterRef the reference to the poster of the event
     * @param time the time of the event
     * @param location the location of the event
     * @param locationGeoLat the latitude of the location
     * @param locationGeoLong the longitude of the location
     * @param checkinId the id of the checkin
     * @param promoteId the id of the promotion
     * @param geo the boolean value of the location
     * @param limit the limit of the event
     * @param attendees the list of users attending the event
     */
    public Event(String id, User host, String name, String description, String posterRef, Date time, String location, Double locationGeoLat, Double locationGeoLong, String checkinId, String promoteId, Boolean geo, Integer limit, UserList attendees) {
        this.id = id;
        this.host = host;
        this.name = name;
        this.description = description;
        this.posterRef = posterRef;
        this.time = time;
        this.location = location;
        this.locationGeoLat = locationGeoLat;
        this.locationGeoLong = locationGeoLong;
        this.checkinId = checkinId;
        this.promoteId = promoteId;
        this.geo = geo;
        this.limit = limit;
        this.attendees = attendees;
    }

    public Event(String id, User host, String name, String description, String posterRef, Date time, String location, Double locationGeoLat, Double locationGeoLong, String checkinId, String checkinRq, String promoteId, String promoteRq, Boolean geo, Integer limit, UserList attendees) {
        this.id = id;
        this.host = host;
        this.name = name;
        this.description = description;
        this.posterRef = posterRef;
        this.time = time;
        this.location = location;
        this.locationGeoLat = locationGeoLat;
        this.locationGeoLong = locationGeoLong;
        this.checkinId = checkinId;
        this.checkinRq = checkinRq;
        this.promoteId = promoteId;
        this.promoteRq = promoteRq;
        this.geo = geo;
        this.limit = limit;
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
        return host;
    }

    /**
     * A method which sets the host of the event
     * @param owner the User hosting the event
     */
    public void setOwner(User owner) {
        this.host = owner;
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


