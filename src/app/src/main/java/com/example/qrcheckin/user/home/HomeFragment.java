package com.example.qrcheckin.user.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.List;

/**
 * The home fragment which contains the QR scanner
 */
public class HomeFragment extends Fragment implements LocationListener {

    private FragmentHomeBinding binding;
    private Location currentLocation;

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
    @SuppressLint("MissingPermission")//requestPermissionIfNecessary corrects for this
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        ImageView qrButton=root.findViewById(R.id.qr_icon);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQRCodeScanner();
            }
        });

        //req permissions for location
        requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});//coarse is fine because its accurate to about 100m

        //set this as a listener for location change to assure accuracy
        LocationManager locationManager = (LocationManager) root.getContext().getSystemService(Context.LOCATION_SERVICE);
        currentLocation = new Location(LocationManager.GPS_PROVIDER);
        for (String provider:locationManager.getProviders(true)){
            locationManager.requestLocationUpdates(provider, 1000, 0, this);
        }

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
                Log.i("Location",currentLocation.getLatitude()+"|"+currentLocation.getLongitude());

                if(currentUser.isGeo())//user geo enabled
                    if (event.checkIn(currentUser,currentLocation.getLatitude(),currentLocation.getLongitude()))//full location check in
                        Toast.makeText(getActivity(), "Success! Checked into "+event.getName(), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), "Failed to check into "+event.getName()+", you are too far from the event or your GPS is disabled!", Toast.LENGTH_LONG).show();
                else//user geo disabled
                    if(event.checkIn(currentUser))//raw check in
                        Toast.makeText(getActivity(), "Success! Checked into "+event.getName(), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), "Failed to check into "+event.getName()+", geolocation is required for this event, please enable geolocation!", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("QR Scanner","Failed to check into event by QR: Database failed to retrieve QR");
            }
        });

    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation=location;
    }

    //needed for location permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
}

