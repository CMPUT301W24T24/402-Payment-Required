package com.example.qrcheckin.admin.allprofiles;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.ProfileArrayAdapter;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentAllProfilesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The fragment class for all profiles which can only be seen by admins
 */
public class AllProfilesFragment extends Fragment {

    protected FragmentAllProfilesBinding binding;
    private ListView listView;
    ArrayList<User> profileDataList;
    ProfileArrayAdapter profileArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;
    private FloatingActionButton deleteCityButton;
    private CollectionReference checkinsRef;
    private CollectionReference signupsRef;
    private Database database = new Database();
    private int position = ListView.INVALID_POSITION;

    /**
     * Create the view with all the profiles
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view of allProfilesFragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AllProfilesViewModel allProfilesViewModel =
                new ViewModelProvider(this).get(AllProfilesViewModel.class);

        binding = FragmentAllProfilesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.adminAllProfilesListview;
        allProfilesViewModel.getProfileList().observe(getViewLifecycleOwner(), listView::setAdapter);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
        profileDataList = new ArrayList<>();
        eventsRef = db.collection("events");
        checkinsRef = db.collection("checkins");
        signupsRef = db.collection("signUpTable");

        String search = binding.allProfileSearchBar.getText().toString();
        getAllUsers(search.isEmpty() ? null : search);

        binding.allProfileSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = binding.allProfileSearchBar.getText().toString();
                getAllUsers(search.isEmpty() ? null : search);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Animation zoomAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_in);
                view.startAnimation(zoomAnimation);
                profileArrayAdapter.setSelectedPosition(position, true);
                AllProfilesFragment.this.position = position;
                Toast.makeText(requireContext(), "Please click the floating button to delete the selected profile", Toast.LENGTH_LONG).show();
            }
        });


        deleteCityButton = binding.adminAllProfilesDelete;
        deleteCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != ListView.INVALID_POSITION) {
                    deleteProfile(position);

                } else {
                    Toast.makeText(requireContext(), "No profile is selected for deletion", Toast.LENGTH_LONG).show();
                }
            }


            /**
             * Delete the selected profile
             * @param position the position of the profile, which needs deleting
             */
            private void deleteProfile(int position) {
                User profileDelete = profileDataList.get(position);

                //delete the user in the FireStore Collection
                String userId = profileDelete.getId();
                DocumentReference ownerRef = usersRef.document(userId);
                String refString = ownerRef.getPath();
                Log.d("Document Reference Path", refString);
                //Create the document reference
                deleteRelatedEvents(ownerRef);
                usersRef.document(profileDelete.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String search = binding.allProfileSearchBar.getText().toString();
                                getAllUsers(search.isEmpty() ? null : search);
                                Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String search = binding.allProfileSearchBar.getText().toString();
                                getAllUsers(search.isEmpty() ? null : search);
                                Log.d("Firestore", "Profile not deleted " + e.toString());
                            }
                        });
                //delete the checkins and signups with userID
                deleteCheckInsAndSignUps(userId);
                position = ListView.INVALID_POSITION;

            }

            /**
             * Delete all the checkins and sign ups related to the user
             * @param userId the id of the user profile
             */
            private void deleteCheckInsAndSignUps(String userId) {
                //delete signUps
                signupsRef.whereEqualTo("user_id", userId).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                                    doc.getReference().delete();
                                }
                            }
                        });
                //delete checkIns
                checkinsRef.whereEqualTo("user_id", userId).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                                    doc.getReference().delete();
                                }
                            }
                        });
            }

            /**
             * Delete all related events that the profile has created and is the owner of
             * @param userRef DocumentReference referencing a profile
             */
            private void deleteRelatedEvents(DocumentReference userRef) {
                eventsRef.whereEqualTo("host", userRef).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                Database.deleteEvent(doc.getId());
                            }
                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Error getting events for deletion", e));
            }
        });
        return root;
    }
            

    /**
     * Get all the users from the Firestore database
     */
    private void getAllUsers(String search) {
        profileDataList.clear();

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (search != null && !Objects.requireNonNull(Objects.requireNonNull(document.getString("name")).toLowerCase()).contains(search.toLowerCase())) {
                        continue;
                    }
                    // Convert each document to a User object and add it to the list
                    String id = document.getId();
                    String name = document.getString("name");
                    String email = document.getString("email");
                    String phone = document.getString("phone");
                    String homepage = document.getString("homepage");
                    String image = document.getString("imageRef");
                    boolean geo = Boolean.TRUE.equals(document.getBoolean("geo"));
                    boolean admin = Boolean.TRUE.equals(document.getBoolean("admin"));
                    User user = new User(id, name, email, phone, homepage, geo, admin, image);
                    profileDataList.add(user);
                }

                profileArrayAdapter = new ProfileArrayAdapter(requireContext(), profileDataList);
                listView.setAdapter(profileArrayAdapter);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    /**
     * Sets the binding to null when the view is destroyed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}