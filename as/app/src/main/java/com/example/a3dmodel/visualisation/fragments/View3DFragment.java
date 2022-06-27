package com.example.a3dmodel.visualisation.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.a3dmodel.R;
import com.example.a3dmodel.data.ModelData;
import com.example.a3dmodel.tab3DPlain;
import com.example.a3dmodel.visualisation.GLView;

public class View3DFragment extends Fragment {
    private GLView glView;
    String model;
//    private ListView listView;
//    private TextView selectedView = tab3DPlain.getSelectedView();

    @SuppressLint("ValidFragment")
    public  View3DFragment(String model){
        this.model = model;
    }

    public  View3DFragment(){}




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        View view = inflater.inflate(R.layout.activity_fullscreen, container, false);
//        listView = tab3DPlain.getListView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        setGlView(view.findViewById(R.id.gl_view));
//        selectedView = null;


        Button resetButton = (Button) view.findViewById(R.id.reset_button);
        View.OnClickListener resetButtonOnClickListener = this::Reset;
        resetButton.setOnClickListener(resetButtonOnClickListener);


        Button getBackButton = (Button) view.findViewById(R.id.exit_button);
        assert getBackButton != null;
        View.OnClickListener getBackButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().onBackPressed();

                System.out.println("getBackButton clicked");
                assert getFragmentManager() != null;
                assert getActivity().getFragmentManager() != null;

//                System.out.println(getActivity().getSupportFragmentManager()
//                        .popBackStackImmediate(null, 1));

//                System.out.println("getBackButton clicked after popBack");

//                getActivity().getSupportFragmentManager()
//                        .beginTransaction()
//                        .setReorderingAllowed(true)
//                        .replace(R.id.view_3d, new tab3DPlain(), null)
//                        .addToBackStack(null)
//                        .commit();
//
//                System.out.println("getBackButton clicked after replace and new view3d");

//                assert getParentFragmentManager() != null;
//                getParentFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.view_3d, new tab3DPlain(), tab3DPlain.class.getSimpleName())
//                        .commit();
//                Toast.makeText(getContext(), "Clicked BACK", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
        };
        getBackButton.setOnClickListener(getBackButtonClickListener);

//        listView.setItemChecked(-1, true);
    }



    public void setGlView(GLView v) {
        glView = v;
        glView.makeRenderer(model);
    }

    public void Reset(View v) {
        glView.Reset();
    }


}
