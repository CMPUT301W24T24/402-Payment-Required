package com.example.qrcheckin;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The main activity class which contains the navigation bar and starts the user on the Home fragment
 */
public class MainActivity extends AppCompatActivity implements Database.UserListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private User currentUser;
    private Map<String, String> eventIdToName;
    private Set<ListenerRegistration> notificationCollections;
    private Map<String, Boolean> eventMap;
    private Map<String, Boolean> eventMapSignup;
    private Map<String, Boolean> eventMapCheckin;
    private long notificationListenerLastUpdate;
    private FirebaseFirestore notidb;
    private ArrayList<String> mileStoneEvents;
    private ListenerRegistration milestoneListener;
    private Map<String, String> milestoneNames;
    private long milestoneLastUpdate;

    /**
     * When the app is first opened this is called
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        // set up the app bar configuration
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_home,
                R.id.nav_event,
                R.id.nav_host_event,
                R.id.nav_explore_event,
                R.id.nav_profile,
                R.id.nav_notifications,
                R.id.nav_all_event, R.id.nav_all_images, R.id.nav_all_profile).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_all_event).setVisible(false);
        menu.findItem(R.id.nav_all_images).setVisible(false);
        menu.findItem(R.id.nav_all_profile).setVisible(false);

        // asks user to turn on notifications
        // https://stackoverflow.com/a/76108866
        // by Akshay Karande, Accessed 17 March, 2024
        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }


        getDeviceUser(this);

        //Set up the map view
    }

    /**
     * Get the user from the device file, if the file does not exists, create a new user
     * and add it to the database and write the id to the file
     * @param context the context of the application
     */
    private void getDeviceUser(Context context) {
        File file = new File(getFilesDir(), "userDevice.txt");
        Database db = new Database(this);
        
        if (file.exists()) {
            Log.d("login", "File exists");
            try {
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String id = bufferedReader.readLine();
                if (id != null) {
                    db.getUser(id);
                } else {
                    currentUser = createDefaultUser("FailedID");
                    db.addUser(currentUser);
                }
                bufferedReader.close();
                reader.close();
            } catch (FileNotFoundException e) {
                Log.e("login", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login", "Can not read file: " + e.toString());
            }
        } else {
            Log.d("login", "File does not exists");
            currentUser = createDefaultUser("FailedID");
            db.addUser(currentUser);
        }
    }

    /**
     * Write the id to the file
     * <a href="https://stackoverflow.com/questions/11386441/filewriter-not-writing-to-file-in-android">Reference: Writing, Author: FoamyGuy, Accessed: 2024-02-27 </a>
     * <a href="https://developer.android.com/reference/java/io/FileWriter">FileWriter</a>
     * @param id the id of the user
     */
    private void writeIdToFile(String id) {
        File file = new File(getFilesDir(), "userDevice.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(id);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.e("login", "Can not write file: " + e.toString());
        }
    }

    /**
     * This method is called when user is fetched from the database, interface method
     * It sets the user to the application and the user name and picture to the navigation view
     * @param user the user
     */
    @Override
    public void onUserFetched(User user) {
        currentUser = user;
        Log.d("Firestore", "User fetched: " + user.getDataString());

        // set the user to the application
        ((QRCheckInApplication) getApplication()).setCurrentUser(currentUser);

        // set the user name to the navigation view
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView navProfileName = headerView.findViewById(R.id.nav_profile_name);
        navProfileName.setText(currentUser.getName());

        ImageView navProfileImage = headerView.findViewById(R.id.nav_profile_pic);
        // Set the profile picture of the user to the XML view
        Database db = new Database();
        db.getUserPicture(currentUser, navProfileImage);

        // set the notification listeners for event user is signed up for
        createNotificationListeners();

        if (currentUser.isAdmin()) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_all_event).setVisible(true);
            menu.findItem(R.id.nav_all_images).setVisible(true);
            menu.findItem(R.id.nav_all_profile).setVisible(true);
        }
    }

    /**
     * After added the user to the database, set id, interface method
     * @param id the id of the user
     */
    @Override
    public void onUserAdded(String id) {
        currentUser.setId(id);
        writeIdToFile(id);
        onUserFetched(currentUser);
    }

    /**
     * Create a default user
     * @param id the id of the user
     * @return the user
     */
    private User createDefaultUser(String id) {
        return new User(id, "Anonymous User", "", "", "", false, false);
//        return new User(id, "Anonymous User", "", "", "", false, false, "users/cat.jpg");
    }

    /**
     * Set up the navigation
     * @param menu the menu item
     * @return the boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Set up the navigation
     * @return the boolean
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Creates listeners on the notification collection of each event
     * the user has signed up for
     */
    public void createNotificationListeners() {
        eventMap = new HashMap<>();
        eventIdToName = new HashMap<>();
        eventMapSignup = new HashMap<>();
        eventMapCheckin = new HashMap<>();
        notificationCollections = new HashSet<>();
        notidb = FirebaseFirestore.getInstance();
        mileStoneEvents = new ArrayList<>();
        milestoneListener = null;
        milestoneNames = new HashMap<>();
        milestoneLastUpdate = System.currentTimeMillis() + 1500;

        signupListener();
        checkinListener();
        milestoneListeners();
    }

    /**
     * creates listener for the signup table to display notifications for relevant events
     */
    public void signupListener() {
        notidb.collection("signUpTable").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {

                try {
                    eventMapSignup.clear();
                    // add each event the user has signed up for
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (doc.get("user_id").toString().equals(currentUser.getId()) ) {

                            // add event to list
                            String event = doc.get("event_id").toString();
                            eventMapSignup.put(event, Boolean.FALSE);
                        }
                    }
                    eventMap.clear();
                    eventMap.putAll(eventMapCheckin);
                    eventMap.putAll(eventMapSignup);

                    // set up timer to enable new notification sending.
                    notificationListenerLastUpdate = System.currentTimeMillis() + 1500;
                    // get names of events and set listeners
                    for (ListenerRegistration notiRef: notificationCollections) {
                        notiRef.remove();
                    }
                    Log.d("Notifications", "removed notification listeners");
                    notificationCollections.clear();
                    getNames();

                } catch (Exception e) {
                    Log.d("Notifications", e.toString());
                }
            }
        });
    }

    /**
     * creates listener for the checkin table to display notifications for relevant events
     */
    public void checkinListener() {
        notidb.collection("checkins").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {

                try {
                    eventMapCheckin.clear();
                    // add each event the user has signed up for
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        if (doc.get("user_id").toString().equals(currentUser.getId()) ) {

                            // add event to list
                            String event = doc.get("event_id").toString();
                            eventMapCheckin.put(event, Boolean.FALSE);
                        }
                    }
                    eventMap.clear();
                    eventMap.putAll(eventMapCheckin);
                    eventMap.putAll(eventMapSignup);

                    // set up timer to enable new notification sending.
                    notificationListenerLastUpdate = System.currentTimeMillis() + 1500;
                    // get names of events and set listeners

                    for (ListenerRegistration notiRef: notificationCollections) {
                        notiRef.remove();
                    }
                    Log.d("Notifications", "removed notification listeners");
                    notificationCollections.clear();
                    getNames();

                } catch (Exception e) {
                    Log.d("Notifications", e.toString());
                }
            }
        });
    }

    /**
     * creates a listener for hosted events to display milestone alerts
     */
    private void milestoneListeners() {
        notidb.collection("events").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore", error.toString());
                            return;
                        }

                        if (value != null) {

                            // prevent event edit from updating milestones
                            for (DocumentChange doc: value.getDocumentChanges()) {
                                if (mileStoneEvents.contains(doc.getDocument().getId())) {
                                    return;
                                }
                            }

                            // reset milestone listener and events
                            mileStoneEvents.clear();
                            if (milestoneListener != null) {
                                milestoneListener.remove();
                            }

                            // get all hosted events
                            for (QueryDocumentSnapshot doc: value) {
                                if (doc.getDocumentReference("host").getId().equals(currentUser.getId())) {
                                    mileStoneEvents.add(doc.getId());
                                    milestoneNames.put(doc.getId(), doc.getString("name"));
                                }
                            }
                            Log.d("Milestone", "creating milestone listener");
                            milestoneLastUpdate = System.currentTimeMillis() + 1000;
                            createMilestoneListener();
                        }
                    }
                });
    }

    /**
     * creates a listener for the checkins table that sends an alert to the user about an attendee milestone reached
     */
    private void createMilestoneListener() {
        milestoneListener = notidb.collection("checkins").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                HashMap<String, HashMap<String, Integer>> milestoneMap = new HashMap<>();

                // prevent initial alert send
                if (value != null &&
                        milestoneLastUpdate <= System.currentTimeMillis()) {

                    // go through all checkins
                    for (QueryDocumentSnapshot doc: value) {
                        String eventID = doc.getString("event_id");
                        // only check events hosted
                        if (mileStoneEvents.contains(eventID)) {
                            // check if event has been mapped yet
                            if (milestoneMap.containsKey(eventID)) {

                                HashMap<String, Integer> upSet = milestoneMap.get(doc.getString("event_id"));
                                String user = doc.getString("user_id");

                                // increment user checkin
                                if (upSet.containsKey(user)) {
                                    upSet.put(user, upSet.get(user) + 1);
                                    milestoneMap.put(eventID, upSet);

                                    // user not in event map yet
                                } else {
                                    upSet.put(user, 1);
                                    milestoneMap.put(eventID, upSet);
                                }
                                // event not in map yet
                            } else {
                                HashMap<String, Integer> upSet = new HashMap<>();
                                upSet.put(doc.getString("user_id"), 1);
                                milestoneMap.put(eventID, upSet);
                            }
                        }
                    }

                    // new document in checkins
                    for (DocumentChange doc: value.getDocumentChanges()) {
                        DocumentSnapshot newDoc = doc.getDocument();

                        HashMap<String, Integer> eventMap = milestoneMap.get(newDoc.getString("event_id"));

                        // if event change has duplicates then ignore the checkin since attendees are the same
                        if (eventMap != null && eventMap.get(newDoc.getString("user_id")) == 1) {
                            // goto milestone
                            switch (eventMap.size()) {
                                case 1:
                                    Log.d("Milestone", "1 person milestone reached");
                                    showNotification(
                                            "Milestone Reached!",
                                            milestoneNames.get(newDoc.getString("event_id")) + " just got its first attendee!",
                                            "Milestones",
                                            "Milestones");
                                    break;
                                case 5:
                                    Log.d("Milestone", "5 person milestone reached");
                                    showNotification(
                                            "Milestone Reached!",
                                            milestoneNames.get(newDoc.getString("event_id")) + " has reached 5 attendees!",
                                            "Milestones",
                                            "Milestones");
                                    break;
                                case 10:
                                    Log.d("Milestone", "10 person milestone reached");
                                    showNotification(
                                            "Milestone Reached!",
                                            milestoneNames.get(newDoc.getString("event_id")) + " has reached 10 attendees!",
                                            "Milestones",
                                            "Milestones");
                                    break;
                                case 100:
                                    Log.d("Milestone", "100 person milestone reached");
                                    showNotification(
                                            "Milestone Reached!",
                                            milestoneNames.get(newDoc.getString("event_id")) + " has reached 100 attendees! WOW!",
                                            "Milestones",
                                            "Milestones");
                                    break;
                                default:
                                    return;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * getNames will get the name of all events specified in the list of event IDs.
     * This will also set a listener to each events notification collection
     */
    private void getNames() {
        // for each signed up event
        Log.d("Notifications", "Amount of listeners: " + eventMap.size());

        for (String event: eventMap.keySet()) {
            notidb.collection("events").document(event).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    eventIdToName.put(event, documentSnapshot.getString("name"));
                    Log.d("Firestore", "successful event name get");

                    // next step in listener setup
                    setListener(event);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Firestore", "unsuccessful event name get");
                }
            });
        }
    }

    /**
     * setListener will set a listener to the specified events notification collection.
     * This listener will send out a notification when a new notification is detected
     *
     * @param eventID ID of the event who's notification collection will be listened to
     */
    private void setListener(String eventID) {
        if (eventMap.get(eventID) == null) {
            return;
        }

        if (!eventMap.get(eventID).booleanValue()) {
            eventMap.put(eventID, Boolean.TRUE);
            Log.d("Notifications", "creating listener for event: " + eventIdToName.get(eventID));

            // create new collection reference for notifications
            CollectionReference notificationRef = notidb
                    .collection("events")
                    .document(eventID)
                    .collection("notifications");

            // add listener to new collection
            ListenerRegistration listenerRef = notificationRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("Firestore", error.toString());
                        return;
                    }

                    if (querySnapshots != null
                            && notificationListenerLastUpdate <= System.currentTimeMillis()) {
                        for (DocumentChange document : querySnapshots.getDocumentChanges()) {
                            DocumentSnapshot doc = document.getDocument();
                            // push notification
                            showNotification( "'" + eventIdToName.get(eventID) + "'" + " Sent you a notification!", doc.getString("message"), "qrchannel", "Event Notifications");
                        }
                        Log.d("Notifications", "received new push notification");
                    }
                }
            });
            notificationCollections.add(listenerRef);
        }
    }

    /**
     * showNotification will send a push notification to the device
     *
     * Reference:
     * https://www.geeksforgeeks.org/how-to-push-notification-in-android-using-firebase-cloud-messaging/
     * from geeksforgeeks
     *
     * @param title title of the notification message
     * @param message the body of the notification message
     */
    public void showNotification(String title, String message, String channel_id, String channel_name) {
        Log.d("Notifications", "creating notification for: " + title);
        // Pass the intent to switch to the MainActivity
//        Intent intent = new Intent(this, MainActivity.class);

//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        // Pass the intent to PendingIntent to start the
//        // next Activity
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext(), channel_id)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.nav_events)
                // .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(message);


        NotificationManager notificationManager
                = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(
                channel_id, channel_name,
                NotificationManager.IMPORTANCE_HIGH);

        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(0, builder.build());
    }

}