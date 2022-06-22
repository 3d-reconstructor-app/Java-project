package com.example.a3dmodel;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3dmodel.adapter.ModelAdapter;
import com.example.a3dmodel.adapter.ProjectSnapshotAdapter;
import com.example.a3dmodel.data.ModelData;
import com.example.a3dmodel.data.ProjectSnapshot;
import com.example.a3dmodel.exeption.ProjectException;
import com.example.a3dmodel.visualisation.GLView;
import com.example.a3dmodel.visualisation.fragments.View3DFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tab3DPlain extends Fragment {
    static private RecyclerView recyclerView;
    static private List<ModelData> modelsList = App.getProjectStorage().getCurrentProject().getModels();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_3d_plane, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.modelsView);

        recyclerView.setAdapter(new ModelAdapter(modelsList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        for (ProjectSnapshot snapshot : projectsData) {
//            String currentProjectName = storage.getCurrentProject().getProjectName();
//            if (snapshot.getProjectName().equals(currentProjectName)) {
//                ProjectSnapshotAdapter adapter = (ProjectSnapshotAdapter) recyclerView.getAdapter();
//                assert(adapter != null);
//                adapter.findItemAndHighlight(currentProjectName);
//            }
//        }
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void updateModelListAndSendItToAdapter() {
        List<ModelData> modelList = App.getProjectStorage().getCurrentProject().getModels();
        ModelAdapter.models = modelList;
        modelsList = modelList;
        assert recyclerView.getAdapter() != null;
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollToPosition();
        registerForContextMenu(recyclerView);
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
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerView.post(() -> layoutManager.scrollToPosition(MainActivity.currentPosition));
                }
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((ModelAdapter) recyclerView.getAdapter()).getPosition();

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.model_option_1:
                String selectedModelName = modelsList.get(position).getModelName();
//                projectsData.remove(position);
//                ProjectSnapshotAdapter.projects.remove(position);
//                ((ProjectSnapshotAdapter)recyclerView.getAdapter()).notifyItemRemoved(position);
//                ((ProjectSnapshotAdapter)recyclerView.getAdapter()).notifyItemRangeChanged(position, projectsData.size());

                assert getFragmentManager() != null;
                getFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_3d, new View3DFragment(getFragmentManager(), selectedModelName), View3DFragment.class.getSimpleName())
                        .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null)
                        .commit();
//                    storage.deleteProjectByName(selectedProjectName);
                break;
        }
        return super.onContextItemSelected(item);
    }


//    private final List<String> models = new ArrayList<>();
//    private static ListView listView;
//    private static TextView selectedView;
//
//    public static TextView getSelectedView() {
//        return selectedView;
//    }
//
//    public static ListView getListView() {
//        return listView;
//    }
//
//
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view =  inflater.inflate(R.layout.fragment_tab_3d_plane, container, false);
//        return view;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        listView = view.findViewById(R.id.model_list);
//        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        listView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_text_view, models));
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                if (selectedView != null && selectedView.equals((TextView) view)) {
//                    listView.setItemChecked(-1, true);
//                    selectedView = null;
//                    return;
//                }
//                selectedView = (TextView) view;
//            }
//        });
//
//        Button drawButton = (Button) view.findViewById(R.id.draw_button);
//        View.OnClickListener drawButtonOnClickListener = v -> {
//            if (selectedView == null) {
//                Toast.makeText(getContext(), "File has not been selected, please select model to draw it", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//
//
//            assert getActivity() != null;
////            getActivity().setContentView(R.layout.activity_fullscreen);
//            assert getFragmentManager() != null;
//            getFragmentManager()
//                    .beginTransaction()
//                    .setReorderingAllowed(true)
//                    .replace(R.id.fragment_3d, new View3DFragment(getFragmentManager()), View3DFragment.class.getSimpleName())
//                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                    .addToBackStack(null)
//                    .commit();
//
//
//
////            setGlView((GLView) getActivity().findViewById(R.id.gl_view));
////            selectedView = null;
////
////            View view_3d = (View) getActivity().findViewById(R.id.view_3d);
////
////            resetButton = (Button) view_3d.findViewById(R.id.reset_button);
////            View.OnClickListener resetButtonOnClickListener = this::Reset;
////
////            resetButton.setOnClickListener(resetButtonOnClickListener);
////            listView.setItemChecked(-1, true);
//        };
//
//        drawButton.setOnClickListener(drawButtonOnClickListener);
//
//        try {
//            //TODO add real models
//            addModels();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        listView = view.findViewById(R.id.model_list);
////        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
////        listView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.list_text_view, models));
////        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            public void onItemClick(AdapterView<?> parent, View view,
////                                    int position, long id) {
////                if (selectedView != null && selectedView.equals((TextView) view)) {
////                    listView.setItemChecked(-1, true);
////                    selectedView = null;
////                    return;
////                }
////                selectedView = (TextView) view;
////            }
////        });
//
//        //TODO drop selection in tabs
////        listView.setItemChecked(-1, true);
//    }
//
////
//    public void setGlView(GLView v) {
//        glView = v;
//        glView.makeRenderer(selectedView.getText().toString());
//    }
//
//    public void Reset(View v) {
//        glView.Reset();
//    }
////
//    public void addModels() throws IOException {
//        //TODO get real models
//        models.clear();
//        models.addAll(Arrays.asList(getResources().getAssets().list("models")));
//    }

}
