package com.example.qrcheckin.core;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

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

    /**
     * Get a user from the database
     * @param id The id of the user
     * @return The user object, or null if the user does not exist
     */
    public User getUser(String id) {
        DocumentReference docRef = usersRef.document(id);
        ArrayList<User> usersList = new ArrayList<>();
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                usersList.add(new User(id, documentSnapshot.getString("name"), documentSnapshot.getString("email"), documentSnapshot.getString("phone"), documentSnapshot.getString("homepage"), Boolean.TRUE.equals(documentSnapshot.getBoolean("geo")), Boolean.TRUE.equals(documentSnapshot.getBoolean("admin"))));
                // TODO: remove this log
                Log.d("Firestore", "User fetched: " + usersList.get(0).getDataString());
            } else {
                // TODO: remove this log
                Log.e("Firestore", "User not found");
            }
        }).addOnFailureListener(e -> {
            // TODO: remove this log
            Log.e("Firestore", e.toString());
        });
        if (usersList.size() == 0) {
            return null;
        } else {
            return usersList.get(0);
        }
    }

    /**
     * Add a new user to the database
     * @param user The user to add
     */
    public void addNewUser(User user) {
        DocumentReference docRef = usersRef.document(user.getId());
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("homepage", user.getHomepage());
        data.put("geo", user.isGeo());
        data.put("admin", user.isAdmin());
        docRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", e.toString());
                    }
                });
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}
