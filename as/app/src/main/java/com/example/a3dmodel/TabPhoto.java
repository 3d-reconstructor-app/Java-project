package com.example.a3dmodel;

import org.apache.commons.lang3.RandomStringUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.a3dmodel.adapter.GridAdapter;
import com.example.a3dmodel.data.ImageData;
import com.example.a3dmodel.exeption.TabPhotoException;

import static com.example.a3dmodel.MainActivity.bitmapArrayList;

public class TabPhoto extends Fragment {
    static public RecyclerView recyclerView;
    static public List<ImageData> imageDataList = new ArrayList<>();
    private FrameLayout frameLayout;

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
//        postponeEnterTransition();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void updateImageBitmapListAndSendItToTheAdapter() {
        Bitmap lastPhotoBitmap = bitmapArrayList.get(bitmapArrayList.size() - 1);
        assert recyclerView.getAdapter() != null;
        imageDataList.add(new ImageData(lastPhotoBitmap));
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            Button cameraButton = (Button) view.findViewById(R.id.button_camera);
            Button galleryButton = (Button) view.findViewById(R.id.button_gallery);

            Button deleteButton = (Button) view.findViewById(R.id.button_delete);
            Button buildButton = (Button) view.findViewById(R.id.button_build);


            // TODO need to store photo directly in the system and save path for them
            View.OnClickListener cameraButtonOnClickListener = v -> new Thread(() -> {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                assert getActivity() != null;
                getActivity().startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }).start();

            cameraButton.setOnClickListener(cameraButtonOnClickListener);


            View.OnClickListener galleryButtonOnClickListener = v -> new Thread(() -> {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                assert getActivity() != null;
                getActivity().startActivityForResult(galleryIntent, GALLERY_PIC_REQUEST);

            }).start();

            galleryButton.setOnClickListener(galleryButtonOnClickListener);


            View.OnClickListener buildButtonOnClickListener = v -> {
                try {
                    new Thread(() -> {
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
                                    try {
                                        sendSelectedPhotosToServerToBuild3DModel(sdcard, bitmapListOfSelectedImages, listOfJPEGFiles, filesCount);
                                    } catch (TabPhotoException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    requestPermission();
                                }
                            } else {
                                File sdcard = Environment.getExternalStorageDirectory();
                                try {
                                    sendSelectedPhotosToServerToBuild3DModel(sdcard, bitmapListOfSelectedImages, listOfJPEGFiles, filesCount);
                                } catch (TabPhotoException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }).start();

                    assert getActivity() != null;
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "The construction of the 3D model has begun", Toast.LENGTH_SHORT).show());


                    // TODO @@@ANDREY
                    //  call right over here function for building 3D-MODEL with args -- ( "listOfJPEGFiles" )


                } catch (AssertionError e) {
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

                    assert getActivity() != null;

                    ((GridAdapter) recyclerView.getAdapter()).checkButtonsVisibility();

//                    ((GridAdapter)recyclerView.getAdapter()).notifyItemRangeInserted();

//                    ((GridAdapter) recyclerView.getAdapter()).notifyDataSetChanged(); // TODO ??? maybe this should be called -- think about it

                    makeTwoButtonsHide(buildButton, deleteButton);

                    if (TabPhoto.imageDataList.size() != 0) {
                        TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
                        textView.setVisibility(View.GONE);
                    } else {
                        TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
                        textView.setVisibility(View.VISIBLE);
                    }
                }
            };

            buildButton.setOnClickListener(buildButtonOnClickListener);


            View.OnClickListener deleteButtonOnClickListener = v -> new Thread(() -> {
                try {
                    ArrayList<ImageData> selectedImages = new ArrayList<>(GridAdapter.selectedImageDataItems);
                    assert getActivity() != null;
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Deleted " + selectedImages + " images", Toast.LENGTH_SHORT).show());
                    GridAdapter.imageDataList.removeAll(selectedImages);
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

//                    recyclerView.getAdapter().notifyDataSetChanged();

                        for (Integer i : ((GridAdapter) recyclerView.getAdapter()).getSelectedPositionsOfImagesViews()) {
                            recyclerView.getAdapter().notifyItemRemoved(i);
                            ((GridAdapter) recyclerView.getAdapter()).getSelectedPositionsOfImagesViews().remove(i);
                        }

                        assert ((GridAdapter) recyclerView.getAdapter()).getSelectedPositionsOfImagesViews().size() == 0;


                        makeTwoButtonsHide(buildButton, deleteButton);

                        if (TabPhoto.imageDataList.size() != 0) {
                            TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
                            textView.setVisibility(View.GONE);
                        } else {
                            TextView textView = view.findViewById(R.id.fragment_photo_empty_view);
                            textView.setVisibility(View.VISIBLE);
                        }

                    });

                } catch (AssertionError e) {
                    e.printStackTrace();
                }

            }).start();

            deleteButton.setOnClickListener(deleteButtonOnClickListener);



            makeTwoButtonsHide(buildButton, deleteButton);
            makeTwoButtonsVisible(cameraButton, galleryButton);

            scrollToPosition();

        } catch (Exception | AssertionError e) {
            e.printStackTrace();
        }

    }

    private void sendSelectedPhotosToServerToBuild3DModel(File sdcard,
                                                          List<Bitmap> bitmapListOfSelectedImages,
                                                          List<File> listOfJPEGFiles,
                                                          int filesCount)
            throws TabPhotoException {
        // TODO  @@@ANDREY
        //  inside "jpegFiles" create dir with a name of current project, if we decide
        //  to save snapshot of current project
        //  it will be easier to recover version from storage
        //  .
        //  maybe there is a point to create directory in a way -- CURRENT_PROJECT_NAME/JPEG_FILES/
        //                                                         CURRENT_PROJECT_NAME/3DMODELFILE
        //  if you decide so, change the path for "dir" File

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

        for (int i = 0; i < filesCount; i++) {
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

}