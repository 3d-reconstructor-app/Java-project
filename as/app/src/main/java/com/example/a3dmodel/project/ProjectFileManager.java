package com.example.a3dmodel.project;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.example.a3dmodel.App;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectFileManager {
    public static final String STORAGE_FILE = "Storage.data";
    public static final String MAIN_FOLDER = "3dModelApp";
    @NonNull
    public static Path getRootPath() {
        return App.getContext().getFilesDir().toPath();
    }

    public static Path getProjectPath(String projectName) {
        return getRootPath().resolve(projectName);
    }

    public static Path getImagesDirPath(String projectName) {
        return getProjectPath(projectName).resolve("images");
    }

    public static Path getImagePath(String projectName, int imageId) {
        return getImagesDirPath(projectName).resolve("img" + imageId + ".jpeg");
    }

    @NonNull
    @Contract("_, _ -> new")
    public static Bitmap getImage(String projectName, int imageId) {
        return BitmapFactory.decodeFile(getImagePath(projectName, imageId).toString());
    }

    public static boolean createRootPath() {
//        File f = new File(getRootPath().toString());
        try {
            System.out.println("Creating resource folder in : " + getRootPath());
            Files.createDirectory(getRootPath());
            System.out.println("Checking if created : " + getRootPath().toFile().exists());
        }
        catch(IOException e) {
            System.out.println("Error while creating directory");
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
