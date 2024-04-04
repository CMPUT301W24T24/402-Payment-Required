package com.example.qrcheckin.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.qrcheckin.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileArrayAdapter extends ArrayAdapter<User> {


    int selectedPosition = -1;

    interface CreateProfileListener {
        void createProfile(int position);
    }

    private List<User> profiles = new ArrayList<>();
    private Context context;

    /** Constructor for the ProfileArrayAdapter
     * @param context
     * @param profiles
     */
    public ProfileArrayAdapter(@NonNull Context context, ArrayList<User> profiles) {
        super(context, 0, profiles);
        this.profiles = profiles;
        this.context = context;
    }

    /**
     * Set the view for the AllProfiles for the admin
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to
     * @return the view of the AllProfiles for the Admin
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_profile_admin, parent, false);

        }
        User user = profiles.get(position);

        ImageView profilePic = view.findViewById(R.id.adminProfilePicture);
        TextView profileName = view.findViewById(R.id.adminProfileName);
        TextView profileNumber = view.findViewById(R.id.adminProfilePhoneNumber);
        TextView profileEmail = view.findViewById(R.id.adminProfileEmail);

        profileName.setText(user.getName());
        if (user.getPhone().isEmpty()) {
            profileNumber.setText("###-###-####");
        } else {
            profileNumber.setText(user.getPhone());
        }

        if (user.getEmail().isEmpty() ) {
            profileEmail.setText("***********");
        } else {
            profileEmail.setText(user.getEmail());
        }

//        ViewGroup rootView = (ViewGroup) view.findViewById(R.id.profileCardView);  // Replace with your ID

//        // Set background based on selected position
//        if (selectedPosition == position) {
//            rootView.setBackgroundColor(context.getResources().getColor(R.color.pink));
//        } else {
//            rootView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//        }

        Database database = new Database();
        database.getUserPicture(user, profilePic);
        return view;
    }

//    public void setSelectedPosition(int position, boolean isSelected) {
//        selectedPosition = position;
//        notifyDataSetChanged();  // Notify adapter about data change
    //}
}
