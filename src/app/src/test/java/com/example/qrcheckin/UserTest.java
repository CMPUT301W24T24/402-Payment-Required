package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.graphics.Bitmap;

import com.example.qrcheckin.core.User;

import org.junit.Test;

public class UserTest {

    private User mockUser() {
        User user = new User("123", "Tina", "hey@gmail.com", "12344555", "123", false, false);
        return user;
    }
    User user1 = new User("56784", "Pseudo", "test@gmail.com", "0905192111", "html", true, true, "users/kcMZVbm6wAYlaKAct5Os");
    User user2 = new User("", "", "", "", "", false, false, "");


    @Test
    public void testGetDataString() {
        String expectedString = "ID: 123, Name: Tina, Email: hey@gmail.com, Phone: 12344555, Homepage: 123, Geo: false, Admin: false";
        String actualString = mockUser().getDataString();
        assertEquals(expectedString, actualString);
    }
    @Test
    public void testGetID() {
        assertEquals("123", mockUser().getId());
        assertEquals("", user2.getId());
        assertEquals("56784", user1.getId());
    }

    @Test
    public void testSetID() {
        User user = mockUser();
        assertEquals("123", user.getId());
        user.setId("hello");
        assertEquals("hello", user.getId());
        user.setId("");
        assertEquals("", user.getId());
    }

    @Test
    public void testGetName() {
        assertEquals("Tina", mockUser().getName());
        assertEquals("", user2.getName());
        assertEquals("Pseudo", user1.getName());
    }

    @Test
    public void testSetName() {
        User user = mockUser();
        assertEquals("Tina", user.getName());
        user.setName("hello");
        assertEquals("hello", user.getName());
        user.setName("");
        assertEquals("", user.getName());
    }

    @Test
    public void testGetEmail() {
        assertEquals("hey@gmail.com", mockUser().getEmail());
        assertEquals("", user2.getEmail());
        assertEquals("test@gmail.com", user1.getEmail());
    }

    @Test
    public void testSetEmail() {
        User user = mockUser();
        assertEquals("hey@gmail.com", user.getEmail());
        user.setEmail("hello");
        assertEquals("hello", user.getEmail());
        user.setEmail("");
        assertEquals("", user.getEmail());
    }

    @Test
    public void testGetPhone() {
        assertEquals("12344555", mockUser().getPhone());
        assertEquals("", user2.getPhone());
        assertEquals("0905192111", user1.getPhone());
    }

    @Test
    public void testSetPhone() {
        User user = mockUser();
        assertEquals("12344555", user.getPhone());
        user.setPhone("hello");
        assertEquals("hello", user.getPhone());
        user.setPhone("");
        assertEquals("", user.getPhone());
    }

    @Test
    public void testGetHomepage() {
        assertEquals("123", mockUser().getHomepage());
        assertEquals("", user2.getHomepage());
        assertEquals("html", user1.getHomepage());
    }

    @Test
    public void testSetHomepage() {
        User user = mockUser();
        assertEquals("123", user.getHomepage());
        user.setHomepage("hello");
        assertEquals("hello", user.getHomepage());
        user.setHomepage("");
        assertEquals("", user.getHomepage());
    }

    @Test
    public void testIsGeo() {
        assertTrue(user1.isGeo());
        assertFalse(user2.isGeo());
    }

    @Test
    public void testSetGeo() {
        User user = mockUser();
        assertFalse(user.isGeo());
        user.setGeo(true);
        assertTrue(user.isGeo());
    }

    @Test
    public void testIsAdmin() {
        assertTrue(user1.isAdmin());
        assertFalse(user2.isAdmin());
    }

    @Test
    public void testGetImageRef() {
        assertEquals("users/kcMZVbm6wAYlaKAct5Os", user1.getImageRef());
        assertEquals("", user2.getImageRef());
        assertEquals(null, mockUser().getImageRef());
    }

    @Test
    public void testSetImageRef() {
        User user = mockUser();
        assertEquals(null, user.getImageRef());
        user.setImageRef("users/kcMZVbm6wAYlaKAct5Os");
        assertEquals("users/kcMZVbm6wAYlaKAct5Os", user.getImageRef());
    }

    @Test
    public void testEquals() {
        User user = mockUser();
        assertFalse(user.equals(user1));
        user1.setId(user.getId());
        assertTrue(user.equals(user1));
    }
}
