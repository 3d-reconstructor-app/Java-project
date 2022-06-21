package com.example.a3dmodel.data;

import com.example.a3dmodel.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProjectSnapshot {
    private final String projectName;
    private final LocalDateTime modTime;
    private final int projectIcon = R.drawable.icon_plus;

    public String getProjectName() {
        return projectName;
    }

    public String getModTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(modTime);
    }

    public ProjectSnapshot(String name, LocalDateTime time) {
        projectName = name;
        modTime = time;
    }

    public int getProjectIcon() {
        return projectIcon;
    }
}
