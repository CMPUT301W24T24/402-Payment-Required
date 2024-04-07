package com.example.qrcheckin.user.editevent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.qrcheckin.MainActivity;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.QRCodeGenerator;
import com.example.qrcheckin.databinding.FragmentEditEventBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.example.qrcheckin.user.attendeessignedup.AttendeesSignedUpFragment;
import com.example.qrcheckin.user.createevent.CreateEventViewModel;
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
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditEventFragment extends Fragment {
    private FragmentEditEventBinding binding;
    private MapView map;
    final private double MAP_DEFAULT_LATITUDE=53.5265, MAP_DEFAULT_LONGITUDE=-113.5255;
    private Location eventLocation;
    private DocumentReference docRef;
    private Marker selectedMarker;
    private Drawable personCircle;

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
        ImageView promoCode = binding.editEventPromoCode;

        //map permissions
        requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE});

        //important for OSM moderation apparently
        Configuration.getInstance().setUserAgentValue(BuildConfig.LIBRARY_PACKAGE_NAME);

        //map config
        map = root.findViewById(R.id.osmmap);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setMultiTouchControls(true);
        map.getController().setZoom(16);

        eventLocation=new Location(LocationManager.GPS_PROVIDER);
        //event location
        if(event.getLocationGeoLat()!=null&&event.getLocationGeoLong()!=null) {//first try to pull event location if it isnt null
            eventLocation.setLatitude(event.getLocationGeoLat());
            eventLocation.setLongitude(event.getLocationGeoLong());
        }
        if(eventLocation.getLatitude()==0&&eventLocation.getLongitude()==0) {//otherwise if it fails to retreive gps use hardcoded defaults
            eventLocation.setLatitude(MAP_DEFAULT_LATITUDE);
            eventLocation.setLongitude(MAP_DEFAULT_LONGITUDE);
        }
        //marker
        personCircle=DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.person_circle));
        DrawableCompat.setTint(personCircle, ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
        Database.fetchGeoPointsForEvent(event.getId(), new Database.OnGeoPointsFetchedListener() {
            @Override
            public void onGeoPointsFetched(List<GeoPoint> geoPoints) {
                for(GeoPoint geoPoint : geoPoints){
                    Marker newMarker=new Marker(map);
                    newMarker.setPosition(geoPoint);
                    newMarker.setIcon(personCircle);
                    newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    map.getOverlays().add(newMarker);
                }
            }
            @Override
            public void onGeoPointsFetchFailed(Exception e) {
                Log.e("CheckinDots", "Error fetching GeoPoints " + e.getMessage());
            }
        });

        selectedMarker=new Marker(map);
        selectedMarker.setPosition(new GeoPoint(eventLocation));
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(selectedMarker);

        map.invalidate(); //refresh
        map.getController().animateTo(new GeoPoint(eventLocation));

        binding.selectLocationButton.setOnClickListener(v -> {
            selectLocation();
        });

        // Set event information
        assert event != null;
        editEventTitle.setText(event.getName());
        editEventDescription.setText(event.getDescription());

        // set event attendee limit
        Integer eventLimit = event.getLimit();
        String eventLimitText;
        if (eventLimit == 0) {
            eventLimitText = "unlimited";
            editEventAttendLimit.setHint(eventLimitText);
        } else {
            eventLimitText = eventLimit.toString();
            editEventAttendLimit.setText(eventLimitText);
        }

        //set poster
        eventPoster.setImageResource(R.drawable.cat);

        //Accessing the events database
        docRef=FirebaseFirestore.getInstance().collection("events").document(event.getId());

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
                        // Create a SimpleDateFormat instance for format
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
                        //get PromoCode
                        String promoID = documentSnapshot.getString("promote_id");
                        promoCode.setImageBitmap(QRCodeGenerator.generateQRCode(promoID, 800, 800));
                        promoCode.setVisibility(View.VISIBLE);
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
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_edit_event_to_nav_attendee_sign_up, bundle);
            }
        });

        //see a list of attendees
        showCheckIns.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("event", (Serializable) event);
                Navigation.findNavController(requireView()).navigate(R.id.action_nav_edit_event_to_nav_attendee_check_in, bundle);
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
                    event.setLimit(0);
                } else {
                    event.setLimit(Integer.valueOf(String.valueOf(editEventAttendLimit.getText())));
                }
                event.setName(String.valueOf(editEventTitle.getText()));
                event.setDescription(String.valueOf(editEventDescription.getText()));
                //access the firebase and database and update
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {//needed for OSM permissions
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++)// array to arraylist
            permissionsToRequest.add(permissions[i]);

        if (permissionsToRequest.size() > 0)
            ActivityCompat.requestPermissions(this.getActivity(),permissionsToRequest.toArray(new String[0]),1);
    }

    private void requestPermissionsIfNecessary(String[] permissions) {//needed for location permissions
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(this.getActivity(), permission)!=PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(permission);

        if (permissionsToRequest.size() > 0)
            ActivityCompat.requestPermissions(this.getActivity(),permissionsToRequest.toArray(new String[0]),1);
    }

    private void selectLocation() {
        Toast.makeText(getContext(), "Tap a location on the map", Toast.LENGTH_SHORT).show();
        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                eventLocation.setLatitude(p.getLatitude());
                eventLocation.setLongitude(p.getLongitude());

                //marker
                if(selectedMarker!=null)
                    map.getOverlays().remove(selectedMarker);
                selectedMarker=new Marker(map);
                selectedMarker.setPosition(p);
                selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(selectedMarker);
                map.invalidate(); //refresh
                map.getController().animateTo(p);

                HashMap<String, Object> data = new HashMap<>();
                data.put("location_geo_lat", p.getLatitude());
                data.put("location_geo_long", p.getLongitude());

                docRef.update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("firestore", "update the lat and lon sucessfully");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firestore", e.toString());
                            }
                        });
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        map.getOverlays().add(new MapEventsOverlay(receiver));
    }

}
