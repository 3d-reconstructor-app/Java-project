package com.example.a3dmodel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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

import java.util.List;


/* TODO @@@ANDREY
    this is main place, where you will write your code to tell AS what to do with this tab
    -- first of all you should decide the xml file structure for this tab -- "fragment_tab_main.xml"
    I've already made some structure there
    .
   TODO but not sure, that "View" button is necessary, so remove it if it is not needed
    .
    Recycle View is a place were all lines with projects will be shown
    for Recycle View you should create an Adapter class, to handle the information that lays inside it
    .
    I suggest this plan --- when user creates his first project, you should show a window, where he will type this project name
    and then save it and continue work as it should be
    .
    and then he will be able to click on "new project" and create a new one, and the previous will be saved somehow
    and displayed in the RecycleView
    .
    I would suggest to remember the date, time and maybe something else about every project and display it (this is easy part)

 */


/*
    ADVICE
    you can see the "tabPhoto.java", "GridAdapter" and "ImageData" as examples
 */


public class tabMainMenu extends Fragment {
    static private final ProjectStorage storage = App.getProjectStorage();
    static private RecyclerView recyclerView;
    static private List<ProjectSnapshot> projectsData = storage.getAllSnapshots();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_main_menu, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.projectsView);

        recyclerView = view.findViewById(R.id.projectsView);
        recyclerView.setAdapter(new ProjectSnapshotAdapter(projectsData));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void updateProjectListAndSendItToAdapter() {
        projectsData = storage.getAllSnapshots();
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
                App.getProjectStorage().createNewProject("Unnamed");
            }
        };
        createButton.setOnClickListener(createButtonOnClickListener);

        Button saveButton = (Button) view.findViewById(R.id.button_save_project);
        View.OnClickListener saveButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    App.getProjectStorage().saveProject();
                } catch (ProjectException e) {
                    Log.d("onClickSaveButton", e.getMessage());
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        };
        saveButton.setOnClickListener(saveButtonOnClickListener);
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
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerView.post(() -> layoutManager.scrollToPosition(MainActivity.currentPosition));
                }
            }
        });
    }
}
