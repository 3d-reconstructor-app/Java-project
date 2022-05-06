
package com.example.a3dmodel;

import androidx.fragment.app.FragmentManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a3dmodel.photo_fragment.GridFragment;
import com.example.a3dmodel.photo_fragment.ImagePagerFragment;
import com.example.a3dmodel.photo_fragment.GridFragment;

public class tabPhoto extends Fragment {
    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";
    private Fragment fragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_photos, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button camera = (Button) getView().findViewById(R.id.button_camera);
        View.OnClickListener CameraButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "hello", Toast.LENGTH_SHORT).show();
            }
        };

        camera.setOnClickListener(CameraButtonOnClickListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
            // Return here to prevent adding additional GridFragments when changing orientation.
            return;
        }

        FragmentManager fragmentManager = getFragmentManager();
        assert fragmentManager != null;
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true) // Optimize for shared element transition
//                .addSharedElement(transitioningView, transitioningView.getTransitionName())
                .replace(R.id.fragment_photo, new GridFragment(), GridFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();


    }


    //    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.SinglePhotoLayout, CardLayout.class, null)
//                .addToBackStack(null)
//                .commit();
//        super.onViewCreated(view, savedInstanceState);
//    }


}

