package com.example.qrcheckin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Notification;
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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Database.UserListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private User currentUser;
    private Map<String, String> eventIdToName;
    private long notificationListenerLastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

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

        getDeviceUser(this);

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
        Database db = new Database();
        db.getUserPicture(currentUser, navProfileImage);

        // set the notification listeners for event user is signed up for
        createNotificationListeners();
        if (!currentUser.isAdmin()) {
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_all_event).setVisible(false);
            menu.findItem(R.id.nav_all_images).setVisible(false);
            menu.findItem(R.id.nav_all_profile).setVisible(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     *
     */
    public void createNotificationListeners() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<String> eventList = new ArrayList<>();
        eventIdToName = new HashMap<>();
        db.collection("signUpTable").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {

                try {
                    Log.d("Notifications", "creating notification listeners. Documents in SignUpTable: " + querySnapshots.size());
                    for (QueryDocumentSnapshot doc: querySnapshots) {

                        if (doc.get("user_id").toString().equals(currentUser.getId()) ) {
                            String event = doc.get("event_id").toString();
                            Log.d("Notifications", "creating listener for event: " + event);
                            eventList.add(event);
                        }
                    }

                    notificationListenerLastUpdate = System.currentTimeMillis() + 1250;

                    getNames(db, eventList);

                }  catch (NullPointerException e) {
                    Log.e("Notifications", e.toString());
                }

            }
        });
    }

    /**
     *
     * @param db
     * @param eventList
     */
    private void getNames(FirebaseFirestore db, ArrayList<String> eventList) {
        // for each signed up event

        for (String event: eventList) {
            db.collection("events").document(event).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    eventIdToName.put(event, documentSnapshot.getString("name"));
                    Log.d("Firestore", "successful event name get");

                    // next step in listener setup
                    setListeners(db, event);
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
     *
     * @param db
     * @param eventID
     */
    private void setListeners(FirebaseFirestore db, String eventID) {
        // add listeners to notification collections of events

        // create new collection reference and put into fireEventsNotifs
        CollectionReference notificationRef = db
                .collection("events")
                .document(eventID)
                .collection("notifications");

        // add listener to new collection
        notificationRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }

                Log.d("Notifications", "ListenerLastUpdate: " + notificationListenerLastUpdate + " Current Time: " + System.currentTimeMillis());

                if (querySnapshots != null
                        && notificationListenerLastUpdate <= System.currentTimeMillis()) {
                    for (DocumentChange document: querySnapshots.getDocumentChanges() ) {
                        DocumentSnapshot doc = document.getDocument();
                        // push notification
                        showNotification(eventIdToName.get(eventID) , doc.getString("message"));
                    }
                    Log.d("Notifications", "received new push notification");
                }
            }
        });
    }

    /**
     *
     * @param title
     * @param message
     */
    // https://www.geeksforgeeks.org/how-to-push-notification-in-android-using-firebase-cloud-messaging/
    public void showNotification(String title, String message) {
        title = "'" + title + "'" + " Sent you a notification!";

        Log.d("Notifications", "creating notification for event: " + title);
        // Pass the intent to switch to the MainActivity
//        Intent intent = new Intent(this, MainActivity.class);

        // Assign channel ID
        String channel_id = "qrchannel";

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
                channel_id, "Event Notifications",
                NotificationManager.IMPORTANCE_HIGH);

        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(0, builder.build());
    }
}