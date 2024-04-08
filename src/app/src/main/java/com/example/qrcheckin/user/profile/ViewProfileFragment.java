package com.example.qrcheckin.user.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qrcheckin.core.Database;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentViewProfileBinding;

public class ViewProfileFragment extends Fragment {

    private FragmentViewProfileBinding binding;
    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentViewProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        user = getArguments().getSerializable("user") != null ? (User) getArguments().getSerializable("user") : null;

        if (user != null) {
            binding.viewProfileName.setText(user.getName());
            binding.viewProfileEmail.setText(user.getEmail().isEmpty() ? "No email" : user.getEmail());
            binding.viewProfilePhoneNumber.setText(user.getPhone().isEmpty() ? "No phone number" : user.getPhone());
            binding.viewProfileHomepageLink.setText(user.getHomepage().isEmpty() ? "No homepage" : user.getHomepage());
            Database db = new Database();
            db.getUserPicture(user, binding.viewProfilePicture);
        }
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
