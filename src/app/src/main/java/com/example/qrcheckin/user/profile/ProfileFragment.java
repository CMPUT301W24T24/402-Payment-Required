package com.example.qrcheckin.user.profile;

import static android.content.ContentValues.TAG;

import static androidx.navigation.Navigation.findNavController;

import static com.google.common.reflect.Reflection.getPackageName;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentProfileBinding;
import com.example.qrcheckin.databinding.FragmentViewEventBinding;
import com.example.qrcheckin.user.viewEvent.ViewEventViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    int selectPicture = 200;

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
            ImageView profileImage = binding.profilePicture;
            // Set the profile picture of the user to the XML view
            Database db = new Database();
            db.getUserPicture(user, profileImage);

        } else {
            Log.e(TAG, "User is null");
        }

        binding.editProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

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

                    // Saving the user's profile picture
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    String imageName = user.getId() + ".jpg";
                    StorageReference imageRef = storageReference.child("users/" + imageName);
                    Uri imageUri = Uri.parse(binding.profilePicture.getTag().toString());
                    UploadTask uploadTask = imageRef.putFile(imageUri);

                    // Monitor the upload progress.
                    uploadTask.addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        // This listener will provide the upload status of the file.
                        // You can add a progress bar in you app and set max of the progress bar to taskSnapshot.getTotalByteCount().
                        // And process of the progress bar to taskSnapshot.getBytesTransferred().
                    }).addOnSuccessListener(taskSnapshot -> {
                        // This listener is triggered when the file is uploaded successfully.
                        // Using the below code you can get the download url of the file
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Do something with the file URL, like saving it to a database or displaying it from cloud.
                        });
                    }).addOnFailureListener(exception -> {
                        // This listener is triggered if there was an exception occured while uploading the file.
                        // You can display a error message from here if exception occurs.
                    });

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
                    data.put("imageRef", user.getImageRef());
                    docRef.update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Firestore", "DocumentSnapshot successfully written!");
                                    // set the user name to the navigation view
                                    NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
                                    View headerView = navigationView.getHeaderView(0);
                                    TextView navProfileName = headerView.findViewById(R.id.nav_profile_name);
                                    navProfileName.setText(user.getName());

                                    ImageView navProfileImage = headerView.findViewById(R.id.nav_profile_pic);
                                    // Set the profile picture of the user to the XML view
                                    Database database = new Database();
                                    database.getUserPicture(user, navProfileImage);
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

    void imageChooser() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), selectPicture);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == selectPicture) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    binding.profilePicture.setImageURI(selectedImageUri);
                    binding.profilePicture.setTag(selectedImageUri.toString());
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

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
