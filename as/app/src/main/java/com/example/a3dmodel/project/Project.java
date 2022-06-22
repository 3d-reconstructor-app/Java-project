package com.example.a3dmodel.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3dmodel.App;
import com.example.a3dmodel.TabPhoto;
import com.example.a3dmodel.data.ImageData;
import com.example.a3dmodel.data.ModelData;
import com.example.a3dmodel.data.ProjectSnapshot;
import com.example.a3dmodel.R;
import com.example.a3dmodel.exeption.ProjectException;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private transient List<Bitmap> images;
    private transient List<ModelData> models;

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
//        List<SerialBitmap> newImgs = imgs.stream().map(SerialBitmap::new).collect(Collectors.toList());
        images.addAll(imgs);
    }

    public void addAndSaveModel(@NonNull File model) throws ProjectException {
        models.add(new ModelData(model.getName()));
        File modelFile = new File(ProjectFileManager.getProjectModelsDirPath(projectName).toString());
        try {
            FileUtils.copyFile(model, modelFile);
        }
        catch (IOException e) {
            throw new ProjectException("Couldn't write model for " + projectName);
        }
    }

    public void deleteImages(@NonNull List<Bitmap> imgs) {
        images.removeAll(imgs);
    }

    public List<ImageData> getImageData() {
        return images.stream().map(ImageData::new).collect(Collectors.toList());
    }

    public List<Bitmap> getImages() {
        return images;
    }

    @NonNull
    public static Project create(String name) {
        Project proj = new Project(name);
        proj.images = new ArrayList<>();

        return proj;
    }
    @Override
    public int compareTo(@NonNull Project other) {
        return projectName.compareTo(other.projectName);
    }

    public void serialize(@NonNull ObjectOutputStream out) throws ProjectException, IOException {
        out.writeObject(this);
        Path projectDataDirPath = ProjectFileManager.getProjectDataDirPath(projectName);
        File projectDataDir = new File(projectDataDirPath.toString());
        Path projectModelsDirPath = ProjectFileManager.getProjectModelsDirPath(projectName);
        File projectModelsDir = new File(projectModelsDirPath.toString());
        ProjectFileManager.clearAndDeleteDir(projectDataDir);
        ProjectFileManager.clearAndDeleteDir(projectModelsDir);
        projectDataDir.mkdir();
        projectModelsDir.mkdir();
        images.clear();
        images.addAll(TabPhoto.imageDataList.stream().map(ImageData::getImageBitmap).collect(Collectors.toList()));
        System.out.println("images size = " + images.size());
        for (int i = 0; i < images.size(); i++) {
            compressToJpegAndWrite(i, projectDataDir);
        }
    }

    private void compressToJpegAndWrite(int imageIndex, File projectDataDir) throws ProjectException {
        final String generatedImageName = "img" + imageIndex + ".jpeg";
        try (FileOutputStream fout = new FileOutputStream(new File(projectDataDir, generatedImageName)); ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            images.get(imageIndex).compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            fout.write(stream.toByteArray());
        }
        catch(IOException e) {
            throw new ProjectException("Error while compressing image in " + projectName);
        }
    }

    @NonNull
    public static Project deserialize(@NonNull ObjectInputStream in) throws IOException, ClassNotFoundException {
        Project proj = (Project) in.readObject();
        File projectDataFile = ProjectFileManager.getProjectDataFile(proj.getProjectName());
        File projectModelsFile = ProjectFileManager.getProjectModelsDir(proj.getProjectName());
        proj.images = new ArrayList<>();
        Files.list(projectDataFile.toPath()).forEach(imgFile -> proj.images.add(BitmapFactory.decodeFile(imgFile.toString())));
        proj.models = new ArrayList<>();
        proj.models = Files.list(projectModelsFile.toPath()).map(modelPath -> new ModelData(modelPath.getFileName().toString())).collect(Collectors.toList());
        return proj;
    }

    public void clear() throws IOException {
        File projectDataFile = ProjectFileManager.getProjectDataFile(getProjectName());
        FileUtils.cleanDirectory(projectDataFile);
        Files.delete(projectDataFile.toPath());
        Files.delete(new File(App.getContext().getFilesDir(), getProjectName()).toPath());
    }

    public List<ModelData> getModels() {
        return models;
    }
}
