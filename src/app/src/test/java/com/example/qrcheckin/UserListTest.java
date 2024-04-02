package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;

import org.junit.Test;

import java.util.ArrayList;

public class UserListTest {
    private User mockUser() {
        User user = new User("123", "Test", "TestE", "123", "http", false, false);
        return user;
    }

    @Test
    public void testAdd() {
        UserList userList = new UserList();
        assertEquals(0, userList.countUsers());
        userList.add(mockUser());
        assertEquals(1, userList.countUsers());
    }

    @Test
    public void testAddException() {
        UserList userList = new UserList();
        userList.add(mockUser());
        //add the user that is already in the list to see whether exception has thrown
        assertThrows(IllegalArgumentException.class, () -> {
            userList.add(mockUser());
        });
    }

    @Test
    public void testHasUser() {
        UserList userList = new UserList();
        User user = mockUser();
        assertFalse(userList.hasUser(user));
        userList.add(user);
        assertTrue(userList.hasUser(user));
    }

    @Test
    public void testCountUsers() {
        UserList userList = new UserList();
        assertEquals(0, userList.countUsers());
        userList.add(mockUser());
        assertEquals(1, userList.countUsers());
        User user2 = new User("0873", "Test", "TestE1", "123", "http", false, false);
        userList.add(user2);
        assertEquals(2, userList.countUsers());
    }

    @Test
    public void testRemoveUser() {
        UserList userList = new UserList();
        User user = mockUser();
        userList.add(user);
        assertEquals(1, userList.countUsers());
        userList.removeUser(user);
        assertEquals(0, userList.countUsers());
    }

    @Test
    public void testRemoveUserException() {
        UserList userList = new UserList();
        assertThrows(IllegalArgumentException.class, () -> {
            userList.removeUser(mockUser());
        });
    }

    @Test
    public void testGetUsers() {
        User user =  mockUser();
        UserList userList = new UserList();
        userList.add(user);
        ArrayList<User> userList2 = new ArrayList<User>();
        userList2.add(user);
        assertEquals(userList.getUsers(), userList2);
    }
}