package com.example.a3dmodel;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3dmodel.adapter.ProjectSnapshotAdapter;
import com.example.a3dmodel.data.ProjectSnapshot;
import com.example.a3dmodel.exeption.ProjectException;
import com.example.a3dmodel.project.Project;
import com.example.a3dmodel.project.ProjectStorage;

import java.io.IOException;
import java.util.List;

public class TabMainMenu extends Fragment {
    private ProjectStorage storage = App.getProjectStorage();
    static private RecyclerView recyclerView;
    static private List<ProjectSnapshot> projectsData = App.getProjectStorage().getAllSnapshots();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_main_menu, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.projectsView);

        recyclerView = view.findViewById(R.id.projectsView);
        recyclerView.setAdapter(new ProjectSnapshotAdapter(projectsData));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        for (ProjectSnapshot snapshot : projectsData) {
            String currentProjectName = storage.getCurrentProject().getProjectName();
            if (snapshot.getProjectName().equals(currentProjectName)) {
                ProjectSnapshotAdapter adapter = (ProjectSnapshotAdapter) recyclerView.getAdapter();
                assert (adapter != null);
                adapter.findItemAndHighlight(currentProjectName);
            }
        }
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void updateProjectListAndSendItToAdapter() {
        List<ProjectSnapshot> snapshotList = App.getProjectStorage().getAllSnapshots();
        ProjectSnapshotAdapter.projects = snapshotList;
        TabMainMenu.projectsData = snapshotList;
        assert recyclerView.getAdapter() != null;

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button createButton = (Button) view.findViewById(R.id.button_create_new_project);
        View.OnClickListener createButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        };
        createButton.setOnClickListener(createButtonOnClickListener);

        Button saveButton = (Button) view.findViewById(R.id.button_save_project);
        View.OnClickListener saveButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    storage.saveProject();
                    Toast.makeText(getContext(), "Saved project " + storage.getCurrentProject().getProjectName(), Toast.LENGTH_SHORT).show();
                } catch (ProjectException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        };
        saveButton.setOnClickListener(saveButtonOnClickListener);
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
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerView.post(() -> layoutManager.scrollToPosition(MainActivity.currentPosition));
                }
            }
        });
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_save_project);

        final EditText projectName = dialog.findViewById(R.id.editTextProjectName);
        Button submitButton = dialog.findViewById(R.id.save_submit);

        submitButton.setOnClickListener((v) -> {
            String name = projectName.getText().toString();
            createNewProjectAndSave(name);
            dialog.dismiss();
            Toast.makeText(this.getContext(), "Project " + name + " is loaded", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void createNewProjectAndSave(String name) {
        try {
            storage.createNewProject(name);
            storage.openExistingProject(name);
            storage.saveProject();
        } catch (ProjectException e) {
            Toast.makeText(this.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((ProjectSnapshotAdapter) recyclerView.getAdapter()).getPosition();

        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.project_delete_option:
                String selectedProjectName = projectsData.get(position).getProjectName();
                try {
                    storage.deleteProjectByName(selectedProjectName);
                } catch (IOException | ProjectException e) {
                    Toast.makeText(this.getContext(), "Couldn't delete project " + selectedProjectName, Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    public static void updateCurrentProject(@NonNull Project proj) {
        ProjectSnapshotAdapter adapter = (ProjectSnapshotAdapter) recyclerView.getAdapter();
        assert (adapter != null);
        adapter.findItemAndHighlight(proj.getProjectName());
    }
}
