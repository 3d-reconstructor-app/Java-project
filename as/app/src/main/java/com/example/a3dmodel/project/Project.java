package com.example.a3dmodel.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3dmodel.App;
import com.example.a3dmodel.TabPhoto;
import com.example.a3dmodel.tab3DPlain;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Project implements Comparable<Project>, Serializable {
    private String projectName;
    private LocalDateTime modTime;
    private int projectIcon;
    private List<ModelData> models = new ArrayList<>();
    private transient List<Bitmap> images;

    private Project(String name) {
        projectName = name;
        modTime = LocalDateTime.now();
        projectIcon = R.drawable.defaulticon;
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
        images.addAll(imgs);
    }

    public void addAndSaveModel(@NonNull File model, Bitmap modelIcon) throws ProjectException {
        models.add(new ModelData(model.getName(), modelIcon));
        File modelFile = new File(ProjectFileManager.getProjectModelsDirPath(projectName).toString() + '/' + model.getName());
        try {
            FileUtils.copyFile(model, modelFile);
            FileUtils.delete(model);
        }
        catch (IOException e) {
            e.printStackTrace();
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
        projectDataDir.mkdir();
        if (!ProjectFileManager.getModelsDirPath().toFile().exists()) {
            ProjectFileManager.getModelsDirPath().toFile().mkdir();
        }
        if (!projectModelsDir.exists()) {
            projectModelsDir.mkdir();
        }
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


    @Nullable
    public static Project deserialize(@NonNull ObjectInputStream in) throws IOException, ClassNotFoundException {
        Project proj;
        try {
            proj = (Project) in.readObject();
        } catch (ClassCastException e) {
            return null;
        }
        File projectDataFile = ProjectFileManager.getProjectDataFile(proj.getProjectName());
        proj.images = new ArrayList<>();
        Files.list(projectDataFile.toPath()).forEach(imgFile -> proj.images.add(BitmapFactory.decodeFile(imgFile.toString())));
        System.out.println("wtf " + proj.models);
        return proj;
    }

    public void clear() throws IOException {
        File projectDataFile = ProjectFileManager.getProjectDataFile(getProjectName());
        System.out.println(projectDataFile);
        FileUtils.deleteDirectory(projectDataFile);
        Files.delete(new File(App.getContext().getFilesDir(), getProjectName()).toPath());
        FileUtils.deleteDirectory(ProjectFileManager.getProjectModelsDir(getProjectName()));
        images.clear();
        models.clear();
        TabPhoto.updateAllImagesAndSendItToAdapter();
        tab3DPlain.updateModelListAndSendItToAdapter();
    }

    public List<ModelData> getModels() {
        return models;
    }
}
