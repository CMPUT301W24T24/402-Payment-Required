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
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentViewEventBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * A fragment which is acrtivitated when an event is clicked on
 */
public class ViewEventFragment extends Fragment {
    private FragmentViewEventBinding binding;
    private Event event;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentViewEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModel
        ViewEventViewModel viewModel = new ViewModelProvider(this).get(ViewEventViewModel.class);

        user = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser();
        event = getArguments().getSerializable("event") != null ? (Event) getArguments().getSerializable("event") : null;

        // Set the event details
        binding.viewEventTitle.setText(event.getName());
        binding.viewEventDate.setText(event.getTime().toString());
        binding.viewEventDescription.setText(event.getDescription());

        // set the button to sign up or un-sign up
        if (event.isCurrentUserSignedUp()) {
            setUnSignUpButton();
            binding.viewEventSignUp.setText(R.string.unsign_up);
        } else {
            setSignUpButton();
            binding.viewEventSignUp.setText(R.string.sign_up);
        }

        // Handle the "Attend Event" button click

        return root;
    }

    /**
     * Set the button to sign up for the event
     */
    private void setSignUpButton() {
        binding.viewEventSignUp.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference signupsRef = db.collection("signUpTable");

            // Add the event to the collection
            HashMap<String, Object> data = new HashMap<>();
            data.put("user_id", user.getId());
            data.put("event_id", event.getId());
            signupsRef.add(data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Signed up with ID: " + documentReference.getId());
                        // update the button
                        setUnSignUpButton();
                        binding.viewEventSignUp.setText(R.string.unsign_up);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", e.toString());
                    });
        });
    }

    /**
     * Set the button to un-sign up for the event
     */
    private void setUnSignUpButton() {
        binding.viewEventSignUp.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference signupsRef = db.collection("signUpTable");

            // fetch the document(s) to delete
            signupsRef.whereEqualTo("user_id", user.getId())
                    .whereEqualTo("event_id", event.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // delete the document(s)
                                task.getResult().getDocuments().forEach(document -> {
                                    signupsRef.document(document.getId()).delete();
                                    Log.d("Firestore", "Document " + document.getId() + " successfully deleted!");
                                });
                                // update the button
                                setSignUpButton();
                                binding.viewEventSignUp.setText(R.string.sign_up);
                            } else {
                                Log.e("Firestore", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        });
    }

    private void getEventPoster() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
