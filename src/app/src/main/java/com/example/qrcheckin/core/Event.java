package com.example.qrcheckin.core;

/**
 * An object that keeps track of the event data
 */
public class Event {

    private String id;
    private String name;
    private User owner;
    private UserList attendees;

    private double latitude,longitude,distanceLimit; //geolocation data

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
        this.latitude = 0;
        this.longitude = 0;
        this.distanceLimit = Double.POSITIVE_INFINITY;
    }

    /**
     * Alternative constructor for event, allows for geolocation information to be additionally stored
     * @param id the id of the event
     * @param name the name of the event
     * @param owner who is hosting the event
     * @param attendees a list of those attending the event
     */
    public Event(String id, String name, User owner, UserList attendees,double latitude,double longitude, double distanceLimit) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.attendees = attendees;
        this.latitude=latitude;
        this.longitude=longitude;
        this.distanceLimit=distanceLimit;
    }

    /**
     * Used to check whether an event limits its attendees based on geolocation data
     * @return true if this event limits its attendees, false otherwise
     */
    public boolean usesGeolocation(){return Double.isInfinite(distanceLimit);}

    /**
     * Changes the geolocation data for the event,
     * @param latitude The latitude for the center of the circular event area
     * @param longitude The longitude for the center of the circular event area
     * @param distanceLimit The radius of the area in meters
     */
    public void setGeolocationData(double latitude, double longitude, double distanceLimit){
        this.latitude=latitude;
        this.longitude=longitude;
        this.distanceLimit=distanceLimit;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistanceLimit() {
        return distanceLimit;
    }

    public void setId(String id) {
        this.id = id;
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
}
