package com.example.qrcheckin.user.editevent;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.databinding.FragmentEditEventBinding;
import com.example.qrcheckin.user.createevent.CreateEventViewModel;

public class EditEventFragment extends Fragment {
    private FragmentEditEventBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Event event = getArguments().getSerializable("event") != null ? (Event) getArguments().getSerializable("event") : null;


        return root;
    }
}
