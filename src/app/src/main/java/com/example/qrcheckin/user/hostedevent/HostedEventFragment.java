package com.example.qrcheckin.user.hostedevent;

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
import com.example.qrcheckin.databinding.FragmentHostedEventBinding;

public class HostedEventFragment extends Fragment {

    private FragmentHostedEventBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HostedEventViewModel hostedEventViewModel =
                new ViewModelProvider(this).get(HostedEventViewModel.class);

        binding = FragmentHostedEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHostedEvent;
        hostedEventViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}