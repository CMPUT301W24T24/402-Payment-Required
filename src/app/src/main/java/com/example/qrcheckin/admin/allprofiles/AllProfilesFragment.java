package com.example.qrcheckin.admin.allprofiles;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.qrcheckin.R;
import com.example.qrcheckin.databinding.FragmentAllProfilesBinding;

/**
 * The fragment class for all profiles which can only be seen by admins
 * This has not been worked on yet
 */
public class AllProfilesFragment extends Fragment {

    private FragmentAllProfilesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AllProfilesViewModel allProfilesViewModel =
                new ViewModelProvider(this).get(AllProfilesViewModel.class);

        binding = FragmentAllProfilesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAllProfile;
        allProfilesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}