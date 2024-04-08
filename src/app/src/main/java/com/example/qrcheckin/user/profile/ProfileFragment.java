package com.example.qrcheckin.user.profile;

import static android.content.ContentValues.TAG;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * A fragment where a user can edit their personal details
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private boolean imageUpdated = false;
    private boolean clearedImage = false;
    private Uri updatedImageUri;
    private boolean imageTooLarge = false;
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
                        binding.profilePicture.setImageURI(uri);
                        updatedImageUri = uri;
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        binding.editProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        binding.removeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.profilePicture.setImageBitmap(user.generateProfilePicture());
                clearedImage = true;
                imageUpdated = true;
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
                    // Reference: https://medium.com/@everydayprogrammer/uploading-files-to-firebase-storage-in-android-studio-using-java-63f43b4c8d72
                    if (imageUpdated) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReference();
                        Uri imageUri = updatedImageUri;
                        String imageName = user.getId();
                        if (!clearedImage) {
                            user.setImageRef("users/" + imageName);
                            StorageReference imageRef = storageReference.child("users/" + imageName);
                            imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("Firebase", "Profile picture uploaded successfully");
                                    NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
                                    View headerView = navigationView.getHeaderView(0);
                                    ImageView navProfileImage = headerView.findViewById(R.id.nav_profile_pic);
                                    // Set the profile picture of the user to the XML view
                                    Database database = new Database();
                                    database.getUserPicture(user, navProfileImage);

                                    // Delete the image if it was scaled down
                                    // Reference: https://stackoverflow.com/questions/23716683/android-delete-file-after-images-media-insertimage Nate. Accessed on 2024-03-31
                                    if (imageTooLarge) {
                                        requireContext().getContentResolver().delete(updatedImageUri, null, null);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firebase", "Profile picture upload failed", e);
                                }
                            });
                        } else {
                            if (user.getImageRef() != null) {
                                StorageReference imageRef = storageReference.child(user.getImageRef());
                                imageRef.delete();
                                user.setImageRef(null);
                            }
                        }
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
                                    Database database = new Database();
                                    database.getUserPicture(user, navProfileImage);

                                    // Show a toast message to the user for successful profile update
                                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
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
