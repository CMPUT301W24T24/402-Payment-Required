// https://stackoverflow.com/questions/6421874/how-to-get-the-date-from-the-datepicker-widget-in-android
// used to get the date from datepicker
package com.example.qrcheckin.user.createevent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentCreateEventBinding;
import com.google.android.gms.maps.MapView;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        createEventViewModel.getEventTitle().observe(getViewLifecycleOwner(), titleTextView::setText);

        DatePicker dateView = binding.dateCreateEvent;

        TimePicker timeView = binding.timeCreateEvent;

        EditText descriptionTextView = binding.textCreateEventDescription;
        createEventViewModel.getEventDescription().observe(getViewLifecycleOwner(), descriptionTextView::setText);

        EditText locationTextView = binding.textCreateEventLocation;


        binding.buttonCreateEventSubmit.setOnClickListener(v -> {
            // TODO: if everything is filled out add to firebase
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference eventsRef = db.collection("events");

            User user = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser();
            String titleText = titleTextView.getText().toString();
            String descriptionText = descriptionTextView.getText().toString();
            // TODO: get the poster reference
            String posterRef = "poster.png";

            int date = dateView.getDayOfMonth();
            int month = dateView.getMonth();
            int year = dateView.getYear();
            int hours = timeView.getHour();
            int min = timeView.getMinute();
            Date time = new Date(year, month, date, hours, min);
            String location = locationTextView.getText().toString();
            Double locationGeoLat = null;
            Double locationGeoLong = null;
            String checkinId = null;
            String checkinQR = null;
            String promoteId = null;
            String promoteQR = null;
            Boolean geo = null;
            Integer limit = null;


            Event event = new Event(user, titleText, descriptionText, posterRef, time, location, locationGeoLat, locationGeoLong, checkinId, checkinQR, promoteId, promoteQR, geo, limit);

            HashMap<String, Object> data = new HashMap<>();
            data.put("name", event.getName());
            data.put("description", event.getDescription());
            data.put("time", event.getTime());
            data.put("posterRef", event.getPosterRef());
            data.put("location_geo_long", event.getLocationGeoLong());
            data.put("location_geo_lat", event.getLocationGeoLat());
            data.put("limit",event.getLimit());
            data.put("host", event.getHost());
            data.put("geo", event.getGeo());
            data.put("promote_id", event.getPromoteId());
            data.put("promote_qr", event.getPromoteRq());
            data.put("location", event.getLocation());
            data.put("checkin_id", event.getCheckinId());
            data.put("checkin_qr", event.getCheckinRq());

            eventsRef.add(data)
                    .addOnSuccessListener(documentReference -> {
                        event.setId(documentReference.getId());
                        Log.d("Firestore", "Created an event with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firesotre", e.toString());
                    });


            // TODO: go back to the previous fragment
        });

        // TODO: copy format of add user


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
