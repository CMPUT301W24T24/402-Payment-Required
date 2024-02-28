package com.example.qrcheckin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Database.UserListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_event, R.id.nav_host_event, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
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
     * Get the user from the database, interface method
     * @param user the user
     */
    @Override
    public void onUserFetched(User user) {
        currentUser = user;
        Log.d("Firestore", "User fetched: " + user.getDataString());

        // set the user to the application
        ((QRCheckInApplication) getApplication()).setCurrentUser(currentUser);

        // set the user name to the navigation view
        // TODO: set the user picture to the navigation view
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView navProfileName = headerView.findViewById(R.id.nav_profile_name);
        navProfileName.setText(currentUser.getName());
    }

    /**
     * After added the user to the database, set id, interface method
     * @param id the id of the user
     */
    @Override
    public void onUserAdded(String id) {
        currentUser.setId(id);
        writeIdToFile(id);
    }

    /**
     * Create a default user
     * @param id the id of the user
     * @return the user
     */
    private User createDefaultUser(String id) {
        return new User(id, "Anonymous User", "", "", "", false, false);
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
}