package com.example.qrcheckin;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String homepage;
    private boolean geo;
    private boolean admin;

    public User(String id, String name, String email, String phone, String homepage, boolean geo, boolean admin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.homepage = homepage;
        this.geo = geo;
        this.admin = admin;
    }

    public String getDataString() {
        return "ID: " + this.id + ", Name: " + this.name + ", Email: " + this.email + ", Phone: " + this.phone + ", Homepage: " + this.homepage + ", Geo: " + this.geo + ", Admin: " + this.admin;
    }
}
