package com.example.qrcheckin.core;

public class notification {

    private String message;
    private String time;
    private String eventName;
    private String eventId;


    public notification(String message, String time, String eventName, String eventId) {
        this.message = message;
        this.time = time;
        this.eventName = eventName;
        this.eventId = eventId;
    }

    public String getMessage() {
        return this.message;
    }

    public String getTime() {
        return this.time;
    }

    public String getEventName() {
        return this.eventName;
    }
    public String getEventId() {
        return this.eventId ;
    }
}
