package com.example.qrcheckin.user.home;

<<<<<<< HEAD
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
=======
>>>>>>> eb80fc55236ebb31f94e3f77e239875e4d878cc8
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.core.UserList;
import com.example.qrcheckin.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

<<<<<<< HEAD
import java.io.File;
import java.io.FileOutputStream;


=======
import java.util.List;

/**
 * The home fragment which contains the QR scanner
 */
>>>>>>> eb80fc55236ebb31f94e3f77e239875e4d878cc8
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    /**
     * Creates the view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        ImageView qrButton=root.findViewById(R.id.qr_icon);
        Button share = root.findViewById(R.id.shareqrcode);

        share.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 BitmapDrawable bitmapDrawable = (BitmapDrawable) qrButton.getDrawable();
                 Bitmap bitmap = bitmapDrawable.getBitmap();
                 shareImageAndText(bitmap);
             }
         });

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQRCodeScanner();
            }
        });

        return root;
    }

    /**
     * Destroys the view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * A method which opens the QR scanner
     */
    public void initQRCodeScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setOrientationLocked(true);
        options.setPrompt("Scan a QR code to sign in");
        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null)
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
                else
                    checkInEventByQR(result.getContents());
            });

    /**
<<<<<<< HEAD
     * Method for opening the share menu
     * @param bitmap the coordinates of the image being shared
     */
    public void shareImageAndText(Bitmap bitmap) {
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    /**
     * A method which retrieves the image being shared to another app
     * @param bitmap the coordinates of the image
     * @return
     * The image retrieved
     */
    public Uri getImageToShare(Bitmap bitmap) {
        File imageFolder = new File(getContext().getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(getContext(), "com.anni.shareimage.fileprovider", file);
        } catch (Exception e) {
            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }
}
=======
     * Find an event in the database by QR value, returns null if the QR value is not present in the database
     * @param qrValue Unique QR value to search for
     * @return The event object with given QR value, null otherwise
     */
    public void checkInEventByQR(String qrValue){
        CollectionReference eventsRef = FirebaseFirestore.getInstance().collection("events");
        Task<QuerySnapshot> querySnapshotTask = eventsRef.whereEqualTo("checkin_id",qrValue).get();
        querySnapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                User currentUser=((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser();
                List<DocumentSnapshot> snapshots=querySnapshotTask.getResult().getDocuments();
                if(snapshots.isEmpty()){
                    Log.i("QR Scanner","Failed to check into event by QR: Code scanned is not in database");
                    Toast.makeText(getActivity(), "Event ID does not exist: "+qrValue, Toast.LENGTH_LONG).show();
                    return;
                }
                DocumentSnapshot snapshot = snapshots.get(0);
                Event event=new Event(snapshot.getId(),
                        currentUser,
                        snapshot.getString("name"),
                        snapshot.getString("description"),
                        snapshot.getString("posterRef"),
                        snapshot.getDate("time"),
                        snapshot.getString("location"),
                        snapshot.getDouble("location_geo_lat"),
                        snapshot.getDouble("location_geo_long"),
                        snapshot.getString("checkinId"),
                        snapshot.getString("promoteId"),
                        snapshot.getBoolean("geo"),
                        snapshot.getDouble("limit").intValue(),
                        new UserList()
                        );
                Log.i("QR","Event created successfully");
                event.checkIn(currentUser);

                Toast.makeText(getActivity(), "Success! Checked into "+event.getName(), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("QR Scanner","Failed to check into event by QR: Database failed to retrieve QR");
            }
        });

    }
}

>>>>>>> eb80fc55236ebb31f94e3f77e239875e4d878cc8
