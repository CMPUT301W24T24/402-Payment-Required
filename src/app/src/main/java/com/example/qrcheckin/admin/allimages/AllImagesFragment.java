package com.example.qrcheckin.admin.allimages;

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
import com.example.qrcheckin.databinding.FragmentAllImagesBinding;

public class AllImagesFragment extends Fragment {

    private FragmentAllImagesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AllImagesViewModel allImagesViewModel =
                new ViewModelProvider(this).get(AllImagesViewModel.class);

        binding = FragmentAllImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAllImages;
        allImagesViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}