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
     * @param latitude double latitude of the center of the event location
     * @param longitude double longitude of the center of the event location
     * @param distanceLimit double maximum allowed distance in meters from the events center
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

    public void checkIn(User user, Double latitude, Double longitude){

    }

    /** Simple haversine implementation to covert latitude longitude pairs to a distance in meters, see https://www.movable-type.co.uk/scripts/latlong.html
     * @return approximation of the distance in meters between the two latitude longitude pairs
     */
    public double haversine(Double lat1,Double lon1, Double lat2,Double lon2){
        final int R = 6371000;//radius of earth in meters
        Double latDistSin = Math.sin((lat2-lat1)*Math.PI/360);
        Double lonDistSin = Math.sin((lon2-lon1)*Math.PI/360);
        Double a = latDistSin*latDistSin + Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180) * lonDistSin*lonDistSin;
        Double distance = R*2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return distance;
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
