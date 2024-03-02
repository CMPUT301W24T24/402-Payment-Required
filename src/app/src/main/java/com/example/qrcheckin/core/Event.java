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
    private double latitude,longitude,distanceLimit; //geolocation data
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
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     * @param checkinId the id of the checkin
     * @param promoteId the id of the promotion
     * @param geo the boolean value of the location
     * @param limit the limit of the event
     * @param attendees the list of users attending the event
     */
    public Event(String id, User host, String name, String description, String posterRef, Date time, String location, Double latitude, Double longitude, Double distanceLimit, String checkinId, String promoteId, Boolean geo, Integer limit, UserList attendees) {
        this.id = id;
        this.host = host;
        this.name = name;
        this.description = description;
        this.posterRef = posterRef;
        this.time = time;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceLimit = distanceLimit
        this.checkinId = checkinId;
        this.promoteId = promoteId;
        this.geo = geo;
        this.limit = limit;
        this.attendees = attendees;
    }

    public Event(String id, User host, String name, String description, String posterRef, Date time, String location, Double latitude, Double longitude, Double distanceLimit, String checkinId, String checkinRq, String promoteId, String promoteRq, Boolean geo, Integer limit, UserList attendees) {
        this.id = id;
        this.host = host;
        this.name = name;
        this.description = description;
        this.posterRef = posterRef;
        this.time = time;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceLimit = distanceLimit
        this.checkinId = checkinId;
        this.checkinRq = checkinRq;
        this.promoteId = promoteId;
        this.promoteRq = promoteRq;
        this.geo = geo;
        this.limit = limit;
        this.attendees = attendees;
    }


    /**
     * CheckIn attempts to check in a user, and if required checks their location against the events
     * @param user User who wants to check in to event
     * @param latitude Latitude of user who wants to check into event
     * @param longitude Longitude of user who wants to check into event
     * @return True if user successfully checked in, false otherwise
     */
    public boolean checkIn(User user, Double latitude, Double longitude){
        if(!usesGeolocation()) {
            (new Database()).checkIn(user,this);
            if(!attendees.hasUser(user))
                attendees.add(user);
            return true;//success
        }else if(haversine(latitude,longitude,this.latitude,this.longitude) < distanceLimit){
            (new Database()).checkInWithGeo(user,this,latitude,longitude);
            if(!attendees.hasUser(user))
                attendees.add(user);
            return true;//success
        }
        return false;//failed to check in
    }

    /**
     * Alternative to checkIn which does not apply geo data, but will always fail if geo data is required
     * @param user User who wants to check in to event
     * @return True if user successfully checked in (only if geo is disabled), false otherwise
     */
    public boolean checkIn(User user){
        if(usesGeolocation())
            return false;//failed to check in as no location provided

        (new Database()).checkIn(user,this);
        if(!attendees.hasUser(user))
            attendees.add(user);
        return true;//success
    }

    /** Simple haversine implementation to covert latitude longitude pairs to a distance in meters, see https://www.movable-type.co.uk/scripts/latlong.html
     * @return approximation of the distance in meters between the two latitude longitude pairs
     */
    public double haversine(Double lat1,Double lon1, Double lat2,Double lon2){
        final int R = 6371000;//radius of earth in meters
        Double latDistSin = Math.sin(Math.toRadians(lat2-lat1)/2);
        Double lonDistSin = Math.sin(Math.toRadians(lon2-lon1)/2);
        Double a = latDistSin*latDistSin + lonDistSin*lonDistSin*Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180);
        Double c=2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distance = R*c;
        return distance;
    }

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

    /**
     * Used to check whether an event limits its attendees based on geolocation data
     * @return true if this event limits its attendees, false otherwise
     */
    public boolean usesGeolocation(){
        return geo;
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
}
