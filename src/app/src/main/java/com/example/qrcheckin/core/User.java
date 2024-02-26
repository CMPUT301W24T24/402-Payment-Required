package com.example.qrcheckin.core;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String homepage;
    private boolean geo;
    private boolean admin;

    /**
     * Constructor for the User class
     * @param id the id of the user
     * @param name the name of the user
     * @param email the email of the user
     * @param phone the phone number of the user
     * @param homepage the homepage of the user
     * @param geo if the user has geo enabled
     * @param admin if the user is an admin
     */
    public User(String id, String name, String email, String phone, String homepage, boolean geo, boolean admin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.homepage = homepage;
        this.geo = geo;
        this.admin = admin;
    }

    /**
     * This method display all the data of the user
     * @return all data of the user
     */
    public String getDataString() {
        return "ID: " + this.id + ", Name: " + this.name + ", Email: " + this.email + ", Phone: " + this.phone + ", Homepage: " + this.homepage + ", Geo: " + this.geo + ", Admin: " + this.admin;
    }

    /**
     * This method returns the id of the user
     * @return the id of the user
     */
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public boolean isGeo() {
        return geo;
    }

    public void setGeo(boolean geo) {
        this.geo = geo;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
