package com.example.qrcheckin.admin.allprofiles;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.ProfileArrayAdapter;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentAllProfilesBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The fragment class for all profiles which can only be seen by admins
 * This has not been worked on yet
 */
public class AllProfilesFragment extends Fragment {

    private FragmentAllProfilesBinding binding;
    private ListView listView;
    ArrayList<User> profileDataList;
    ProfileArrayAdapter profileArrayAdapter;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private FloatingActionButton deleteCityButton;
    private Database database;

    private int position = ListView.INVALID_POSITION;

    /**
     *
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
        database = new Database();

        getAllUsers();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AllProfilesFragment.this.position = position;
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
             *
             * @param position
             */
            private void deleteProfile(int position) {
                User profileDelete = profileDataList.get(position);
                profileDataList.remove(position);
                profileArrayAdapter.notifyDataSetChanged();
                position = ListView.INVALID_POSITION;

                //delete the user to the FireStore Collection
                usersRef.document(profileDelete.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                            }
                        });
            }
        });
        return root;
    }

    /**
     * Get all the users from the Fi
     */
    private void getAllUsers() {
        profileDataList.clear();
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}