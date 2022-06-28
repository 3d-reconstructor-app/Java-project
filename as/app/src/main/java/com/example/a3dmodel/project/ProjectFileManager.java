package com.example.a3dmodel.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.a3dmodel.App;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectFileManager {
    public static final String MODELS = "models";
    public static final File APP_ROOT_DIR = App.getContext().getFilesDir();
    public static final Path APP_ROOT_PATH = App.getContext().getFilesDir().toPath();
    public static final Path MODELS_PATH = APP_ROOT_PATH.resolve(MODELS);

    @NonNull
    public static Path getRootPath() {
        return APP_ROOT_PATH;
    }

    public static Path getProjectPath(String projectName) {
        return APP_ROOT_PATH.resolve(projectName);
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
        try {
            Files.createDirectory(APP_ROOT_PATH);
        }
        catch(IOException e) {
            return false;
        }
        return true;
    }

    @NonNull
    @Contract(pure = true)
    public static String getProjectDataDirName(String projectName) {
        return projectName + "Data";
    }

    public static Path getProjectDataDirPath(String projectName) {
        return APP_ROOT_PATH.resolve(getProjectDataDirName(projectName));
    }

    @NonNull
    @Contract("_ -> new")
    public static File getProjectDataFile(String projectName) {
        return new File(APP_ROOT_DIR, ProjectFileManager.getProjectDataDirName(projectName));
    }

    public static Path getModelsDirPath() {
        return MODELS_PATH;
    }

    @NonNull
    @Contract("_ -> new")
    public static File getProjectModelsDir(String projectName) {
        return new File(MODELS_PATH.resolve(projectName).toString());
    }

    public static Path getProjectModelsDirPath(String projectName) {
        return MODELS_PATH.resolve(projectName);
    }

    public static void clearAndDeleteDir(@NonNull File directory) throws IOException {
        if (directory.exists()) {
            FileUtils.cleanDirectory(directory);
            FileUtils.deleteDirectory(directory);
        }
    }
}
