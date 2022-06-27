package com.example.a3dmodel;

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Contract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.a3dmodel.adapter.GridAdapter;
import com.example.a3dmodel.data.ImageData;
import com.example.a3dmodel.exeption.AppException;
import com.example.a3dmodel.exeption.ProjectException;
import com.example.a3dmodel.exeption.TabPhotoException;
import com.example.a3dmodel.project.ProjectFileManager;
import com.example.a3dmodel.project.ProjectStorage;

import static com.example.a3dmodel.MainActivity.bitmapArrayList;

public class TabPhoto extends Fragment {
    static public RecyclerView recyclerView;
    static public List<ImageData> imageDataList = new ArrayList<>();
    private FrameLayout frameLayout;
    private Path outputDirModels;
    private Path cacheTmpDirectory;
    public static final int PERMISSION_REQUEST_CODE = 100;
    public static final int CAMERA_PIC_REQUEST = 1888;
    public static final int GALLERY_PIC_REQUEST = 1777;
    private static final int lengthOfRandomFileJPEGName = 10;

    public final FrameLayout getFrameLayout() {
        return frameLayout;
    }

    private void checkTextSeenStatus(View view) {
        if (TabPhoto.imageDataList.size() != 0) {
            TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
            textView.setVisibility(View.GONE);
        } else {
            TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void initializePathsForDirectory() {
        Path dirPath = App.getContext().getFilesDir().toPath();

        this.cacheTmpDirectory = dirPath.resolve("tmp");
        this.outputDirModels = dirPath.resolve("models");

        System.out.println(cacheTmpDirectory.toString());
        System.out.println(outputDirModels.toString());


        File cacheTmpDirectoryFile = new File(cacheTmpDirectory.toString());
        File outputDirModelsFile = new File(outputDirModels.toString());

        System.out.println(cacheTmpDirectoryFile.isDirectory());
        System.out.println(outputDirModelsFile.isDirectory());

        try {
            boolean res = true;
            if (!cacheTmpDirectoryFile.isDirectory()) {
                if (!cacheTmpDirectoryFile.mkdir()) {
                    throw new TabPhotoException("could not create directory for cacheTmpDirectory");

                }
            }
            if (!outputDirModelsFile.isDirectory()) {
                if (!outputDirModelsFile.mkdir()) {
                    throw new TabPhotoException("could not create directory for outputDirModels");
                }
            }

        } catch (TabPhotoException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_photos, container, false);
        assert getActivity() != null;
        frameLayout = view.findViewById(R.id.fragment_photo);
        recyclerView = view.findViewById(R.id.fragment_photo_grid);

        recyclerView.setAdapter(new GridAdapter(imageDataList, this));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        checkTextSeenStatus(view);

        prepareTransitions();

        initializePathsForDirectory();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void updateImageBitmapListAndSendItToTheAdapter() {
        if (bitmapArrayList.isEmpty()) {
            return;
        }
        Bitmap lastPhotoBitmap = bitmapArrayList.get(bitmapArrayList.size() - 1);
        assert recyclerView.getAdapter() != null;
        imageDataList.add(new ImageData(lastPhotoBitmap));
        recyclerView.getAdapter().notifyDataSetChanged();
        App.getProjectStorage().getCurrentProject().addImages(List.of(lastPhotoBitmap));
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void updateAllImagesAndSendItToAdapter() {
        imageDataList.clear();
        bitmapArrayList.forEach(i -> imageDataList.add(new ImageData(i)));
        assert recyclerView.getAdapter() != null;
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkTextSeenStatus(view);

        try {
            Button cameraButton = (Button) view.findViewById(R.id.button_camera);
            Button galleryButton = (Button) view.findViewById(R.id.button_gallery);
            Button deleteButton = (Button) view.findViewById(R.id.button_delete);
            Button buildButton = (Button) view.findViewById(R.id.button_build);


            try {
                App.getProjectStorage().loadProject();
            } catch (ProjectException e) {
                Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            // TODO need to store photo directly in the system and save path for them
            View.OnClickListener cameraButtonOnClickListener = new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    new Thread(() -> {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        assert getActivity() != null;
                        getActivity().startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                    }).start();

                }
            };
            cameraButton.setOnClickListener(cameraButtonOnClickListener);


            View.OnClickListener galleryButtonOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(() -> {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        assert getActivity() != null;
                        getActivity().startActivityForResult(galleryIntent, GALLERY_PIC_REQUEST);

                    }).start();
                }

            };
            galleryButton.setOnClickListener(galleryButtonOnClickListener);


            View.OnClickListener buildButtonOnClickListener = new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    try {
                        int filesCount = GridAdapter.selectedImageDataItems.size();
                        List<Bitmap> bitmapListOfSelectedImages = new ArrayList<>();

                        for (int i = 0; i < filesCount; i++) {
                            bitmapListOfSelectedImages.add(GridAdapter.selectedImageDataItems.get(i).getImageBitmap());
                        }

                        String state = Environment.getExternalStorageState();
                        try {
                            if (Environment.MEDIA_MOUNTED.equals(state)) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    if (checkPermission()) {
                                        sendSelectedPhotosToServerToBuild3DModel(bitmapListOfSelectedImages, filesCount);
                                    } else {
                                        requestPermission();
                                    }
                                } else {
                                    sendSelectedPhotosToServerToBuild3DModel(bitmapListOfSelectedImages, filesCount);

                                }
                            }
                        } catch (TabPhotoException | ProjectException | AppException | IOException e) {
                            e.printStackTrace();
                        }


                        assert getActivity() != null;
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "The construction of the 3D model has begun", Toast.LENGTH_SHORT).show());

                    } finally {
                        /*
                         * stop highlight selected photos
                         */
                        GridAdapter.selectedImageDataItems.clear();
                        GridAdapter.isSelectMode = false;
                        for (View imageView : GridAdapter.selectedImagesViewWithBackgroundColor) {
                            imageView.setBackgroundColor(Color.TRANSPARENT);
                        }
                        GridAdapter.selectedImagesViewWithBackgroundColor.clear();
                        assert recyclerView.getAdapter() != null;

                        assert getActivity() != null;

                        ((GridAdapter) recyclerView.getAdapter()).checkButtonsVisibility();


                        recyclerView.getAdapter().notifyDataSetChanged();

                        makeTwoButtonsHide(buildButton, deleteButton);

                        if (TabPhoto.imageDataList.size() != 0) {
                            TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
                            textView.setVisibility(View.GONE);
                        } else {
                            TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
                            textView.setVisibility(View.VISIBLE);
                        }

                    }
                }

            };

            buildButton.setOnClickListener(buildButtonOnClickListener);


            View.OnClickListener deleteButtonOnClickListener = new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    new Thread(() -> {
                        ArrayList<ImageData> selectedImages = new ArrayList<>(GridAdapter.selectedImageDataItems);
                        assert getActivity() != null;
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Deleted " + selectedImages + " images", Toast.LENGTH_SHORT).show());
                        GridAdapter.imageDataList.removeAll(selectedImages);
                        App.getProjectStorage().getCurrentProject().deleteImages(selectedImages.stream().map(ImageData::getImageBitmap).collect(Collectors.toList()));
                        GridAdapter.selectedImageDataItems.clear();
                        GridAdapter.isSelectMode = false;
                        assert getActivity() != null;

                        getActivity().runOnUiThread(() -> {
                            for (View imageView : GridAdapter.selectedImagesViewWithBackgroundColor) {
                                imageView.setBackgroundColor(Color.TRANSPARENT);
                            }
                            GridAdapter.selectedImagesViewWithBackgroundColor.clear();

                        });

                        assert recyclerView.getAdapter() != null;

                        assert getActivity() != null;

                        getActivity().runOnUiThread(() -> {
                            ((GridAdapter) recyclerView.getAdapter()).checkButtonsVisibility();


                            recyclerView.getAdapter().notifyDataSetChanged();

                            // TODO
//                                    for (Integer i : ((GridAdapter) recyclerView.getAdapter()).getSelectedPositionsOfImagesViews()) {
//                                        recyclerView.getAdapter().notifyItemRemoved(i);
//                                        ((GridAdapter) recyclerView.getAdapter()).getSelectedPositionsOfImagesViews().remove(i);
//                                    }

                            makeTwoButtonsHide(buildButton, deleteButton);

                            if (TabPhoto.imageDataList.size() != 0) {
                                TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
                                textView.setVisibility(View.GONE);
                            } else {
                                TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
                                textView.setVisibility(View.VISIBLE);
                            }

                        });


                    }).start();
                }
            };

            deleteButton.setOnClickListener(deleteButtonOnClickListener);


            makeTwoButtonsHide(buildButton, deleteButton);

            makeTwoButtonsVisible(cameraButton, galleryButton);


            scrollToPosition();


        } catch (Exception | AssertionError e) {
            e.printStackTrace();
        }

    }

    private void sendSelectedPhotosToServerToBuild3DModel(List<Bitmap> bitmapListOfSelectedImages,
                                                          int filesCount)
            throws TabPhotoException, ProjectException, AppException, IOException {
        ProjectStorage storage = App.getProjectStorage();
//        storage.getCurrentProject().addImages(bitmapListOfSelectedImages);
        storage.saveProject();


        // TODO : store 3dModel in CURRENT_PROJECT_NAME/model/


        List<File> listOfJPEGFiles = new ArrayList<>();

        if (this.cacheTmpDirectory == null || this.outputDirModels == null) {
            initializePathsForDirectory();
        }
        System.out.println(cacheTmpDirectory);
        System.out.println(outputDirModels);
        for (int i = 0; i < filesCount; i++) {
            String generatedFileNameForJPEGPhoto = RandomStringUtils.random(lengthOfRandomFileJPEGName, true, false) + ".jpg";
            File jpegFile = new File(cacheTmpDirectory.resolve(generatedFileNameForJPEGPhoto).toString());
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(jpegFile);
                assert bitmapListOfSelectedImages.get(i) != null;

                if (bitmapListOfSelectedImages.get(i).compress(Bitmap.CompressFormat.JPEG, 100, outputStream)) {
                    outputStream.flush();
                    outputStream.close();
                } else {
                    throw new TabPhotoException("Cannot compress Bitmap to Jpeg in checkPermission\n");
                }
            } catch (IOException e) {
                throw new TabPhotoException("Caught exception in button build in checkPermission\\n", e);
            }

            listOfJPEGFiles.add(jpegFile);
        }


        for (File f : listOfJPEGFiles) {
            System.out.println(f.getName());
        }

        String currentProjectName = storage.getCurrentProject().getProjectName();

        File projectDirectoryForModels = new File(outputDirModels.toString());
        projectDirectoryForModels.mkdir();

        String generatedFileNameForMODEL;
        File resultFileFor3DModel;

        resultFileFor3DModel = createResultFile();
        System.out.println("Result model file --  " + resultFileFor3DModel);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Client.httpClientRequest(listOfJPEGFiles, resultFileFor3DModel);
                } catch (AppException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            App.getProjectStorage().getCurrentProject().addAndSaveModel(resultFileFor3DModel);
            App.getProjectStorage().saveProject();
        }
        catch(ProjectException e) {
            Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        Toast.makeText(getContext(), "Build of 3DModel has finished", Toast.LENGTH_LONG).show();
        tab3DPlain.updateModelListAndSendItToAdapter();
        clearDirectory(cacheTmpDirectory.toFile());
    }

    private void clearDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                clearDirectory(child);

        fileOrDirectory.delete();
    }

    private boolean checkPermission() {
        assert getActivity() != null;
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        assert getActivity() != null;
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(
                            getActivity(),
                            "Write External Storage permission allows us to create files. Please allow this permission in App Settings.",
                            Toast.LENGTH_LONG
                    )
                    .show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public static void makeTwoButtonsHide(Button button1, Button button2) {
        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
    }

    public static void makeTwoButtonsVisible(Button button1, Button button2) {
        button1.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
    }


    private void scrollToPosition() {
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                recyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                assert layoutManager != null;
                View viewAtPosition = layoutManager.findViewByPosition(MainActivity.currentPosition);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerView.post(() -> layoutManager.scrollToPosition(MainActivity.currentPosition));
                }
            }
        });
    }

    private void prepareTransitions() {
        System.out.println("CALLED PREPARE TRANSITION EXIT");
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.grid_exit_transition));

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the ViewHolder for the clicked position.
                        RecyclerView.ViewHolder selectedViewHolder = recyclerView
                                .findViewHolderForAdapterPosition(MainActivity.currentPosition);
                        if (selectedViewHolder == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements
                                .put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.card_image));
                    }
                });
    }

    public static void addImage(Bitmap bitmap) {
        bitmapArrayList.add(bitmap);
        updateImageBitmapListAndSendItToTheAdapter();
    }

    public static void loadImagesFromCurrentProject() {
        bitmapArrayList.clear();
        bitmapArrayList.addAll(App.getProjectStorage().getCurrentProject().getImages());
        updateAllImagesAndSendItToAdapter();
    }

    @NonNull
    @Contract(" -> new")
    private File createResultFile() {
        final Dialog dialog = new Dialog(this.getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_save_model);

        final EditText projectName = dialog.findViewById(R.id.editTextModelName);
        Button submitButton = dialog.findViewById(R.id.save_model_submit);

        File resultFile = new File("NewModel");
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String modelName = projectName.getText().toString();
                boolean result = resultFile.renameTo(new File(cacheTmpDirectory.resolve(modelName).toString()));
                assert(result);
            }
        });
        dialog.show();
        return resultFile;
    }
}
