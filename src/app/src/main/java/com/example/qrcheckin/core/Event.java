package com.example.qrcheckin.core;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

/**
 * An object that keeps track of the event data
 */
public class Event implements Serializable {

    private String id;
    private User host;
    private String name;
    private String description;
    private String posterRef;
    private Date time;
    private String location;
    private Double locationGeoLat,locationGeoLong; //geolocation data
    private String checkinId;
    private String checkinQR;
    private String promoteId;
    private String promoteQR;
    private Boolean geo;
    private Integer limit;
    private UserList attendees;
    private Boolean currentUserSignedUp;
    private Boolean currentUserCheckedIn;

   

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
    public Event(String id, User host, String name, String description, String posterRef, Date time, String location, Double latitude, Double longitude, String checkinId, String promoteId, Boolean geo, Integer limit, UserList attendees) {
        this.id = id;
        this.host = host;
        this.name = name;
        this.description = description;
        this.posterRef = posterRef;
        this.time = time;
        this.location = location;
        this.locationGeoLat = latitude;
        this.locationGeoLong = longitude;
        this.checkinId = checkinId;
        this.promoteId = promoteId;
        this.geo = geo;
        this.limit = limit;
        this.attendees = attendees;
        this.currentUserSignedUp = false;
        this.currentUserCheckedIn = false;
    }

    /**
     * A constructor for the event object
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
     */
    public Event(String id, User host, String name, String description, String posterRef, Date time, String location, Double latitude, Double longitude, String checkinId, String checkinQR, String promoteId, String promoteQR, Boolean geo, Integer limit, UserList attendees) {
        this.id = id;
        this.host = host;
        this.name = name;
        this.description = description;
        this.posterRef = posterRef;
        this.time = time;
        this.location = location;
        this.locationGeoLat = latitude;
        this.locationGeoLong = longitude;
        this.checkinId = checkinId;
        this.checkinQR = checkinQR;
        this.promoteId = promoteId;
        this.promoteQR = promoteQR;
        this.geo = geo;
        this.limit = limit;
        this.attendees = attendees;
        this.currentUserSignedUp = false;
        this.currentUserCheckedIn = false;
    }

    /**
     * A constructor for the event object
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
     */
    public Event(User host, String name, String description, String posterRef, Date time, String location, Double locationGeoLat, Double locationGeoLong, String checkinId, String checkinQR, String promoteId, String promoteQR, Boolean geo, Integer limit) {
        this.host = host;
        this.name = name;
        this.description = description;
        this.posterRef = posterRef;
        this.time = time;
        this.location = location;
        this.locationGeoLat = locationGeoLat;
        this.locationGeoLong = locationGeoLong;
        this.checkinId = checkinId;
        this.checkinQR = checkinQR;
        this.promoteId = promoteId;
        this.promoteQR = promoteQR;
        this.geo = geo;
        this.limit = limit;
        this.currentUserSignedUp = false;
        this.currentUserCheckedIn = false;
    }

