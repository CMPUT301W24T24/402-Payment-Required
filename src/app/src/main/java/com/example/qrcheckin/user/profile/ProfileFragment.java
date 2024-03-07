package com.example.qrcheckin.user.profile;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentProfileBinding;
import com.example.qrcheckin.databinding.FragmentViewEventBinding;
import com.example.qrcheckin.user.viewEvent.ViewEventViewModel;

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

            // Print user details to the log
            Log.d(TAG, "User Details:");
            Log.d(TAG, "Name: " + user.getName());
            Log.d(TAG, "Email: " + user.getEmail());
            Log.d(TAG, "Phone: " + user.getPhone());
            Log.d(TAG, "Homepage: " + user.getHomepage());
            Log.d(TAG, "Geolocation Enabled: " + user.isGeo());
        } else {
            Log.e(TAG, "User is null");
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
