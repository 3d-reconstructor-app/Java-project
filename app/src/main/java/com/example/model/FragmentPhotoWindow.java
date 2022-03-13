package com.example.model;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentPhotoWindow extends Fragment  {
    // хочется -- чтобы фрагмент учитывал тот активити, в котором он создается
    private static final String CONTENT_TAG = "WTF";
    private String Tag;
    public FragmentPhotoWindow(){
        // Required empty public constructor
    }

    public static FragmentPhotoWindow newInstance(String tag){
        FragmentPhotoWindow fragmentPhotoWindow = new FragmentPhotoWindow();
        Bundle args = new Bundle();
        args.putString(CONTENT_TAG, tag);
        fragmentPhotoWindow.setArguments(args);
        return fragmentPhotoWindow;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            Tag = getArguments().getString(CONTENT_TAG);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_window, container, false);
    }

    // нужно при взаимодействии с кнопкой изменить содержание layout
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
