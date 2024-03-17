package com.example.qrcheckin.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * This class is used to interact with the Firestore database
 * It can be used to fetch, add, edit and delete users
 * It can also be used to add events and check in users to events
 */
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
        data.put("location_geo_lat", event.getLocationGeoLat());
        data.put("location_geo_long", event.getLocationGeoLong());
        data.put("checkin_id", null);
        data.put("checkin_qr", null);
        data.put("promote_id", null);
        data.put("promote_qr", null);
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

    /**
     * This static function is used to set on event listeners to the list of events from the database
     * It's used to update the list of events in the EventArrayAdaptor in all three explore, my and hosted events
     * @param eventList the list to update
     * @param mEventArrayAdaptor the MutableLiveData of the EventArrayAdaptor to notify the adaptor of the change
     * @param currentUserId the id of the current user from the app user
     * @param type the type of the event list, explore, my or hosted
     */
    public static void onEventListChanged(ArrayList<Event> eventList, MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor, String currentUserId, String type) {
        CollectionReference cr = FirebaseFirestore.getInstance().collection("events");
        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    Log.d("Firestore", "Event list changed " + querySnapshots.size());
                    eventList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        DocumentReference hostRef = doc.getDocumentReference("host");
                        Log.d("Firestore", "Event fetched " + doc.getId());
                        fetchHost(doc, hostRef, mEventArrayAdaptor, eventList, currentUserId, type);
                    }
                }

            }
        });

    }
    /**
     * Fetch the host of an event and add the event to the eventList
     * only works with onEventListChanged
     * @param doc The event document
     * @param userRef The reference to the host
     * @param mEventArrayAdaptor The MutableLiveData of the EventArrayAdaptor
     * @param eventList The list of events
     * @param currentUserId The id of the current user
     */
    private static void fetchHost(QueryDocumentSnapshot doc, DocumentReference userRef, MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor, ArrayList<Event> eventList, String currentUserId, String type) {
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot userDoc) {
                if (userDoc.exists()) {
                    User user = new User(userDoc.getId(),
                            userDoc.getString("name"),
                            userDoc.getString("email"),
                            userDoc.getString("phone"),
                            userDoc.getString("homepage"),
                            Boolean.TRUE.equals(userDoc.getBoolean("geo")),
                            Boolean.TRUE.equals(userDoc.getBoolean("admin")),
                            userDoc.getString("imageRef")
                    );
                    Event event = new Event(doc.getId(),
                            user,
                            doc.getString("name"),
                            doc.getString("description"),
                            doc.getString("posterRef"),
                            doc.getDate("time"),
                            doc.getString("location"),
                            doc.getDouble("location_geo_lat"),
                            doc.getDouble("location_geo_long"),
                            doc.getString("checkin_id"),
                            doc.getString("checkin_qr"),
                            doc.getString("promote_id"),
                            doc.getString("promote_qr"),
                            Boolean.TRUE.equals(doc.getBoolean("geo")),
                            doc.getLong("limit").intValue(),
                            null
                    );
                    Log.d("Firestore", "Host fetched " + user.getName());
                    switch(type) {
                        case "explore":
                            getCurrentUserCheckedIns(event, mEventArrayAdaptor, currentUserId, Boolean.FALSE, eventList);
                            getCurrentUserSignedUps(event, mEventArrayAdaptor, currentUserId, Boolean.FALSE, eventList);
                            eventList.add(event);
                            break;
                        case "my":
                            getCurrentUserCheckedIns(event, mEventArrayAdaptor, currentUserId, Boolean.TRUE, eventList);
                            getCurrentUserSignedUps(event, mEventArrayAdaptor, currentUserId, Boolean.TRUE, eventList);
                            break;
                        case "hosted":
                            if (user.getId().equals(currentUserId)) {
                                getCurrentUserCheckedIns(event, mEventArrayAdaptor, currentUserId, Boolean.FALSE, eventList);
                                getCurrentUserSignedUps(event, mEventArrayAdaptor, currentUserId, Boolean.FALSE, eventList);
                                eventList.add(event);
                            }
                            break;
                        default:
                            Log.e("Firestore", "Unknown type");
                    }
                    Objects.requireNonNull(mEventArrayAdaptor.getValue()).notifyDataSetChanged();
                } else {
                    Log.e("Firestore", "Host not found");
                }
            }
        });
    }

    /**
     * Get the current user's check ins for an event
     * only works with onEventListChanged, fetchHost
     * @param event The event to check
     * @param mEventArrayAdaptor The MutableLiveData of the EventArrayAdaptor
     * @param userId The id of the current user
     */
    private static void getCurrentUserCheckedIns(Event event, MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor, String userId, Boolean toAdd, ArrayList<Event> eventList) {
        CollectionReference cr = FirebaseFirestore.getInstance().collection("checkins");
        cr.whereEqualTo("event_id", event.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    event.setCurrentUserCheckedIn(Boolean.FALSE);
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (doc.getString("user_id").equals(userId)) {
                            Log.d("Firestore", "Checked in");
                            event.setCurrentUserCheckedIn(Boolean.TRUE);
                        }
                    }
                    if (toAdd && event.isCurrentUserCheckedIn() && eventList.contains(event) == Boolean.FALSE) {
                        eventList.add(event);
                    }
                    Objects.requireNonNull(mEventArrayAdaptor.getValue()).notifyDataSetChanged();
                }
            }
        });

    }

    /**
     * Get the current user's sign ups for an event
     * only works with onEventListChanged, fetchHost
     * @param event The event to check
     * @param mEventArrayAdaptor The MutableLiveData of the EventArrayAdaptor
     * @param userId The id of the current user
     */
    private static void getCurrentUserSignedUps(Event event, MutableLiveData<EventArrayAdaptor> mEventArrayAdaptor, String userId, Boolean toAdd, ArrayList<Event> eventList) {
        CollectionReference cr = FirebaseFirestore.getInstance().collection("signUpTable");
        cr.whereEqualTo("event_id", event.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    event.setCurrentUserSignedUp(Boolean.FALSE);
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (doc.getString("user_id").equals(userId)) {
                            Log.d("Firestore", "Signed up");
                            event.setCurrentUserSignedUp(Boolean.TRUE);
                        }
                    }
                    if (toAdd && event.isCurrentUserSignedUp() && eventList.contains(event) == Boolean.FALSE) {
                        eventList.add(event);
                    }
                    Objects.requireNonNull(mEventArrayAdaptor.getValue()).notifyDataSetChanged();
                }
            }
        });

    }

}
