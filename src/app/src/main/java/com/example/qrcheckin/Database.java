package com.example.qrcheckin;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Database {

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;
    private CollectionReference checkinsRef;

    Database() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        eventsRef = db.collection("events");
        checkinsRef = db.collection("checkins");
    }

    Database(ArrayList<User> usersList, ArrayList<Event> eventsList) {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        eventsRef = db.collection("events");
        checkinsRef = db.collection("checkins");
        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    usersList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String id = doc.getId();
                        User newUser = new User(id, doc.getString("name"), doc.getString("email"), doc.getString("phone"), doc.getString("homepage"), doc.getBoolean("geo"), doc.getBoolean("admin"));
                        // TODO remove this log
                        usersList.add(newUser);
                        Log.d("Firestore", "User fetched: " + newUser.getDataString());
                    }
                }
            }
        });
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public boolean addUser(User user) {
        CollectionReference usersRef = db.collection("users");
        return true;
    }
}
