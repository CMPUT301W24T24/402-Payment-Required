package com.example.qrcheckin.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class Database {

    public interface UserListener {
        void onUserFetched(User user);
        void onUserAdded(String id);
    }

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private CollectionReference usersRef;
    private UserListener userListener;
    private CollectionReference eventsRef;
    private CollectionReference checkinsRef;
    private CollectionReference signupsRef;

    /**
     * Create a new Database object
     * initialize the database and the collections
     */
    public Database() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        usersRef = db.collection("users");
        eventsRef = db.collection("events");
        checkinsRef = db.collection("checkins");
    }

    /**
     * Create a new Database object
     * initialize the database and the collections
     * and set the UserListener
     * @param context The context of the UserListener (usually the activity)
     */
    public Database(Context context) {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        usersRef = db.collection("users");
        eventsRef = db.collection("events");
        checkinsRef = db.collection("checkins");
        userListener = (UserListener) context;
    }

    /**
     * Get a user from the database by id and call the UserListener.onUserFetched method
     * It will throw a RuntimeException if the UserListener is not set
     * @param id The id of the user
     */
    public void getUser(String id) {
        if (userListener == null) {
            Log.e("Firestore", "UserListener not set");
            throw new RuntimeException("UserListener not set");
        }
        DocumentReference docRef = usersRef.document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d("Firestore", "User fetched");
                    User user = new User(id,
                            documentSnapshot.getString("name"),
                            documentSnapshot.getString("email"),
                            documentSnapshot.getString("phone"),
                            documentSnapshot.getString("homepage"),
                            Boolean.TRUE.equals(documentSnapshot.getBoolean("geo")),
                            Boolean.TRUE.equals(documentSnapshot.getBoolean("admin")),
                            documentSnapshot.getString("imageRef")
                    );
                    userListener.onUserFetched(user);
                } else {
                    // TODO: Handle user not found
                    Log.d("Firestore", "User not found");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", e.toString());
            }
        });
    }

    /**
     * Update an existing user (with the same id), will fail if the user does not exist
     * @param user The user edit
     */
    public void editUser(@NonNull User user) {
        DocumentReference docRef = usersRef.document(user.getId());
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("homepage", user.getHomepage());
        data.put("geo", user.isGeo());
        data.put("admin", user.isAdmin());
        data.put("imageRef", user.getImageRef());
        docRef.update(data)
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

    /**
     * Add a new user to the database, call the UserListener.onUserAdded method
     * @param user The user to add, the id of the user will be changed to the id of the document in the database
     */
    public void addUser(@NonNull User user) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("homepage", user.getHomepage());
        data.put("geo", user.isGeo());
        data.put("admin", user.isAdmin());
        data.put("imageRef", user.getImageRef());
        usersRef.add(data)
                .addOnSuccessListener(documentReference -> {
                    user.setId(documentReference.getId());
                    userListener.onUserAdded(documentReference.getId());
                    Log.d("Firestore", "DocumentSnapshot successfully written with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", e.toString());
                });
    }

    /**
     * Delete a user from the database
     * @param id The id of the user to delete
     */
    public void deleteUser(String id) {
        DocumentReference docRef = usersRef.document(id);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", e.toString());
                    }
                });
    }

    /**
     * Find an event in the database by ID, returns null if the ID is not present in the database
     * @param id Unique ID to search for
     * @return The event object with given ID, null otherwise
     */
    public Event getEventByID(String id){
        DocumentSnapshot document = eventsRef.document(id).get().getResult();
        if(document.exists()){
            return document.toObject(Event.class);
        }
        return null;
    }

    /**
     * Adds a given event to the database of available events
     * @param event
     */
    public void addEvent(@NonNull Event event){
        HashMap<String, Object> data = new HashMap<>();
        data.put("host", event.getOwner().getId());
        data.put("name", event.getName());
        data.put("description", event.getDescription());
        data.put("posterRef", null);
        data.put("time", null);
        data.put("location", null);
        data.put("latitude", event.getLatitude());
        data.put("longitude", event.getLongitude());
        data.put("distanceLimit", event.getDistanceLimit());
        data.put("checkinId", null);
        data.put("checkinQR", null);
        data.put("promoteId", null);
        data.put("promoteQR", null);
        data.put("geo", null);
        data.put("limit", null);
        eventsRef.add(data)
                .addOnSuccessListener(documentReference -> {
                    event.setId(documentReference.getId());
                    Log.d("Firestore", "DocumentSnapshot successfully written with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", e.toString());
                });
    }

    /**
     * Check in a user to an event
     * @param user The user to check in
     * @param event The event to check in to
     */
    public void checkIn(User user, Event event) {
        FieldValue serverTimestamp = FieldValue.serverTimestamp();
        HashMap<String, Object> data = new HashMap<>();
        data.put("user_id", user.getId());
        data.put("event_id", event.getId());
        data.put("time", serverTimestamp);
        data.put("latitude", null);
        data.put("longitude", null);
        checkinsRef.add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Checked in with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", e.toString());
                });
    }

    /**
     * Check in a user to an event with geo location
     * @param user The user to check in
     * @param event The event to check in to
     * @param latitude The latitude of the user's location
     * @param longitude The longitude of the user's location
     */
    public void checkInWithGeo(User user, Event event, double latitude, double longitude) {
        FieldValue serverTimestamp = FieldValue.serverTimestamp();
        HashMap<String, Object> data = new HashMap<>();
        data.put("user_id", user.getId());
        data.put("event_id", event.getId());
        data.put("time", serverTimestamp);
        data.put("latitude", latitude);
        data.put("longitude", longitude);
        checkinsRef.add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Checked in with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", e.toString());
                });
    }

    /**
     * This method fetches the picture of a user from the storage and sets it to an ImageView
     * <a href="https://stackoverflow.com/questions/13854742/byte-array-of-image-into-imageview">ByteArray to bitmap, Dipak Keshariya, HB., accessed on 2024-02-28</a>
     * @param user the user to fetch the picture from
     * @param imageView the ImageView to set the picture to
     */
    public void getUserPicture(User user, ImageView imageView) {
        if (user.getImageRef() == null || user.getImageRef().isEmpty()) {
            Log.e("Firestorage", "No picture reference");
            // TODO: set a default picture or handle no picture
            return;
        }
        storage.getReference().child(user.getImageRef()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length), imageView.getWidth(), imageView.getHeight(), false);
                imageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Firestorage", exception.toString());
            }
        });
    }
}
