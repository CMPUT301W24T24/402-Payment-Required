package com.example.qrcheckin.user.createevent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.QRCodeGenerator;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentCreateEventBinding;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.type.DateTime;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.grpc.Context;

/**
 * Create Event Fragment is a class that creates a CreateEventFragment object
 */
public class CreateEventFragment extends Fragment {
    private FragmentCreateEventBinding binding;
    public String checkinId;
    public String promoteId;

    private boolean imageUpdated = false;
    private Uri updatedImageUri;
    private boolean imageTooLarge = false;

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
        Button uploadEventBanner = binding.uploadEventBanner;

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

        ImageView qrCodeCheckinImageView = binding.imageviewCreateEventCheckinQr;
        ImageView qrCodePromoteImageView = binding.imageviewCreateEventDescriptionQr;

        Button generateQRCheckinButton = binding.buttonCreateEventGenerateQrCheckin;

        // Set up the photo picker
        // Reference: https://developer.android.com/training/data-storage/shared/photopicker Accessed on 2024-03-31
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        InputStream imageStream = null;
                        try {
                            imageStream = requireContext().getContentResolver().openInputStream(uri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        // decode the image stream into a bitmap
                        Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                        if (bmp.getHeight() > 2048) {
                            bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() * 2048 / bmp.getHeight(), 2048, false);
                            imageTooLarge = true;
                        } else if (bmp.getWidth() > 2048) {
                            bmp = Bitmap.createScaledBitmap(bmp, 2048, bmp.getHeight() * 2048 / bmp.getWidth(), false);
                            imageTooLarge = true;
                        }

                        // Convert the bitmap to a URI
                        // Reference: https://stackoverflow.com/questions/8295773/how-can-i-transform-a-bitmap-into-a-uri Ajay. DragonFire. Accessed on 2024-03-31
                        if (imageTooLarge) {
                            Toast.makeText(getContext(), "Image too large, scaled down to 2048x2048", Toast.LENGTH_SHORT).show();
                            String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bmp, "tempImg", null);
                            uri = Uri.parse(path);
                        }
                        imageUpdated = true;
                        binding.imageCreateEventBanner.setImageURI(uri);
                        updatedImageUri = uri;
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

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

        binding.uploadEventBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        binding.buttonCreateEventSubmit.setOnClickListener(v -> {
            // TODO: check that checkinId and promoteID are not the same
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

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
            String posterRef = "events/" + now.format(formatter);

            int year = dateView.getYear();
            int month = dateView.getMonth();
            int date = dateView.getDayOfMonth();
            int hours = timeView.getHour();
            int min = timeView.getMinute();
            Date time = new Date(year-1900, month, date, hours, min);
            String location = locationTextView.getText().toString();
            String checkinQR = null;
            String promoteQR = null;
            Double locationGeoLat = null;
            Double locationGeoLong = null;
            Boolean geo = geoCheckBox.isChecked();
            Integer limit = limitNumberPicker.getValue();

            if (imageUpdated) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                Uri imageUri = updatedImageUri;
                StorageReference imageRef = storageRef.child(posterRef);

                imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Firebase", "Event poster uploaded successfully");


                        // Delete the image if it was scaled down
                        // Reference: https://stackoverflow.com/questions/23716683/android-delete-file-after-images-media-insertimage Nate. Accessed on 2024-03-31
                        if (imageTooLarge) {
                            requireContext().getContentResolver().delete(updatedImageUri, null, null);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Event poster upload failed", e);
                    }
                });
            }

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
     * @param n - the length of the alphanumeric string
     * @return - the alphanumeric string
     */
    public String getAlphaNumericString(int n) {

        // choose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    /**
     * This generates a checkin QR code
     * @return - the QR code string data
     */
    public String generateQRCode() {
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
        String randomString = getAlphaNumericString(20);
        return QRCodeGenerator.getQRCodeData(QRCodeGenerator.generateQRCode(randomString, 400, 400));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
