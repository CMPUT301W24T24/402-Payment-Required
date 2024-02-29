package com.example.qrcheckin.core;

public class notification {

    private String message;
    private String time;
    private String eventName;


    public notification(String m, String t, String e) {
        this.message = m;
        this.time = t;
        this.eventName = e;
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
}
