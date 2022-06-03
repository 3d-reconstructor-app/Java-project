package com.example.a3dmodel.data;

import android.graphics.Bitmap;

import com.example.a3dmodel.R;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// TODO @@@ANDREY
public class ProjectSnapshotData {
    private final String projectName;
    private final LocalDateTime creationTime;
    private final Bitmap projectIcon;

    ProjectSnapshotData(String name, LocalDateTime time, Bitmap icon) {
        projectName = name;
        creationTime = time;
        projectIcon = icon;
    }

    public Bitmap getProjectIcon() {
        return projectIcon;
    }

    public String getCreationTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(creationTime);
    }

    public String getProjectName() {
        return projectName;
    }
}
