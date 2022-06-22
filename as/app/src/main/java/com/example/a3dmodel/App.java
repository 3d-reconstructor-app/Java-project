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
    private static Application application;
    private static Context context;
    private static ProjectStorage projectStorage = null;

    private void initializeProjectStorage() {
        try {
            projectStorage = ProjectStorage.build();
        }
        catch(ProjectException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = getApplicationContext();
        initializeProjectStorage();
    }

}