        /**
     * CheckIn attempts to check in a user, and if required checks their location against the events
     * @param user User who wants to check in to event
     * @param latitude Latitude of user who wants to check into event
     * @param longitude Longitude of user who wants to check into event
     * @return True if user successfully checked in, false otherwise
     */
    public boolean checkIn(User user, Double latitude, Double longitude){
        if(!geo) {
            (new Database()).checkIn(user,this);
            if(!attendees.hasUser(user))
                attendees.add(user);
            return true;//success
        }else if(haversine(latitude,longitude,this.locationGeoLat,this.locationGeoLong) < 3000){
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
     * @return True if user successfully checked in (only if event geo is disabled), false otherwise
     */
    public boolean checkIn(User user){
        if(geo)
            return false;//failed to check in as no location provided

        (new Database()).checkIn(user,this);
        if(!attendees.hasUser(user))
            attendees.add(user);
        return true;//success
    }

    /** Simple haversine implementation to covert two latitude longitude pairs to a distance in meters, see https://www.movable-type.co.uk/scripts/latlong.html
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
     */
    public void setGeolocationData(double latitude, double longitude){
        this.locationGeoLat=latitude;
        this.locationGeoLong=longitude;
    }

    /**
     * The method returns the id of the event
     *
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
     * A method sets the id of the event
     * @param id the new id of the event
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * A method which returns the host of the event
     * @return The host of the event
     */
    public User getHost() {
        return host;
    }

    /**
     * A method which sets the host of the event
     * @param host the new host of the event
     */
    public void setHost(User host) {
        this.host = host;
    }

    /**
     * A method which returns the description of the event
     * @return The description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * A method which sets the description of the event
     * @param description the new description of the event
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * A method gets the poster Ref String to database
     * @return the string of reference
     */
    public String getPosterRef() {
        return posterRef;
    }

    /**
     * A method sets the poster Ref String to database
     * @param posterRef the string of reference
     */
    public void setPosterRef(String posterRef) {
        this.posterRef = posterRef;
    }

    /**
     * A method that gets the date and time of the event
     * @return a Date object
     */
    public Date getTime() {
        return time;
    }

    /**
     * A method that sets the date and time of the event
     * @param time the date object to set
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * A method that gets the location string
     * @return the string of the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * A method that sets the location string
     * @param location the string of the location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * A method that gets the latitude of the location
     * @return the latitude of the location
     */
    public Double getLocationGeoLat() {
        return locationGeoLat;
    }

    /**
     * A method that sets the latitude of the location
     * @param locationGeoLat the latitude of the location
     */
    public void setLocationGeoLat(Double locationGeoLat) {
        this.locationGeoLat = locationGeoLat;
    }

    /**
     * A method that gets the longitude of the location
     * @return the longitude of the location
     */
    public Double getLocationGeoLong() {
        return locationGeoLong;
    }

    /**
     * A method that sets the longitude of the location
     * @param locationGeoLong the longitude of the location
     */
    public void setLocationGeoLong(Double locationGeoLong) {
        this.locationGeoLong = locationGeoLong;
    }

    /**
     * A method that gets the checkin id for the event qr code
     * @return the checkin id
     */
    public String getCheckinId() {
        return checkinId;
    }

    /**
     * A method that sets the checkin id for the event qr code
     * @param checkinId the checkin id
     */
    public void setCheckinId(String checkinId) {
        this.checkinId = checkinId;
    }

    /**
     * A method that gets the checkin qr code for the event
     * @return the checkin qr code
     */
    public String getCheckinQR() {  return checkinQR; }

    /**
     * A method that sets the checkin qr code for the event
     * @param checkinQR the checkin qr code
     */
    public void setCheckinQR(String checkinQR) {
        this.checkinQR = checkinQR;
    }

    /**
     * A method that gets the promote id for the event qr code
     * @return the promote id
     */
    public String getPromoteId() {
        return promoteId;
    }

    /**
     * A method that sets the promote id for the event qr code
     * @param promoteId the promote id
     */
    public void setPromoteId(String promoteId) {
        this.promoteId = promoteId;
    }

    /**
     * A method that gets the promote qr code for the event
     * @return the promote qr code
     */
    public String getPromoteQR() {
        return promoteQR;
    }

    /**
     * A method that sets the promote qr code for the event
     * @param promoteQR the promote qr code
     */
    public void setPromoteQR(String promoteQR) {
        this.promoteQR = promoteQR;
    }

    /**
     * A method that gets the boolean value of the location
     * @return the boolean value of the location
     */
    public Boolean getGeo() {
        return geo;
    }

    /**
     * A method that sets the boolean value of the location
     * @param geo the boolean value of the location
     */
    public void setGeo(Boolean geo) {
        this.geo = geo;
    }

    /**
     * A method that gets the limit of the event
     * @return the limit of the event
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * A method that sets the limit of the event
     * @param limit the limit of the event
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

//    /**
//     * This method compares two city objects based on event id
//     *
//     * @param o the object to be compared.
//     * @return an integer specifying the comparison between events
//     */
//    @Override
//    public int compareTo(Object o) {
//        Event event = (Event) o;
//        return this.id.compareTo(event.getId());
//    }

    /**
     * A method that get Bitmap of the QR code from the checkin id
     * @return the Bitmap of the QR code from the checkin id
     */
    public Bitmap getQRCodeFromID(int width, int height) {
        if (checkinId == null) {
            return null;
        }
        return QRCodeGenerator.generateQRCode(checkinId, width, height);
    }

    /**
     * A method that checks if the current user  is signed up for the event
     * @return a boolean value of the current user signed up
     */
    public Boolean isCurrentUserSignedUp() {
        return currentUserSignedUp;
    }

    /**
     * A method that sets the current user is or not signed up for the event
     * @param currentUserSignedUp the boolean value of the current user signed up
     */
    public void setCurrentUserSignedUp(Boolean currentUserSignedUp) {
        this.currentUserSignedUp = currentUserSignedUp;
    }

    /**
     * A method that gets the current user is or not signed up for the event
     * @return the boolean value of the current user signed up
     */
    public Boolean isCurrentUserCheckedIn() {
        return currentUserCheckedIn;
    }

    /**
     * A method that sets the current user is or not checked in for the event
     * @param currentUserCheckedIn the boolean value of the current user checked in
     */
    public void setCurrentUserCheckedIn(Boolean currentUserCheckedIn) {
        this.currentUserCheckedIn = currentUserCheckedIn;
    }

}


