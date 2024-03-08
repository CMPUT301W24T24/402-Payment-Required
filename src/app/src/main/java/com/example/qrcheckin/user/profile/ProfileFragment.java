package com.example.qrcheckin.user.profile;

import static android.content.ContentValues.TAG;

import static androidx.navigation.Navigation.findNavController;

import static com.google.common.reflect.Reflection.getPackageName;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentProfileBinding;
import com.example.qrcheckin.databinding.FragmentViewEventBinding;
import com.example.qrcheckin.user.viewEvent.ViewEventViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

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
     * @return the View that contains all the information about the ProfileFragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the global user instance
        User user = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser();

        if (user != null) {
            // Set homepage, profile info, and contact of the global user to the XML views
            binding.profileName.setText(user.getName());
            binding.profileEmail.setText(user.getEmail());
            binding.profilePhoneNumber.setText(user.getPhone());
            binding.profileHomepageLink.setText(user.getHomepage());

            // Set geolocation checkbox state based on user's preference
            binding.profileGeolocation.setChecked(user.isGeo());

            //String uri = "@drawable/cat.jpg";
            //int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            binding.profilePicture.setImageResource(R.drawable.cat);

        } else {
            Log.e(TAG, "User is null");
        }

        /**
         * Save the updated profile information on FireStore database
         */
        binding.profileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    user.setName(binding.profileName.getText().toString());
                    user.setEmail(binding.profileEmail.getText().toString());
                    user.setPhone(binding.profilePhoneNumber.getText().toString());
                    user.setHomepage(binding.profileHomepageLink.getText().toString());
                    if (binding.profileGeolocation.isChecked()) {
                        user.setGeo(TRUE);
                    } else {
                        user.setGeo(FALSE);
                    }
                    //Accessing the users database
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference usersRef = db.collection("users");
                    DocumentReference docRef = usersRef.document(user.getId());
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("name", user.getName());
                    data.put("email", user.getEmail());
                    data.put("phone", user.getPhone());
                    data.put("homepage", user.getHomepage());
                    data.put("geo", user.isGeo());
                    docRef.update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firestore", e.toString());
                                }
                            });
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * if the user switches to change profile without click on EditButton, the information would not be changed on the UI
     */
    @Override
    public void onPause() {
        super.onPause();
        User user = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser();
        if (!(binding.profileName.getText().toString().equals(user.getName()))) {
            binding.profileName.setText(user.getName());

        }
        if (!(binding.profilePhoneNumber.getText().toString().equals(user.getPhone()))) {
            binding.profilePhoneNumber.setText(user.getPhone());
        }
        if (!(binding.profileEmail.getText().toString().equals(user.getEmail()))) {
            binding.profileEmail.setText(user.getEmail());
        }
        if (!(binding.profileHomepageLink.getText().toString().equals(user.getHomepage()))) {
            binding.profileHomepageLink.setText(user.getHomepage());
        }
        if (!(binding.profileGeolocation.isChecked() == user.isGeo())) {
            binding.profileGeolocation.setChecked(false);
        }
    }
}
