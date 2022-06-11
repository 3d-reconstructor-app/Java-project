package com.example.a3dmodel.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3dmodel.data.ProjectSnapshot;
import com.example.a3dmodel.R;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// TODO @@@ANDREY
public class Project implements Comparable<Project>, Serializable {
    private String projectName;
    private LocalDateTime modTime;
    private int projectIcon;
    private List<Bitmap> images;

    private Project(String name) {
        projectName = name;
        modTime = LocalDateTime.now();
        projectIcon = R.drawable.defaulticon;
    }

    public Project(String name, LocalDateTime time, int icon) {
        projectName = name;
        modTime = time;
        projectIcon = icon;
    }

    @Nullable
    public static Project getProject(String projectName) {
        if (!exists(projectName)) {
            return null;
        }
        Project project = new Project(projectName);
        project.loadImages();
        project.setModTime();
        return project;
    }

    public static Project create(String name) {
        if (exists(name)) {
            System.out.println("Project already exists");
            // TODO : throw exception here
            return null;
        }
        return new Project(name);
    }

    public static Project loadLastOrCreate() {
        List<ProjectSnapshot> projects = getAllProjects();
        if (projects != null && !projects.isEmpty()) {
            return getProject(projects.get(0).getProjectName());
        }
        else {
            return create("Unnamed");
        }
    }

    public static List<ProjectSnapshot> getAllProjects() {
        try {
            System.out.println("Loading project history...");
            System.out.println("Resource folder exists : " + Files.exists(ProjectFileManager.getRootPath()));
            List<Path> pathsList = Files.list(ProjectFileManager.getRootPath())
                    .collect(Collectors.toList());

            Comparator<Path> cmp = Comparator.comparing(path -> {
                try {
                    return Files.getLastModifiedTime(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });

            return pathsList.stream()
                    .sorted(cmp)
                    .map(p -> makeSnapshot(p.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error while getting project history");
            System.out.println(e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    private void loadImages() {
        Path imagesDir = ProjectFileManager.getImagesDirPath(projectName);
        try {
            images = Files.walk(imagesDir).map(imgPath -> BitmapFactory.decodeFile(imgPath.toString())).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error while loading resources from project");
        }
    }

    private void setModTime() {
        try {
            FileTime fileTime = Files.getLastModifiedTime(ProjectFileManager.getProjectPath(projectName));
            modTime = LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            System.out.println("Error while setting project modification time");
        }
    }

    public int getProjectIcon() {
        return projectIcon;
    }

    public static boolean exists(String projectName) {
        return Files.exists(ProjectFileManager.getProjectPath(projectName));
    }

    public void setProjectName(String name) {
        File projectDir = ProjectFileManager.getProjectPath(projectName).toFile();
        if (projectDir.exists()) {
            if (!projectDir.renameTo(ProjectFileManager.getProjectPath(name).toFile())) {
                System.out.println("Project already exists");
                return;
            }
        }
        projectName = name;
    }

    public String serializeModTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(modTime);
    }

    public String getProjectName() {
        return projectName;
    }

    private static LocalDateTime getModTime(String projectName) {
        try {
            FileTime fileTime = Files.getLastModifiedTime(ProjectFileManager.getProjectPath(projectName));
            return LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        } catch (IOException e) {
            System.out.println("Error while checking project modification time");
            return null;
        }
    }

    public static ProjectSnapshot makeSnapshot(String projectName) {
        return new ProjectSnapshot(projectName, getModTime(projectName));
    }

    @Override
    public int compareTo(@NonNull Project other) {
        return modTime.compareTo(other.modTime);
    }
}
