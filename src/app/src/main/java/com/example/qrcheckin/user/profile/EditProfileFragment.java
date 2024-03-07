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
    protected EditText contact;
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
        contact = view.findViewById(R.id.edit_profile_contact);
        homepage = view.findViewById(R.id.edit_profile_homepage);
        // Set the user profile and details
        profileName.setText(user.getName());
        contact.setText(user.getEmail());
        homepage.setText(user.getHomepage());

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
                        String pcontact = contact.getText().toString();
                        String phomepage = homepage.getText().toString();
                        boolean condition = true;
                        // Check if the user did not put any value for the profile name
                        if (pName.isEmpty()) {
                            profileName.setError("You must enter the profile name");
                            condition = false;
                        }
                        // Check if the user did not put any value for the gmail contact
                        if (pcontact.isEmpty()) {
                            contact.setError("You must enter the gmail contact information");
                            condition = false;
                        }
                        // Check if the user did not put any value for the homepage
                        if (phomepage.isEmpty()) {
                            homepage.setError("You must enter the homepage detail");
                            condition = false;
                        }
                        if (condition) {
                            user.setName(pName);
                            user.setEmail(pcontact);
                            user.setHomepage(phomepage);
                            dismiss();
                        }
                    }
                });
            }
        });
        return dialog;
    }
}
