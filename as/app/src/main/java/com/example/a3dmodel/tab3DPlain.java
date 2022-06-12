package com.example.a3dmodel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a3dmodel.visualisation.GLView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tab3DPlain extends Fragment {
    Button drawButton;
    Button resetButton;
    GLView glView;
    List<String> models = new ArrayList<>();
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_3d_plane, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drawButton = (Button) view.findViewById(R.id.draw_button);
        View.OnClickListener drawButtonOnClickListener = v -> {
            System.out.println("wtf");
            assert getActivity() != null;
            getActivity().setContentView(R.layout.activity_fullscreen);

            setGlView((GLView) getActivity().findViewById(R.id.gl_view));

            View view_3d = (View) getActivity().findViewById(R.id.view_3d);

            resetButton = (Button) view_3d.findViewById(R.id.reset_button);
            View.OnClickListener resetButtonOnClickListener = this::Reset;
            resetButton.setOnClickListener(resetButtonOnClickListener);
        };
        drawButton.setOnClickListener(drawButtonOnClickListener);
        try {
            listModels();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listView = view.findViewById(R.id.model_list);
        listView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_text_view, models));
    }

    public void setGlView(GLView v) {
        glView = v;
    }

    public void Reset(View v) {
        glView.Reset();
    }

    private void listModels() throws IOException {
        //TODO get real models
        models.clear();
        models.addAll(Arrays.asList(getResources().getAssets().list("models")));
    }
}
