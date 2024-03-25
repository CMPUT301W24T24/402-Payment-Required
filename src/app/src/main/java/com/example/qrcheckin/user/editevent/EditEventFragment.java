package com.example.qrcheckin.user.editevent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.QRCodeGenerator;
import com.example.qrcheckin.databinding.FragmentEditEventBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.BuildConfig;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EditEventFragment extends Fragment implements LocationListener {
    private FragmentEditEventBinding binding;
    private MapView map;
    private LocationManager locationManager;
    final private double MAP_DEFAULT_LATITUDE=53.5265, MAP_DEFAULT_LONGITUDE=-113.5255;

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Event event = getArguments().getSerializable("event") != null ? (Event) getArguments().getSerializable("event") : null;
        ImageView eventPoster = binding.editEventProfile;
        EditText editEventTitle = binding.editEventTitle;
        TextView editEventDate = binding.editEventDate;
        TextView editEventTime = binding.editEventTime;
        Button changeTimeButton = binding.editEventChangeTime;
        TextView editEventAttendLimit = binding.editEventAttendLimit;
        TextView editEventDescription = binding.editEventDescription;
        Button showCheckIns = binding.editEventShowCheckIns;
        Button showSignUps = binding.editEventShowSignUps;
        Button notify = binding.editEventNotifyAttendees;
        Button exportQREventCode = binding.editEventExportEventCode;
        ImageView checkInCode = binding.editEventCheckInCode;
        Button exportCheckCode = binding.editEventExportEventCode;
        FloatingActionButton editEventUpdate = binding.editEventUpdate;

        //map permissions
        requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE});

        //important for OSM moderation apparently
        Configuration.getInstance().setUserAgentValue(BuildConfig.LIBRARY_PACKAGE_NAME);

        //map config
        map = root.findViewById(R.id.osmmap);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.getController().setZoom(16);

        //location manager and setting this up as a location listener
        locationManager = (LocationManager) root.getContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        for (String provider:locationManager.getProviders(true)){
            locationManager.requestLocationUpdates(provider, 1000, 0, this);
        }

        //on init if loc reads null simply default to uofa
        if (location == null){
            location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(MAP_DEFAULT_LATITUDE);
            location.setLongitude(MAP_DEFAULT_LONGITUDE);
            map.getController().animateTo(new GeoPoint(location));
        }



        // Set event information
        assert event != null;
        editEventTitle.setText(event.getName());
        editEventDescription.setText(event.getDescription());
        editEventAttendLimit.setText(String.valueOf(event.getLimit()));
        //set poster
        eventPoster.setImageResource(R.drawable.cat);

        //Accessing the events database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        DocumentReference docRef = eventsRef.document(event.getId());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //set up time and date
                    Log.d("Firestore", "Event fetched");
                    Timestamp timestamp = documentSnapshot.getTimestamp("time");
                    if (timestamp != null) {
                        // Convert the timestamp to a Date object
                        Date date = timestamp.toDate();
                        // Create a SimpleDateFormat instance for your desired format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy"); // Example: "March 8, 2024"
                        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a"); // Example: "8:10 AM"
                        // Extract the formatted date and time strings
                        String formattedDate = dateFormat.format(date);
                        String formattedTime = timeFormat.format(date);
                        // Get the separate date and time components
                        Log.d("Formatted Date", formattedDate);
                        Log.d("Formatted Time", formattedTime);
                        editEventDate.setText(String.valueOf(formattedDate));
                        editEventTime.setText(String.valueOf(formattedTime));

                        //get CheckInCode
                        String check_inId = documentSnapshot.getString("checkin_id");
                        checkInCode.setImageBitmap(QRCodeGenerator.generateQRCode(check_inId, 800, 800));
                        checkInCode.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        //see a list of attendees
        showSignUps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", (Serializable) event);
                //TODO: create an id to navigate from edit to see_signUps
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_event_to_nav_view_event, bundle);
            }
        });

        //notify atendees
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("eventId", event.getId());
                (new EditEventFragment()).setArguments(args);
                //TODO: navigate to the notify -> link in the navigation bar
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_edit_event_to_nav_create_notification, args);
            }
        });

        //set OnClickListener for editInformation
        editEventUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (editEventTitle.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter event title", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editEventDescription.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter event description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editEventAttendLimit.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please enter event limit", Toast.LENGTH_SHORT).show();
                    return;
                }
                event.setName(String.valueOf(editEventTitle.getText()));
                event.setDescription(String.valueOf(editEventDescription.getText()));
                event.setLimit(Integer.valueOf(String.valueOf(editEventAttendLimit.getText())));
                //access the firebase and databse and update
                HashMap<String, Object> data = new HashMap<>();
                data.put("description", event.getDescription());
                data.put("limit", event.getLimit());
                data.put("name", event.getName());
                docRef.update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("firestore", "update the event name, limit, and description sucessfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firestore", e.toString());
                            }
                        });
            }
        });


        // Set OnClickListener for changeTime
        changeTimeButton.setOnClickListener(new View.OnClickListener() {
            String holderTime;
            @Override
            public void onClick(View v) {
                // Fetch timestamp from the database
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve timestamp from Firestore document
                            Timestamp timestamp = documentSnapshot.getTimestamp("time");
                            if (timestamp != null) {
                                // Convert timestamp to Calendar object
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(timestamp.toDate().getTime());
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH);
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);
                                openDateDialog(year, month, day);
                                openTimeDialog(hour, minute);
                            }
                        }
                    }
                });
            }
            private void openTimeDialog(int hour, int minute) {
                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        // Create a SimpleDateFormat instance for desired format
                        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                        // Format the selected time
                        String formattedTime = timeFormat.format(calendar.getTime());
                        // Set the formatted time to the TextView or do something with it
                        editEventTime.setText(formattedTime);
                    }
                }, hour, minute, false);
                // Show the TimePickerDialog
                dialog.show();
            }

            private void openDateDialog(int year, int month, int day) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        // Create a SimpleDateFormat instance for desired format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
                        // Format the selected date
                        String formattedDate = dateFormat.format(calendar.getTime());
                        // Set the formatted date to the TextView or do something with it
                        editEventDate.setText(formattedDate);

                        // After setting both date and time, update the Firestore document
                        updateTimeFireStore(calendar);
                    }
                }, year, month, day);
                // Show the DatePickerDialog
                dialog.show();
            }

            private void updateTimeFireStore(Calendar calendar) {
                // Combine date and time into a single timestamp
                Date selectedDate = calendar.getTime();
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMMM d, yyyy h:mm a");
                String combinedDateTime = editEventDate.getText().toString() + " " + editEventTime.getText().toString();
                try {
                    Date combinedDate = dateTimeFormat.parse(combinedDateTime);
                    Timestamp combinedTimestamp = new Timestamp(combinedDate);

                    // Update Firestore document
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("time", combinedTimestamp);
                    docRef.update(data)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("Firestore", "Updated time: " + docRef.getId());
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", e.toString());
                            });
                } catch (ParseException e) {
                    Log.e("TAG", "Can not parse the time stamp");;
                }
            }
        });

        return root;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        map.getController().setCenter(new GeoPoint(location));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {//needed for OSM display
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {//needed for OSM display
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {//needed for OSM permissions
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {// array to arraylist
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this.getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    1);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {//needed for OSM permissions
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this.getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this.getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    1);
        }
    }

}
