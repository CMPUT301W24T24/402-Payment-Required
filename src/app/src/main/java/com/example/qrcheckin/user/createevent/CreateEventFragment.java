package com.example.qrcheckin.user.createevent;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.QRCodeGenerator;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentCreateEventBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.BuildConfig;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor;

/**
 * Create Event Fragment is a class that creates a CreateEventFragment object
 */
public class CreateEventFragment extends Fragment {
    private FragmentCreateEventBinding binding;
    public String checkinId;
    public String promoteId;

    //mapping
    final private double MAP_DEFAULT_LATITUDE=53.5265, MAP_DEFAULT_LONGITUDE=-113.5255;
    private Location eventLocation;
    private MapView map;
    private Marker selectedMarker;

    /**
     * Initializes the CreateEventFragment on create
     * Reference: https://stackoverflow.com/questions/6421874/how-to-get-the-date-from-the-datepicker-widget-in-android Berllium Accessed: March 8 2024
     * @param inflater: the inflater used to create the binding
     * @param container: the ViewGroup used to create the binding
     * @param savedInstanceState: the Bundle used to pass information
     * @return root
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CreateEventViewModel createEventViewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);

        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView bannerImageView = binding.imageCreateEventBanner;

        EditText titleTextView = binding.textCreateEventTitle;
        createEventViewModel.getEventTitle().observe(getViewLifecycleOwner(), titleTextView::setHint);

        DatePicker dateView = binding.dateCreateEvent;

        TimePicker timeView = binding.timeCreateEvent;

        EditText descriptionTextView = binding.textCreateEventDescription;
        createEventViewModel.getEventDescription().observe(getViewLifecycleOwner(), descriptionTextView::setHint);

        EditText locationTextView = binding.textCreateEventLocation;

        EditText limitNumberPicker = binding.createEventAttendLimit;
        // limitNumberPicker.setMinValue(0);
        // limitNumberPicker.setMaxValue(1000000000);

        CheckBox geoCheckBox = binding.checkboxCreateEventGeolocation;

        ImageView qrCodeCheckinImageView = binding.imageviewCreateEventCheckinQr;
        ImageView qrCodePromoteImageView = binding.imageviewCreateEventDescriptionQr;

        Button generateQRCheckinButton = binding.buttonCreateEventGenerateQrCheckin;
        generateQRCheckinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkinId = generateQRCode();
                qrCodeCheckinImageView.setImageBitmap(QRCodeGenerator.generateQRCode(checkinId, 800, 800));
                qrCodeCheckinImageView.setVisibility(View.VISIBLE);
            }
        });

        Button generateQRPromoteButton = binding.buttonCreateEventGenerateQrDescription;
        generateQRPromoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promoteId = generateQRCode();
                qrCodePromoteImageView.setImageBitmap(QRCodeGenerator.generateQRCode(promoteId, 800, 800));
                qrCodePromoteImageView.setVisibility(View.VISIBLE);
            }
        });

        //map permissions
        requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE});

        //important for OSM moderation apparently
        Configuration.getInstance().setUserAgentValue(BuildConfig.LIBRARY_PACKAGE_NAME);

        //map config
        map=root.findViewById(R.id.osmmap);
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        map.setMultiTouchControls(true);
        map.getController().setZoom(16);

        eventLocation=new Location(LocationManager.GPS_PROVIDER);
        //event location
        if(eventLocation.getLatitude()==0&&eventLocation.getLongitude()==0) {//otherwise if it fails to retreive gps use hardcoded defaults
            eventLocation.setLatitude(MAP_DEFAULT_LATITUDE);
            eventLocation.setLongitude(MAP_DEFAULT_LONGITUDE);
        }
        //marker
        selectedMarker=new Marker(map);
        selectedMarker.setPosition(new GeoPoint(eventLocation));
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        map.getOverlays().add(selectedMarker);
        map.invalidate(); //refresh
        map.getController().animateTo(new GeoPoint(eventLocation));

        binding.selectNewLocationButton.setOnClickListener(v -> {
            selectLocation();
        });


        binding.buttonCreateEventSubmit.setOnClickListener(v -> {
            if (titleTextView.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please enter a Title", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkinId == null) {
                checkinId = generateQRCode();
            }
            if (promoteId == null) {
                promoteId = generateQRCode();
            }



            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference eventsRef = db.collection("events");
            CollectionReference usersRef = db.collection("users");

            User user = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser();
            String titleText = titleTextView.getText().toString();
            String descriptionText = descriptionTextView.getText().toString();
            // TODO: get the poster reference
            String posterRef = "";
            int year = dateView.getYear();
            int month = dateView.getMonth();
            int date = dateView.getDayOfMonth();
            int hours = timeView.getHour();
            int min = timeView.getMinute();
            Date time = new Date(year-1900, month, date, hours, min);
            String location = locationTextView.getText().toString();
            String checkinQR = null;
            String promoteQR = null;
            Double locationGeoLat = eventLocation.getLatitude();
            Double locationGeoLong = eventLocation.getLongitude();
            Boolean geo = geoCheckBox.isChecked();

            // set limit
            Integer limit = 0;
            Integer inputLimitLength = limitNumberPicker.getText().length();
            if (inputLimitLength != 0){
                limit = Integer.parseInt(limitNumberPicker.getText().toString());
            }

            Log.d("testlol", "limit= " + limit);


            Event event = new Event(user, titleText, descriptionText, posterRef, time, location, locationGeoLat, locationGeoLong, checkinId, checkinQR, promoteId, promoteQR, geo, limit);

            HashMap<String, Object> data = new HashMap<>();
            data.put("name", event.getName());
            data.put("description", event.getDescription());
            data.put("time", event.getTime());
            data.put("posterRef", event.getPosterRef());
            data.put("location_geo_long", event.getLocationGeoLong());
            data.put("location_geo_lat", event.getLocationGeoLat());
            data.put("limit",event.getLimit());
            data.put("host", (DocumentReference) usersRef.document(user.getId()));
            data.put("geo", event.getGeo());
            data.put("promote_id", event.getPromoteId());
            data.put("location", event.getLocation());
            data.put("checkin_id", event.getCheckinId());

            eventsRef.add(data)
                    .addOnSuccessListener(documentReference -> {
                        event.setId(documentReference.getId());
                        Log.d("Firestore", "Created an event with ID: " + documentReference.getId());
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", e.toString());

                    });


        });

        return root;
    }

    /**
     * This function generates a random alphanumeric string of length n
     * Reference: https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/ Rajput-Ji Accessed March 8 2024
     * Reference: https://chat.openai.com/c/12e422eb-0289-44e5-ba5e-5b87443c3946 ChatGPT Accessed April 4, 2024
     * @param n - the length of the alphanumeric string
     * @return - the alphanumeric string
     */
    public String getAlphaNumericString(int n) {
        String QRstring;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");

        ArrayList<String> existingIds = new ArrayList<String>();

        // Fetch all existing promote_ids and checkin_ids in the fireStore database
        eventsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String promoID = document.getString("promote_id");
                    String checkinID = document.getString("checkin_id");
                    if (promoID != null) existingIds.add(promoID);
                    if (checkinID != null) existingIds.add(checkinID);
                }
            }
        });

        // Generate a new alphanumeric string until it's unique
        while (true) {
            // Choose a Character random from this String
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
            // Create StringBuilder size of AlphaNumericString
            StringBuilder sb = new StringBuilder(n);

            // Generate a random alphanumeric string of length n
            for (int i = 0; i < n; i++) {
                int index = (int) (AlphaNumericString.length() * Math.random());
                sb.append(AlphaNumericString.charAt(index));
            }

            QRstring = sb.toString();

            // If the generated string doesn't exist in the existing IDs set, break the loop
            if (!existingIds.contains(QRstring)) {
                break;
            }
        }

        return QRstring;
    }



        /**
         * This generates a checkin QR code
         * @return - the QR code string data
         */
    public String generateQRCode() {
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
        String randomString = null;
        //Check that the checkinID will not be the same as the promoteID
        while (true) {
            randomString = getAlphaNumericString(20);
            if (randomString != null && (!randomString.equals(checkinId)) && (!randomString.equals(promoteId))) {
                break;
            }
        }
        return QRCodeGenerator.getQRCodeData(QRCodeGenerator.generateQRCode(randomString, 400, 400));
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
            if (ContextCompat.checkSelfPermission(this.getActivity(), permission)!= PackageManager.PERMISSION_GRANTED)
                permissionsToRequest.add(permission);

        if (permissionsToRequest.size() > 0)
            ActivityCompat.requestPermissions(this.getActivity(),permissionsToRequest.toArray(new String[0]),1);
    }

    private void selectLocation() {
        Toast.makeText(getContext(), "Tap a location on the map", Toast.LENGTH_SHORT).show();
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                eventLocation.setLatitude(p.getLatitude());
                eventLocation.setLongitude(p.getLongitude());

                //marker
                if(selectedMarker!=null)
                    map.getOverlays().remove(selectedMarker);
                selectedMarker=new Marker(map);
                selectedMarker.setPosition(p);
                selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                map.getOverlays().add(selectedMarker);
                map.invalidate(); //refresh
                map.getController().animateTo(p);

                Toast.makeText(getContext(), "Successfully set location!", Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        map.getOverlays().add(new MapEventsOverlay(mReceive));
    }

}
