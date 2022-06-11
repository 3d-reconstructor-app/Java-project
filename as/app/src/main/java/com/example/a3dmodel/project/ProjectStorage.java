package com.example.a3dmodel.project;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.a3dmodel.App;
import com.example.a3dmodel.data.ProjectSnapshot;
import com.example.a3dmodel.exeption.ProjectException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ProjectStorage implements Serializable {
    private final TreeSet<Project> projects;
    private final Map<String, Project> nameToProject;
    private Project currentProject;

    private ProjectStorage() {
        projects = new TreeSet<>();
        nameToProject = new HashMap<>();
    }

    @NonNull
    public static ProjectStorage build() {
        ProjectStorage storage = new ProjectStorage();
        File resources = App.getContext().getFilesDir();
        for (File projectFile : resources.listFiles()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(projectFile))) {
                Project proj = (Project) in.readObject();
                storage.addProject(proj);
            }
            catch(IOException | ClassNotFoundException e) {
                System.out.println("Unable to load project : " + projectFile);
            }
        }
        return storage;
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
            return Project.create("Unnamed Project");
        }
        return projects.last();
    }

    public void addProject(Project project) {
        projects.add(project);
        nameToProject.put(project.getProjectName(), project);
    }

    public void createNewProject(String projectName) {
        Project newProject = Project.create(projectName);
        projects.add(newProject);
        nameToProject.put(projectName, newProject);
    }

    public void openExistingProject(String projectName) throws ProjectException {
        if (!nameToProject.containsKey(projectName)) {
            throw new ProjectException("Project doesn't exist");
        }
        currentProject = nameToProject.get(projectName);
    }

    public void saveProject() throws ProjectException {
        String currentProjectName = getCurrentProject().getProjectName();
        try (FileOutputStream fileOut = App.getContext().openFileOutput(currentProjectName, Context.MODE_PRIVATE); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(currentProject);
        }
        catch(IOException e) {
            throw new ProjectException("Couldn't write project file for " + currentProjectName);
        }
    }
}
