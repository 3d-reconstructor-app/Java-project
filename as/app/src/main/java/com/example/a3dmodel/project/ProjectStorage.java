package com.example.a3dmodel.project;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.a3dmodel.App;
import com.example.a3dmodel.TabPhoto;
import com.example.a3dmodel.data.ProjectSnapshot;
import com.example.a3dmodel.exeption.AmbiguousProjectNameException;
import com.example.a3dmodel.exeption.ProjectException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @NonNull
    public static ProjectStorage build() throws ProjectException {
        File resources = App.getContext().getFilesDir();
        List<Project> projects = new ArrayList<>();
        for (File projectFile : resources.listFiles()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(projectFile))) {
                Project proj = (Project) in.readObject();
                projects.add(proj);
            }
            catch(IOException | ClassNotFoundException e) {
                System.out.println("Unable to load project : " + projectFile);
            }
        }
        ProjectStorage storage = new ProjectStorage(projects);
        storage.currentProject = storage.getLastOrCreate();
        return new ProjectStorage(projects);
    }

    public void loadProject() throws ProjectException {
        loadProject(getCurrentProject());
    }

    private void loadProject(Project project) throws ProjectException {
        openExistingProject(project.getProjectName());
    }

    public Project getCurrentProject() {
        if (currentProject == null) {
            currentProject = getLastOrCreate();
        }
        return currentProject;
    }

    public List<ProjectSnapshot> getAllSnapshots() {
        return projects.stream().map(Project::makeSnapshot).collect(Collectors.toList());
    }

    public Project getLastOrCreate() {
        if (projects.isEmpty()) {
            return createNewProject("Unnamed Project", false);
        }
        return projects.get(projects.size() - 1);
    }

    public void addProject(Project project) {
        projects.add(project);
        nameToProject.put(project.getProjectName(), project);
        com.example.a3dmodel.tabMainMenu.updateProjectListAndSendItToAdapter();
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
        currentProject = nameToProject.get(projectName);
        TabPhoto.loadImagesFromCurrentProject();
    }

    public void saveProject() throws ProjectException {
        String currentProjectName = getCurrentProject().getProjectName();
        try (FileOutputStream fileOut = App.getContext().openFileOutput(currentProjectName, Context.MODE_PRIVATE); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(currentProject);
        }
        catch(IOException e) {
            throw new ProjectException("Couldn't write project file for " + currentProjectName);
        }
        com.example.a3dmodel.tabMainMenu.updateProjectListAndSendItToAdapter();
    }
}
