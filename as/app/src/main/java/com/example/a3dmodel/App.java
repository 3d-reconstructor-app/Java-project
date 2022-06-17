package com.example.a3dmodel;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.a3dmodel.exeption.ProjectException;
import com.example.a3dmodel.project.Project;
import com.example.a3dmodel.project.ProjectFileManager;
import com.example.a3dmodel.project.ProjectStorage;

import java.io.IOException;
import java.nio.file.Files;

public class App extends android.app.Application {
    static {
////        System.out.println("Resources located : " + ProjectFileManager.getRootPath());
//        System.out.println("StorageDir : " + Environment.getStorageDirectory());
//        System.out.println("StorageDir is accessible : " + Environment.getStorageDirectory().exists());
//        System.out.println("Creating resource dir...");
//        ProjectFileManager.createRootPath();
//        System.out.println("Resources folder exists : " + Files.exists(ProjectFileManager.getRootPath()));
//        System.out.println("Application initialized");

    }
    private static Application application;
//    private Project currentProject;
    private static ProjectStorage projectStorage;
//    public Project getCurrentProject() {
//        return currentProject;
//    }

    public App() {

    }
    public static ProjectStorage getProjectStorage() {
        return projectStorage;
    }

    public static void setProjectStorage(ProjectStorage ps) {
        projectStorage = ps;
    }

    public static Application getApplication() {
        return application;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    public static void clear() {
        try {
            Files.walk(getContext().getFilesDir().toPath()).forEach(f -> {try {
                Files.delete(f);
            }
            catch(IOException e) {
                Log.d("Cleaning", "IOException!");
            }});
        }
        catch(IOException e) {
            Log.d("Cleaning", "IOException!");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        try {
            projectStorage = ProjectStorage.build();
        }
        catch(ProjectException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

//    public void setCurrentProject(Project currentProject) {
//        this.currentProject = currentProject;
//    }
}
