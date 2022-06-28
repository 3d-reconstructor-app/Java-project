package com.example.a3dmodel.visualisation.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a3dmodel.R;
import com.example.a3dmodel.visualisation.GLView;

public class View3DFragment extends Fragment {
    private GLView glView;
    String model;

    @SuppressLint("ValidFragment")
    public View3DFragment(String model) {
        this.model = model;
    }

    public View3DFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.activity_fullscreen, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setGlView(view.findViewById(R.id.gl_view));
        Button resetButton = (Button) view.findViewById(R.id.reset_button);
        View.OnClickListener resetButtonOnClickListener = this::Reset;
        resetButton.setOnClickListener(resetButtonOnClickListener);
        Button getBackButton = (Button) view.findViewById(R.id.exit_button);
        assert getBackButton != null;
        View.OnClickListener getBackButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("getBackButton clicked");
                assert getFragmentManager() != null;
                assert getActivity().getFragmentManager() != null;
                getActivity().onBackPressed();
            }
        };
        getBackButton.setOnClickListener(getBackButtonClickListener);
    }

    public void setGlView(GLView v) {
        glView = v;
        glView.makeRenderer(model);
    }

    public void Reset(View v) {
        glView.Reset();
    }


}
