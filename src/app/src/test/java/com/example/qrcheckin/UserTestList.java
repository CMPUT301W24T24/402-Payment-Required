package com.example.qrcheckin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;

import org.junit.Test;

import java.util.ArrayList;

public class UserTestList {
    User user1 = new User("123", "Tina", "ngocthuy@gmail.com", "12344555", "123", false, false);
    User user2 = new User("345", "Tina2", "ngocthuy@gmail.com", "12344555", "123", false, false);

    @Test
    public void testAdd() {
        UserList userlist = new UserList();
        assertEquals(0, userlist.countUsers());
        userlist.add(user1);
        assertEquals(1, userlist.countUsers());
        userlist.add(user2);
        assertEquals(2, userlist.countUsers());
    }

    @Test
    public void testAddException() {
        UserList userlist = new UserList();
        userlist.add(user1);
        assertEquals(1, userlist.countUsers());
        assertThrows(IllegalArgumentException.class, () -> {
           userlist.add(user1);
        });
    }

    @Test
    public void testGetUsers() {
        UserList userlist = new UserList();
        userlist.add(user1);
        userlist.add(user2);

        ArrayList<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user2);
        ArrayList<User> actualUsers = userlist.getUsers();
        assertEquals(expectedUsers, actualUsers);

        //When the list is empty
        ArrayList<User> userList2 = new UserList().getUsers();
        assertEquals(userList2, new ArrayList<User>());
    }

    @Test
    public void testHasUser() {
        UserList userList = new UserList();
        //Case when there is none
        assertFalse(userList.hasUser(user1));
        //Case when there is one
        userList.add(user1);
        assertTrue(userList.hasUser(user1));
        assertFalse(userList.hasUser(user2));
        //Case when there are multiple users
        userList.add(user2);
        assertTrue(userList.hasUser(user2));
    }

    @Test
    public void testCountUsers() {
        UserList userList = new UserList();
        //Case when there is none
        assertEquals(userList.countUsers(), 0);
        //Case when there is one
        userList.add(user1);
        assertEquals(userList.countUsers(), 1);
        //Case when there are multiple
        userList.add(user2);
        assertEquals(userList.countUsers(), 2);
    }

    @Test
    public void testRemoveUser() {
        UserList userList = new UserList();
        userList.add(user1);
        userList.add(user2);
        //check if user1 is removed properly
        assertTrue(userList.hasUser(user1));
        userList.removeUser(user1);
        assertFalse(userList.hasUser(user1));
        //check if user2 is still in the list
        assertTrue(userList.hasUser(user2));
        assertEquals(1, userList.countUsers());
    }

    @Test
    public void testRemoveUserException() {
        UserList userList = new UserList();
        //the list only has one user, which is user1
        userList.add(user1);
        assertEquals(1, userList.countUsers());
        assertTrue(userList.hasUser(user1));
        assertThrows(IllegalArgumentException.class, () -> {
            userList.removeUser(user2);
        });
    }
}
