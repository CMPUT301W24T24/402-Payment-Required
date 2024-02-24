package com.example.qrcheckin.core;

import com.example.qrcheckin.core.User;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class which keeps track of a list of user objects (the people who are attending an event)
 */
public class UserList {
    private List<User> users = new ArrayList<>();

    /**
     * This adds a user to the list (the event) if the user is not already in the list
     * @param user
     * The user which is attending the event
     */
    public void add(User user) {
        if (users.contains(user)) {
            throw new IllegalArgumentException("This user is already in the event.");
        }
        users.add(user);
    }

    /**
     * This returns a list of the users (those attending the event)
     * @return
     * The list of users attending the event
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Determines if a user is in the list (the event)
     * @param user who is being checked if is in the list
     * @return
     * true if the user is in the list, false if the user is not in the list
     */
    public Boolean hasUser(User user) {
        return users.contains(user);
    }

    /**
     * Returns a count with the number of users in the list (# of people attending the event)
     * @return
     * The size of the user list
     */
    public int countUsers() {
        return users.size();
    }

    /**
     * Removes a user from the list if the user is in the list
     * @param user who is being removed from the list
     */
    public void removeUser(User user) {
        if (!hasUser(user)) {
            throw new IllegalArgumentException("The user is already not in the event.");
        }
        users.remove(user);
    }

}
