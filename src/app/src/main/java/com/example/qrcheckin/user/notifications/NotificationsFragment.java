package com.example.qrcheckin.user.notifications;

import androidx.lifecycle.ViewModel;
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
import com.example.qrcheckin.databinding.FragmentEventBinding;
import com.example.qrcheckin.databinding.FragmentHostedEventBinding;
import com.example.qrcheckin.databinding.FragmentNotificationsBinding;
import com.example.qrcheckin.user.hostedevent.HostedEventViewModel;
import com.example.qrcheckin.user.myevent.EventViewModel;
import com.example.qrcheckin.user.notifications.NotificationsViewModel;
import com.example.qrcheckin.user.notifications.NotificationsViewModel;
import com.example.qrcheckin.user.notifications.NotificationsViewModel;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel NotificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        NotificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}