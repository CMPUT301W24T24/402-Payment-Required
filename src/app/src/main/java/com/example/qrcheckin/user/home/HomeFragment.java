package com.example.qrcheckin.user.home;

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

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

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

