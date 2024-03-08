package com.example.qrcheckin.core;

/**
 * Notification contains
 */
public class Notification {

    private String message;
    private String time;
    private String eventName;
    private String eventId;


    /**
     * Constructor for the Notification class
     *
     * @param message content of the notification
     * @param time time that the notification was sent at
     * @param eventName the name of the event the notification is associated with
     * @param eventId the ID of the event the notification is associated with
     */
    public Notification(String message, String time, String eventName, String eventId) {
        this.message = message;
        this.time = time;
        this.eventName = eventName;
        this.eventId = eventId;
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
    public String getEventId() {return this.eventId ; }
}
