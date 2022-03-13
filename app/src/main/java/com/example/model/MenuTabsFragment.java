package com.example.model;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MenuTabsFragment extends Fragment {

    private OnMenuListener listener = null;

    public interface OnMenuListener {
        void onAttach(String message);
        void onClick(String message);
    }

    public MenuTabsFragment() {
        // Required empty public constructor
    }

    public static MenuTabsFragment newInstance(String param1, String param2) {
        MenuTabsFragment fragment = new MenuTabsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnMenuListener){
            listener = (OnMenuListener) context;
            listener.onAttach(getResources().getString(R.string.main_menu));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_tabs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        addListener(view, R.id.fragment_menu);
//        addListener(view, R.id.tag_photos);
//        addListener(view, R.id.tag_plate);
        final Button button1 = view.findViewById(R.id.tag_main_menu);
        addListener(button1);

        final Button button2 = view.findViewById(R.id.tag_plate);
        addListener(button2);

        final Button button3 = view.findViewById(R.id.tag_photos);
        addListener(button3);

    }

    private void addListener(Button button){
//        button.setOnClickListener(view1 -> {
//                assert listener != null;
//                listener.onClick();
//
//        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert listener != null;
                listener.onClick(button.getText().toString());
            }
        });
    }
}