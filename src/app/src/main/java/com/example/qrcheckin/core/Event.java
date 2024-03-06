package com.example.qrcheckin.core;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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

    public Event(User host, String name, String description, String posterRef, Date time, String location, Double locationGeoLat, Double locationGeoLong, String checkinId, String checkinRq, String promoteId, String promoteRq, Boolean geo, Integer limit) {
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

    public void setId(String id) {
        this.id = id;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPosterRef() {
        return posterRef;
    }

    public void setPosterRef(String posterRef) {
        this.posterRef = posterRef;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLocationGeoLat() {
        return locationGeoLat;
    }

    public void setLocationGeoLat(Double locationGeoLat) {
        this.locationGeoLat = locationGeoLat;
    }

    public Double getLocationGeoLong() {
        return locationGeoLong;
    }

    public void setLocationGeoLong(Double locationGeoLong) {
        this.locationGeoLong = locationGeoLong;
    }

    public String getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(String checkinId) {
        this.checkinId = checkinId;
    }

    public String getCheckinRq() {
        return checkinRq;
    }

    public void setCheckinRq(String checkinRq) {
        this.checkinRq = checkinRq;
    }

    public String getPromoteId() {
        return promoteId;
    }

    public void setPromoteId(String promoteId) {
        this.promoteId = promoteId;
    }

    public String getPromoteRq() {
        return promoteRq;
    }

    public void setPromoteRq(String promoteRq) {
        this.promoteRq = promoteRq;
    }

    public Boolean getGeo() {
        return geo;
    }

    public void setGeo(Boolean geo) {
        this.geo = geo;
    }

    public Integer getLimit() {
        return limit;
    }

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

}


