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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// TODO @@@ANDREY
public class Project implements Comparable<Project>, Serializable {
    private String projectName;
    private LocalDateTime modTime;
    private int projectIcon;
    private List<SerialBitmap> images;

    private Project(String name) {
        projectName = name;
        modTime = LocalDateTime.now();
        projectIcon = R.drawable.defaulticon;
//        images = new ArrayList<>();
    }

    public ProjectSnapshot makeSnapshot() {
        return new ProjectSnapshot(projectName, modTime);
    }

    public String getProjectName() {
        return projectName;
    }

    public void rename(String name) {
        projectName = name;
    }

    public void addImages(@NonNull List<Bitmap> imgs) {
        List<SerialBitmap> newImgs = imgs.stream().map(SerialBitmap::new).collect(Collectors.toList());
        images.addAll(newImgs);
    }

    @NonNull
    public static Project create(String name) {
        Project proj = new Project(name);
        proj.images = new ArrayList<>();
        return proj;
    }
    @Override
    public int compareTo(@NonNull Project other) {
        return modTime.compareTo(other.modTime);
    }
}
