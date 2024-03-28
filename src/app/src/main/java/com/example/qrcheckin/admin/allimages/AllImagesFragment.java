package com.example.qrcheckin.admin.allimages;

import static com.example.qrcheckin.core.Database.getAllImages;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.qrcheckin.core.ImagesEventArrayAdaptor;
import com.example.qrcheckin.core.ImagesUserArrayAdaptor;
import com.example.qrcheckin.core.Event;
import com.example.qrcheckin.core.User;
import com.example.qrcheckin.databinding.FragmentAllImagesBinding;

import java.util.ArrayList;

/**
 * The fragment class for all images which can only be seen by admins
 * This has not been worked on yet
 */
public class AllImagesFragment extends Fragment {

    private FragmentAllImagesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AllImagesViewModel allImagesViewModel =
                new ViewModelProvider(this).get(AllImagesViewModel.class);

        binding = FragmentAllImagesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ArrayList<Event> events = new ArrayList<>();
        ImagesEventArrayAdaptor eventAdapter = new ImagesEventArrayAdaptor(getContext(), events);
        ArrayList<User> users = new ArrayList<>();
        ImagesUserArrayAdaptor userAdapter = new ImagesUserArrayAdaptor(getContext(), users);
        getAllImages(eventAdapter, userAdapter);

        binding.allImagesEventListView.setAdapter(eventAdapter);
        binding.allImagesProfileListView.setAdapter(userAdapter);

        binding.allImagesEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0 || position >= events.size()) {
                    return;
                }
                Event event = events.get(position);
                DeleteImageFragment deleteImageFragment = new DeleteImageFragment(event, null, eventAdapter);
                deleteImageFragment.show(getParentFragmentManager(), "deleteImage");
            }
        });

        binding.allImagesProfileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0 || position >= users.size()) {
                    return;
                }
                User user = users.get(position);
                DeleteImageFragment deleteImageFragment = new DeleteImageFragment(null, user, userAdapter);
                deleteImageFragment.show(getParentFragmentManager(), "deleteImage");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}