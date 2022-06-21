package com.example.a3dmodel.project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.a3dmodel.App;
import com.example.a3dmodel.data.ProjectSnapshot;
import com.example.a3dmodel.exeption.AmbiguousProjectNameException;
import com.example.a3dmodel.exeption.ProjectException;
import com.example.a3dmodel.tabPhoto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.a3dmodel.tabMainMenu;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ProjectStorage implements Serializable {
    private final List<Project> projects;
    private final Map<String, Project> nameToProject;
    private Project currentProject;

    private ProjectStorage() {
        projects = new ArrayList<>();
        nameToProject = new HashMap<>();
    }

    private ProjectStorage(List<Project> projectList) throws ProjectException {
        this();
        projects.addAll(projectList);
        projectList.forEach(p -> nameToProject.put(p.getProjectName(), p));
    }

    private void setCurrentProject(Project proj) {
        setCurrentProject(proj, true);
    }

    private void setCurrentProject(Project proj, boolean notify) {
        currentProject = proj;
        if (notify) {
            tabMainMenu.updateCurrentProject(proj);
        }
    }

    @NonNull
    public static ProjectStorage build() throws ProjectException {
        File resources = App.getContext().getFilesDir();
        List<Project> projects = new ArrayList<>();
//        try {
//            Files.walk(resources.toPath()).forEach(f -> {
//                try {
//                    Files.delete(f.toAbsolutePath());
//                } catch (IOException e) {
//                    Log.d(TAG, "Unable to delete file " + f);
//                }
//            });
//        } catch (IOException e) {
//            Log.d(TAG, "Error while cleaning resource folder");
//        }
        for (File projectFile : resources.listFiles()) {
            if (projectFile.isDirectory()) {
                continue;
            }
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(projectFile))) {
                Project proj = Project.deserialize(in);
                projects.add(proj);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Unable to load project : " + projectFile);
            }
        }
        ProjectStorage storage = new ProjectStorage(projects);
        storage.setCurrentProject(storage.getLastOrCreate(), false);
        return storage;
    }

    public void loadProject() throws ProjectException {
        loadProject(getCurrentProject());
    }

    private void loadProject(Project project) throws ProjectException {
        openExistingProject(project.getProjectName());
    }

    public Project getCurrentProject() {
        assert(currentProject != null);
        return currentProject;
    }

    public List<ProjectSnapshot> getAllSnapshots() {
        return projects.stream().map(Project::makeSnapshot).collect(Collectors.toList());
    }

    public Project getLastOrCreate() {
        if (projects.isEmpty()) {
            Project sampleProject = createNewProject("Sample Project", false);
            try {
                saveProject(sampleProject, false);
            } catch (ProjectException e) {
                Log.d(TAG, "Error while saving sample project");
            }
        }
        return projects.get(projects.size() - 1);
    }

    public void addProject(Project project) {
        projects.add(project);
        nameToProject.put(project.getProjectName(), project);
        tabMainMenu.updateProjectListAndSendItToAdapter();
    }

    public void renameCurrentProject(String name) throws AmbiguousProjectNameException {
        if (nameToProject.containsKey(name)) {
            Log.d("ProjectStorage", "Project with given name already exists");
            throw new AmbiguousProjectNameException("Project with given name already exists");
        }
        nameToProject.remove(getCurrentProject().getProjectName());
        nameToProject.put(name, getCurrentProject());
        getCurrentProject().rename(name);
    }

    public Project createNewProject(String projectName) {
        return createNewProject(projectName, true);
    }

    private Project createNewProject(String projectName, boolean notifyAdapter) {
        Project newProject = Project.create(projectName);
        projects.add(newProject);
        nameToProject.put(projectName, newProject);
        if (notifyAdapter) {
            com.example.a3dmodel.tabMainMenu.updateProjectListAndSendItToAdapter();
        }
        return newProject;
    }

    public void openExistingProject(String projectName) throws ProjectException {
        if (!nameToProject.containsKey(projectName)) {
            throw new ProjectException("Project doesn't exist");
        }
        setCurrentProject(nameToProject.get(projectName));
        tabPhoto.loadImagesFromCurrentProject();
    }

    public void saveProject() throws ProjectException {
        saveProject(getCurrentProject(), true);
    }

    private void saveProject(@NonNull Project projectToSave, boolean notify) throws ProjectException {
        String projectName = projectToSave.getProjectName();
        try (FileOutputStream fileOut = App.getContext().openFileOutput(projectName, Context.MODE_PRIVATE); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            projectToSave.serialize(out);
        } catch (IOException e) {
            throw new ProjectException("Couldn't write project file for " + projectName);
        }
        if (notify) {
            com.example.a3dmodel.tabMainMenu.updateProjectListAndSendItToAdapter();
        }
    }

    public void deleteProjectByName(String projectName) throws IOException {
        Project projectToDelete = App.getProjectStorage().nameToProject.get(projectName);
        deleteProject(projectToDelete);
    }

    public void deleteProject(Project projectToDelete) throws IOException {
        projects.remove(projectToDelete);
        nameToProject.remove(projectToDelete.getProjectName());
        projectToDelete.clear();
        tabMainMenu.updateProjectListAndSendItToAdapter();
        setCurrentProject(getLastOrCreate());
        try {
            saveProject();
        }
        catch (ProjectException e) {
            Toast.makeText(App.getContext(), "Unable to save project", Toast.LENGTH_LONG).show();
        }
    }
}
