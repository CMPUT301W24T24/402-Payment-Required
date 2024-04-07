package com.example.qrcheckin.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.qrcheckin.R;
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
import com.google.firebase.storage.StorageReference;

import org.osmdroid.util.GeoPoint;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        DocumentReference hostReference = db.collection("users").document(event.getOwner().getId());
        data.put("host", hostReference);
        data.put("name", event.getName());
        data.put("description", event.getDescription());
        data.put("posterRef", null);
        data.put("time", event.getTime());
        data.put("location", null);
        data.put("location_geo_lat", event.getLocationGeoLat());
        data.put("location_geo_long", event.getLocationGeoLong());
        data.put("checkin_id", null);
        data.put("checkin_qr", null);
        data.put("promote_id", null);
        data.put("promote_qr", null);
        data.put("geo", null);
        data.put("limit", event.getLimit());
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
            imageView.setImageBitmap(user.generateProfilePicture());
            return;
        }
        storage.getReference().child(user.getImageRef()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Firestorage", exception.toString());
                imageView.setImageBitmap(user.generateProfilePicture());
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
                    mEventArrayAdaptor.getValue().getEvents().clear();
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
                        // TODO: add "all" type and modify explore to show only current and future events
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
    public static void deleteEvent(String id) {
        // Delete the event
        FirebaseFirestore.getInstance().collection("events").document(id).delete()
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

        // Delete the related check ins
        FirebaseFirestore.getInstance().collection("checkins").whereEqualTo("event_id", id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                            doc.getReference().delete();
                        }
                    }
                });

        // Delete the related sign ups
        FirebaseFirestore.getInstance().collection("signUpTable").whereEqualTo("event_id", id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                            doc.getReference().delete();
                        }
                    }
                });
    }

    /**
     * Gets the list of users signed up to an event
     * @param userList The empty list that is to be filled with users
     * @param mUserArrayAdaptor The MutableLiveData of the UserArrayAdaptor
     * @param currentEventID The id of the current event
     */
    public static void getUsersSignedUpToEvent(ArrayList<User> userList, MutableLiveData<UserArrayAdaptor> mUserArrayAdaptor, String currentEventID) {
        CollectionReference cr = FirebaseFirestore.getInstance().collection("signUpTable");
        CollectionReference userRef = FirebaseFirestore.getInstance().collection("users");
        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    userList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (Objects.equals(doc.getString("event_id"), currentEventID)) {
                            String userId = doc.getString("user_id");
                            DocumentReference userDoc = userRef.document(userId);
                            Log.d("Firestore", "Document Reference " + userDoc);
                            Log.d("Firestore", "User fetched " + userId);
                            if (userDoc == null) {
                                Log.d("Firestore", "User " + userId +" not found");
                            }
                            else {
                                fetchUser(doc, userDoc, mUserArrayAdaptor, userList);
                            }
                        }
                    }
                    Log.d("Firestore", "User list changed " + userList.size());
                    Objects.requireNonNull(mUserArrayAdaptor.getValue()).notifyDataSetChanged();
                }
                if (querySnapshots == null || querySnapshots.isEmpty()) {
                    userList.clear();
                    Objects.requireNonNull(mUserArrayAdaptor.getValue()).notifyDataSetChanged();
                }

            }
        });
    }

    /**
     * Fetches the current user signed up or checked into an event
     * @param doc The signup document
     * @param userRef The reference to the user
     * @param mUserArrayAdaptor The MutableLiveData of the UserArrayAdapter
     * @param userList The user list to be updated
     */
    private static void fetchUser(QueryDocumentSnapshot doc, DocumentReference userRef, MutableLiveData<UserArrayAdaptor> mUserArrayAdaptor, ArrayList<User> userList) {
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

                    Log.d("Firestore", "User fetched " + user.getName());
                    if (!userList.contains(user)) {
                        userList.add(user);
                    }
                    Objects.requireNonNull(mUserArrayAdaptor.getValue()).notifyDataSetChanged();
                } else {
                    Log.e("Firestore", "User not found");
                }
            }
        });
    }

    /**
     * Sign up the user to the given event
     * @param user The user to be signed up
     * @param event The event the user is signing up to
     */
    public void signUpUser(User user, Event event) {
        CollectionReference signupsRef = FirebaseFirestore.getInstance().collection("signUpTable");

        // Add the event to the collection
        HashMap<String, Object> data = new HashMap<>();
        data.put("user_id", user.getId());
        data.put("event_id", event.getId());
        signupsRef.add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Signed up with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", e.toString());
                });
    }

    /**
     * Retrieves the number of checkins for a specific user and event
     * @param userId The string id of the user
     * @param eventId The string id of the event
     * @param listener The listener to be used for callback
     * Reference: Gemini
     * Prompt: How could I update the database function to be callable in the UserArrayAdaptor class so that I
     *          could set the text of user_number_of_checkins to the number of time the user has checked into a
     *          specific event without introducing a new field numCheckIns in the user store?
     */
    public static void getCheckInCountForUser(String userId, String eventId, OnCheckInCountRetrievedListener listener) {
        CollectionReference checkinRef = FirebaseFirestore.getInstance().collection("checkins");
        checkinRef.whereEqualTo("event_id", eventId)
                .whereEqualTo("user_id", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long checkInCount = task.getResult().size();
                        listener.onCheckInCountRetrieved(checkInCount);
                    } else {
                        Log.w("Firestore", "Error getting check-in count", task.getException());
                        listener.onCheckInCountRetrieved(-1); // Handle error
                    }
                });
    }


    /**
     * Used so the UserArrayAdaptor can handle the retrieved check-in count in its onCheckInCountRetrieved method
     * Reference: Gemini
     * Prompt: How could I update the database function to be callable in the UserArrayAdaptor class so that I
     *          could set the text of user_number_of_checkins to the number of time the user has checked into a
     *          specific event without introducing a new field numCheckIns in the user store?
     */
    public interface OnCheckInCountRetrievedListener {
        void onCheckInCountRetrieved(long checkInCount);
    }



    public static void deleteImage(User user, ImagesUserArrayAdaptor imagesUserArrayAdaptor) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(user.getImageRef());
        imgRef.delete();
        usersRef.document(user.getId()).update("imageRef", null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Firestore", "Image reference deleted");
                imagesUserArrayAdaptor.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", e.toString());
            }
        });
    }


    public static void getAllImages(ImagesEventArrayAdaptor events, ImagesUserArrayAdaptor users) {
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
        CollectionReference eventsRef = FirebaseFirestore.getInstance().collection("events");
        usersRef.whereNotEqualTo(
                "imageRef",
                null
        ).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    users.getUsers().clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (!Objects.requireNonNull(doc.getString("imageRef")).isEmpty()) {
                            User user = new User(doc.getId(),
                                    doc.getString("name"),
                                    doc.getString("email"),
                                    doc.getString("phone"),
                                    doc.getString("homepage"),
                                    Boolean.TRUE.equals(doc.getBoolean("geo")),
                                    Boolean.TRUE.equals(doc.getBoolean("admin")),
                                    doc.getString("imageRef")
                            );
                            Log.d("Firestore", "User fetched for Image" + user.getName());

                            users.getUsers().add(user);
                            users.notifyDataSetChanged();
                        }
                    }
                    users.notifyDataSetChanged();
                }
            }
        });
        eventsRef.whereNotEqualTo(
                "posterRef",
                ""
        ).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    events.getEvents().clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        DocumentReference hostRef = doc.getDocumentReference("host");
                        if (doc.getString("posterRef") != null) {
                            Log.d("FirestoreEventImage", "Event fetched for Image" + doc.getId());
                            hostRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                        events.getEvents().add(event);
                                        events.notifyDataSetChanged();
                                    } else {
                                        Log.e("Firestore", "Host not found");
                                    }
                                }
                            });
                        }
                    }
                    events.notifyDataSetChanged();
                }
            }
        });
    }

    public static void getAllImagesUserPicture(ImagesUserArrayAdaptor imagesUserArrayAdaptor, int position, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ArrayList<User> users = imagesUserArrayAdaptor.getUsers();
        User user = users.get(position);
        storage.getReference().child(user.getImageRef()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("Firestorage", "Image fetched:" + user.getName());
                Log.d("ImageView", "Width: " + imageView.getWidth() + " Height: " + imageView.getHeight());
                Log.d("array", imagesUserArrayAdaptor.getUsers().toString());
                if(imageView.getWidth() == 0) {
                    // TODO: Figure out why it gets called multiple times
                    return;
                }
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Firestorage", exception.toString());
                imageView.setImageResource(R.drawable.unable);
            }
        });
    }

    public static void getAllImagesEventPoster(ImagesEventArrayAdaptor imagesEventArrayAdaptor, int position, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ArrayList<Event> events = imagesEventArrayAdaptor.getEvents();
        Event event = events.get(position);
        storage.getReference().child(event.getPosterRef()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                if(imageView.getWidth() == 0) {
                    // TODO: Figure out why it gets called multiple times
                    return;
                }
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Firestorage", exception.toString());
                imageView.setImageResource(R.drawable.unable);
            }
        });
    }



    public static void deleteImage(Event event, ImagesEventArrayAdaptor imagesEventArrayAdaptor) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(event.getPosterRef());
        imgRef.delete();
        eventsRef.document(event.getId()).update("posterRef", "").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Firestore", "Image reference deleted");
                imagesEventArrayAdaptor.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", e.toString());
            }
        });
    }

    public static void fetchGeoPointsForEvent(String eventId, OnGeoPointsFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference checkinRef = db.collection("checkins");
        checkinRef.whereEqualTo("event_id", eventId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<GeoPoint> geoPoints = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Double latitude = document.getDouble("latitude");
                    Double longitude = document.getDouble("longitude");
                    if (latitude != null && longitude != null) {
                        GeoPoint point = new GeoPoint(latitude, longitude);
                        geoPoints.add(point);
                    }
                }
                listener.onGeoPointsFetched(geoPoints);
            } else {
                Log.e("CheckinDots", "Error getting documents: ", task.getException());
                listener.onGeoPointsFetchFailed(task.getException());
            }
        });
    }

    public interface OnGeoPointsFetchedListener {
        void onGeoPointsFetched(List<GeoPoint> geoPoints);
        void onGeoPointsFetchFailed(Exception e);
    }


    /**
     * Gets the list of users checked into an event
     * @param userList The empty list that is to be filled with users
     * @param mUserArrayAdaptor The MutableLiveData of the UserArrayAdaptor
     * @param currentEventID The id of the current event
     */
    public static void getUsersCheckedIntoEvent(ArrayList<User> userList, MutableLiveData<UserArrayAdaptor> mUserArrayAdaptor, String currentEventID) {
        CollectionReference cr = FirebaseFirestore.getInstance().collection("checkins");
        CollectionReference userRef = FirebaseFirestore.getInstance().collection("users");
        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    userList.clear();
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (Objects.equals(doc.getString("event_id"), currentEventID)) {
                            String userId = doc.getString("user_id");
                            DocumentReference userDoc = userRef.document(userId);
                            Log.d("Firestore", "Document Reference " + userDoc);
                            Log.d("Firestore", "User fetched " + userId);
                            if (userDoc == null) {
                                Log.d("Firestore", "User " + userId +" not found");
                            }
                            else {
                                fetchUser(doc, userDoc, mUserArrayAdaptor, userList);
                            }
                        }
                    }
                    Log.d("Firestore", "User list changed " + userList.size());
                }

            }
        });
    }
}
