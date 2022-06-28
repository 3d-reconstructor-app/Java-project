package com.example.a3dmodel;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3dmodel.adapter.ModelAdapter;
import com.example.a3dmodel.data.ModelData;
import com.example.a3dmodel.visualisation.fragments.View3DFragment;

import java.util.ArrayList;
import java.util.List;

public class tab3DPlain extends Fragment {
    static private RecyclerView recyclerView;
    static private List<ModelData> modelsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_3d_plane, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.modelsView);
        modelsList.clear();
        modelsList.addAll(App.getProjectStorage().getCurrentProject().getModels());
        recyclerView.setAdapter(new ModelAdapter(modelsList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void updateModelListAndSendItToAdapter() {
        List<ModelData> modelList = App.getProjectStorage().getCurrentProject().getModels();
        modelsList.clear();
        modelsList.addAll(modelList);
        assert recyclerView.getAdapter() != null;
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(recyclerView);

        scrollToPosition();
    }

    private void scrollToPosition() {
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                recyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                assert layoutManager != null;
                View viewAtPosition = layoutManager.findViewByPosition(MainActivity.currentPosition);
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerView.post(() -> layoutManager.scrollToPosition(MainActivity.currentPosition));
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        System.out.println("onContextItemSelected in tab3dPlain");

        int position = -1;
        try {
            position = ((ModelAdapter) recyclerView.getAdapter()).getPosition();

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        // TODO add case for model.delete.option

        switch (item.getItemId()) {
            case R.id.model_draw_option:
                String selectedModelName = modelsList.get(position).getModelName();

                assert getActivity() != null;
                assert getFragmentManager() != null;
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_3d_constraint, new View3DFragment(selectedModelName), View3DFragment.class.getSimpleName())
                        .addToBackStack(tab3DPlain.class.getSimpleName())
                        .commit();
                break;
        }
        return super.onContextItemSelected(item);
    }
}
