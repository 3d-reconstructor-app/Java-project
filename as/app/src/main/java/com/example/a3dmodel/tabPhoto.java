package com.example.a3dmodel;

import org.apache.commons.lang3.RandomStringUtils;

import static com.example.a3dmodel.MainActivity.bitmapArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.a3dmodel.adapter.GridAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.a3dmodel.data.ImageData;
import com.example.a3dmodel.exeption.ProjectException;
import com.example.a3dmodel.exeption.TabPhotoException;
import com.example.a3dmodel.project.Project;
import com.example.a3dmodel.project.ProjectStorage;

public class tabPhoto extends Fragment {
    static public RecyclerView recyclerView;
    static public List<ImageData> imageDataList = new ArrayList<>();
    @SuppressLint("StaticFieldLeak") // TODO make this not static to avoid memory leak
    public static FrameLayout frameLayout;
    private ProjectStorage storage = App.getProjectStorage();

    public static final int PERMISSION_REQUEST_CODE = 100;
    public static final int CAMERA_PIC_REQUEST = 1888;
    public static final int GALLERY_PIC_REQUEST = 1777;
    private static final int lengthOfRandomFileJPEGName = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_photos, container, false);
        assert getActivity() != null;
        frameLayout = view.findViewById(R.id.fragment_photo);
        recyclerView = view.findViewById(R.id.fragment_photo_grid);

        recyclerView.setAdapter(new GridAdapter(imageDataList));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

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

        try {
            App.getProjectStorage().loadProject();
        }
        catch(ProjectException e) {
            Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // TODO need to store photo directly in the system and save path for them
        Button cameraButton = (Button) view.findViewById(R.id.button_camera);
        View.OnClickListener cameraButtonOnClickListener = new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                assert getActivity() != null;
                getActivity().startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }

        };

        cameraButton.setOnClickListener(cameraButtonOnClickListener);


        Button galleryButton = (Button) view.findViewById(R.id.button_gallery);
        View.OnClickListener galleryButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                assert getActivity() != null;
                getActivity().startActivityForResult(galleryIntent, GALLERY_PIC_REQUEST);

            }
        };

        galleryButton.setOnClickListener(galleryButtonOnClickListener);


        Button buildButton = (Button) view.findViewById(R.id.button_build);
        View.OnClickListener selectButtonOnClickListener = new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    int filesCount = GridAdapter.selectedImageDataItems.size();
                    List<Bitmap> bitmapListOfSelectedImages = new ArrayList<>();
                    List<File> listOfJPEGFiles = new ArrayList<>();


                    for (int i = 0; i < filesCount; i++) {
                        bitmapListOfSelectedImages.add(GridAdapter.selectedImageDataItems.get(i).getImageBitmap());
                    }

                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkPermission()) {
                                File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                                sendSelectedPhotosToServerToBuild3DModel(sdcard, bitmapListOfSelectedImages, listOfJPEGFiles, filesCount);
                            } else {
                                requestPermission();
                            }
                        } else {
                            File sdcard = Environment.getExternalStorageDirectory();
                            sendSelectedPhotosToServerToBuild3DModel(sdcard, bitmapListOfSelectedImages, listOfJPEGFiles, filesCount);
                        }
                    }

                    Toast.makeText(getContext(), "The construction of the 3D model has begun", Toast.LENGTH_SHORT).show();

                    // TODO @@@ANDREY
                    //  call right over here function for building 3D-MODEL with args -- ( "listOfJPEGFiles" )


                } catch (TabPhotoException | ProjectException e) {
                    e.printStackTrace();
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
                    GridAdapter.checkButtonsVisibility();
                    recyclerView.getAdapter().notifyDataSetChanged();
                }


            }
        };

        buildButton.setOnClickListener(selectButtonOnClickListener);


        Button deleteButton = (Button) view.findViewById(R.id.button_delete);
        View.OnClickListener deleteButtonOnClickListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                ArrayList<ImageData> selectedImages = new ArrayList<>(GridAdapter.selectedImageDataItems);
                Toast.makeText(getContext(), "Deleted " + selectedImages + " images", Toast.LENGTH_SHORT).show();
                GridAdapter.imageDataList.removeAll(selectedImages);
                GridAdapter.selectedImageDataItems.clear();
                GridAdapter.isSelectMode = false;
                for (View imageView : GridAdapter.selectedImagesViewWithBackgroundColor) {
                    imageView.setBackgroundColor(Color.TRANSPARENT);
                }
                GridAdapter.selectedImagesViewWithBackgroundColor.clear();
                assert recyclerView.getAdapter() != null;
                GridAdapter.checkButtonsVisibility();
                recyclerView.getAdapter().notifyDataSetChanged();
                Project project = App.getProjectStorage().getCurrentProject();
                project.deleteImages(selectedImages.stream().map(ImageData::getImageBitmap).collect(Collectors.toList()));
            }
        };

        deleteButton.setOnClickListener(deleteButtonOnClickListener);

        makeTwoButtonsHide(buildButton, deleteButton);

        scrollToPosition();
        loadImagesFromCurrentProject();
    }

    private void sendSelectedPhotosToServerToBuild3DModel(File sdcard,
                                                          List<Bitmap> bitmapListOfSelectedImages,
                                                          List<File> listOfJPEGFiles,
                                                          int filesCount)
            throws TabPhotoException, ProjectException {
        // TODO  @@@ANDREY
        //  inside "jpegFiles" create dir with a name of current project, if we decide
        //  to save snapshot of current project
        //  it will be easier to recover version from storage
        //  .
        //  maybe there is a point to create directory in a way -- CURRENT_PROJECT_NAME/JPEG_FILES/
        //                                                         CURRENT_PROJECT_NAME/3DMODELFILE
        //  if you decide so, change the path for "dir" File


        ProjectStorage storage = App.getProjectStorage();
//        storage.getCurrentProject().addImages(bitmapListOfSelectedImages);
        storage.saveProject();
            // TODO : store 3dModel in CURRENT_PROJECT_NAME/model/


        File dir = new File(sdcard.getAbsoluteFile() + "/jpegFiles/currentProject/");

        if (dir == null) {
            throw new TabPhotoException("dir is null in checkPermission\n");
        }

        dir.mkdirs();
        if (!dir.exists()) {
            throw new TabPhotoException("dir does not exists in checkPermission\n");
        }

        if (!dir.isDirectory()) {
            throw new TabPhotoException("dir does not a directory in checkPermission\\n");
        }

        for (
                int i = 0;
                i < filesCount; i++) {
            String generatedFileNameForJPEGPhoto = RandomStringUtils.random(lengthOfRandomFileJPEGName, true, false) + ".jpg";
            File jpegFile = new File(dir, generatedFileNameForJPEGPhoto);
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

    }


    private boolean checkPermission() {
        assert getActivity() != null;
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
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
}