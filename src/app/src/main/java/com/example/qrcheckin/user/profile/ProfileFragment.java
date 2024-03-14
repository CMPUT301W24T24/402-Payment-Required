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
            binding.profilePicture.setImageBitmap(generateProfilePicture(user));

        } else {
            Log.e(TAG, "User is null");
        }
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

    /**
     * Generates a profile picture for the user
     * Reference: https://stackoverflow.com/questions/2655402/android-canvas-drawtext Gaz. Accessed 2024-03-14
     * @param user the user
     * @return the profile picture
     */
    private Bitmap generateProfilePicture(User user) {
        String name = "NU";
        if (user.getName().length() >= 2) {
            name = user.getName().substring(0, 2);
        }
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(name, 50, 55, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(hashedColor(name.substring(0,1).toLowerCase()));
        canvas.drawCircle(50, 50, 35, paint);
        return bitmap;
    }
    private int hashedColor(String s) {
        switch (s) {
            case "a":
                return Color.parseColor("#FF0000");
            case "b":
                return Color.parseColor("#FFA500");
            case "c":
                return Color.parseColor("#FFFF00");

            case "d":
                return Color.parseColor("#008000");

            case "e":
                return Color.parseColor("#0000FF");

            case "f":
                return Color.parseColor("#4B0082");

            case "g":
                return Color.parseColor("#EE82EE");

            case "h":
                return Color.parseColor("#A9A9A9");

            case "i":
                return Color.parseColor("#808080");

            case "j":
                return Color.parseColor("#FFFFFF");

            case "k":
                return Color.parseColor("#FFC0CB");

            case "l":
                return Color.parseColor("#FFA07A");

            case "m":
                return Color.parseColor("#FFD700");

            case "n":
                return Color.parseColor("#ADFF2F");

            case "o":
                return Color.parseColor("#00FFFF");

            case "p":
                return Color.parseColor("#800080");

            case "q":
                return Color.parseColor("#DA70D6");

            case "r":
                return Color.parseColor("#696969");

            case "s":
                return Color.parseColor("#D3D3D3");

            case "t":
                return Color.parseColor("#F5F5F5");

            case "u":
                return Color.parseColor("#FF69B4");

            case "v":
                return Color.parseColor("#00FF7F");

            case "w":
                return Color.parseColor("#87CEEB");

            case "x":
                return Color.parseColor("#FF6347");

            case "y":
                return Color.parseColor("#FF4500");

            case "z":
                return Color.parseColor("#FF1493");

            default:
                return Color.parseColor("#000000");

        }
    }

}
