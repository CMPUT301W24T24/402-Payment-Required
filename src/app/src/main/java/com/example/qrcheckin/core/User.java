package com.example.qrcheckin.core;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * The user class which connects with the database and uses the app
 */
public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String homepage;
    private String imageRef;
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
        this.imageRef = null;
    }

    /**
     * Constructor for the User class
     * @param id the id of the user
     * @param name the name of the user
     * @param email the email of the user
     * @param phone the phone number of the user
     * @param homepage the homepage of the user
     * @param geo if the user has geo enabled
     * @param admin if the user is an admin
     * @param imageRef the reference path on firestore to the image of the user
     */
    public User(String id, String name, String email, String phone, String homepage, boolean geo, boolean admin, String imageRef) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.homepage = homepage;
        this.geo = geo;
        this.admin = admin;
        this.imageRef = imageRef;
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
        return this.id;
    }

    /**
     * This method sets the id of the user
     * @param id the id of the user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method returns the name of the user
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * This method sets the name of the user
     * @param name the name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns the email of the user
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method sets the email of the user
     * @param email the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This method returns the phone number of the user
     * @return the phone number of the user
     */
    public String getPhone() {
        return phone;
    }

    /**
     * This method sets the phone number of the user
     * @param phone the phone number of the user
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * This method returns the homepage of the user
     * @return the homepage of the user
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * This method sets the homepage of the user
     * @param homepage the homepage of the user
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    /**
     * This method returns if the user has geo enabled
     * @return if the user has geo enabled
     */
    public boolean isGeo() {
        return geo;
    }

    /**
     * This method sets if the user has geo enabled
     * @param geo if the user has geo enabled
     */
    public void setGeo(boolean geo) {
        this.geo = geo;
    }

    /**
     * This method returns if the user is an admin
     * @return if the user is an admin
     */
    public boolean isAdmin() {
        return admin;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    /**
     * Generates a profile picture for the user
     * Reference: https://stackoverflow.com/questions/2655402/android-canvas-drawtext Gaz. Accessed 2024-03-14
     * @return the profile picture
     */
    public Bitmap generateProfilePicture() {
        String name = "NU";
        if (this.name.length() >= 2) {
            name = this.name.substring(0, 2);
        }
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        // Fill background
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(50, 50, 35, paint);

        // Draw text
        paint.setColor(Color.BLACK);
        paint.setTextSize(25);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(name, 50, 59, paint);

        // Draw border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(hashedColor(name.charAt(0)));
        canvas.drawCircle(50, 50, 35, paint);
        return bitmap;
    }
    private int hashedColor(Character s) {
        String colorString = "#";
        int ascii = (int) s;
        colorString += Integer.toHexString(ascii % 255);
        colorString += Integer.toHexString((ascii * 5 + 12) % 255);
        colorString += Integer.toHexString((ascii * 7 + 3) % 255);

        return Color.parseColor(colorString);
    }

    /**
     * Checks if this object equals another object by comparing user id's
     * Reference: https://stackoverflow.com/questions/62718310/how-to-use-the-arraylist-contain-to-check-the-object-in-java Chakraborty Abhinaba. Accessed 2024-03-19
     * @param obj The object to compare against
     * @return The boolean result of the comparison
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this.getClass() == obj.getClass()) {
            return this.getId() == ((User) obj).getId();
        }
        return false;
    }
}
