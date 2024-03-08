package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;

import org.junit.Test;

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
}
