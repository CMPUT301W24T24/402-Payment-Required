package com.example.qrcheckin.core;

import com.google.firebase.Timestamp;

/**
 * Notification contains
 */
public class Notification {

    /**
     * message of the notification
     */
    private String message;
    /**
     * time of the notification
     */
    private String time;
    /**
     * name of the event the notification came from
     */
    private String eventName;
    /**
     * ID of the event the notification came from
     */
    private String eventId;
    /**
     * the timestamp of the notification
     */
    private Timestamp realTime;


    /**
     * Constructor for the Notification class
     *
     * @param message content of the notification
     * @param time time that the notification was sent at
     * @param eventName the name of the event the notification is associated with
     * @param eventId the ID of the event the notification is associated with
     */
    public Notification(String message, String time, String eventName, String eventId, Timestamp realTime) {
        this.message = message;
        this.time = time;
        this.eventName = eventName;
        this.eventId = eventId;
        this.realTime = realTime;
    }

    /**
     * gets the message content of the notification
     *
     * @return String  message content of the notification
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * gets the time of the notification
     *
     * @return String  the time the notification was sent
     */
    public String getTime() {
        return this.time;
    }

    /**
     * gets the event name the notification was sent from
     *
     * @return String  the name of the event this notification was sent from
     */
    public String getEventName() {
        return this.eventName;
    }

    /**
     * gets the eventID of the notification
     *
     * @return String  the eventID the notification came from
     */
    public String getEventId() {return this.eventId; }

    /**
     * gets the Time of the notification as a Timestamp
     *
     * @return Timestamp  the time the notification was sent as a timestamp
     */
    public Timestamp getRealTime() {return this.realTime;}
}
