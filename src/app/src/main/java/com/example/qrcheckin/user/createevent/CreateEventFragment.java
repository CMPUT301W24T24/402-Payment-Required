// https://stackoverflow.com/questions/6421874/how-to-get-the-date-from-the-datepicker-widget-in-android
// used to get the date from datepicker
package com.example.qrcheckin.user.createevent;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.QRCodeGenerator;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentCreateEventBinding;
import com.google.android.gms.maps.MapView;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class CreateEventFragment extends Fragment {
    private FragmentCreateEventBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CreateEventViewModel createEventViewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);

        binding = FragmentCreateEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView bannerImageView = binding.imageCreateEventBanner;
        // TODO: set the image reference
        //createEventViewModel.getBannerRef().observe(getViewLifecycleOwner(), bannerImageView::);

        EditText titleTextView = binding.textCreateEventTitle;
        createEventViewModel.getEventTitle().observe(getViewLifecycleOwner(), titleTextView::setHint);

        DatePicker dateView = binding.dateCreateEvent;

        TimePicker timeView = binding.timeCreateEvent;

        EditText descriptionTextView = binding.textCreateEventDescription;
        createEventViewModel.getEventDescription().observe(getViewLifecycleOwner(), descriptionTextView::setHint);

        EditText locationTextView = binding.textCreateEventLocation;

        NumberPicker limitNumberPicker = binding.createEventAttendLimit;
        limitNumberPicker.setMinValue(0);
        limitNumberPicker.setMaxValue(1000000000);

        CheckBox geoCheckBox = binding.checkboxCreateEventGeolocation;

        /*String checkinId = null;
        String promoteId = null;
        Button generateQRCheckinButton = binding.buttonCreateEventGenerateQrCheckin;
        generateQRCheckinButton.setOnClickListener(v -> {
            QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
            checkinId = QRCodeGenerator.getQRCodeData(QRCodeGenerator.generateQRCode(location, 100, 100));
        });*/


        binding.buttonCreateEventSubmit.setOnClickListener(v -> {
            if (locationTextView.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please enter a location", Toast.LENGTH_SHORT).show();
                return;
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
            Date time = new Date(year, month, date, hours, min);
            String location = locationTextView.getText().toString();
            Double locationGeoLat = null;
            Double locationGeoLong = null;
            QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
            String checkinId = QRCodeGenerator.getQRCodeData(QRCodeGenerator.generateQRCode(location, 100, 100));
            String checkinQR = null;
            String promoteId = QRCodeGenerator.getQRCodeData(QRCodeGenerator.generateQRCode(location, 100, 100));
            String promoteQR = null;
            Boolean geo = geoCheckBox.isChecked();
            Integer limit = limitNumberPicker.getValue();


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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
