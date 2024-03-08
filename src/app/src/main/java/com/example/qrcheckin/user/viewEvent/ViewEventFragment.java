package com.example.qrcheckin.user.viewEvent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentViewEventBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ViewEventFragment extends Fragment {
    private FragmentViewEventBinding binding;
    private ViewEventViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentViewEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(ViewEventViewModel.class);

        // Set the ViewModel for data binding
        binding.getRoot();

        // Handle the "Attend Event" button click
        binding.viewEventSignUp.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference signupsRef = db.collection("signUps");

            User user = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser();
            Intent intent = new Intent();
            Event event = intent.getParcelableExtra("event" );
            // Add the event to the collection
            HashMap<String, String> data = new HashMap<>();
            data.put("user_id", user.getId());
            data.put("event_id", event.getId());
            signupsRef.add(data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Signed up with ID: " + documentReference.getId());
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
