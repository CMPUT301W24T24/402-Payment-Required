package com.example.qrcheckin.user.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrcheckin.QRCheckInApplication;
import com.example.qrcheckin.R;
import com.example.qrcheckin.core.User;

public class EditProfileFragment extends DialogFragment {

    protected EditText profileName;
    protected EditText email;
    protected EditText phone;
    protected EditText homepage;
    protected User user = ((QRCheckInApplication) requireActivity().getApplication()).getCurrentUser();

    public static EditProfileFragment newInstance() {
        Bundle args = new Bundle();
        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_edit_profile, null);
        profileName = view.findViewById(R.id.edit_profile_name);
        email = view.findViewById(R.id.edit_profile_email);
        phone = view.findViewById(R.id.edit_profile_phone);
        homepage = view.findViewById(R.id.edit_profile_homepage);
        // Set the user profile and details
        profileName.setText(user.getName());
        email.setText(user.getEmail());
        homepage.setText(user.getHomepage());
        phone.setText(user.getPhone());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setTitle("Edit Profile details")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pName = profileName.getText().toString();
                        String pEmail = email.getText().toString();
                        String pHomepage = homepage.getText().toString();
                        String pPhone = phone.getText().toString();
                        boolean condition = true;
                        // Check if the user did not put any value for the profile name
                        if (pName.isEmpty()) {
                            profileName.setError("You must enter the profile name");
                            condition = false;
                        }
                        if (condition) {
                            user.setName(pName);
                            user.setEmail(pEmail);
                            user.setHomepage(pHomepage);
                            user.setPhone(pPhone);
                            dismiss();
                        }
                    }
                });
            }
        });
        return dialog;
    }
}
