
package com.example.a3dmodel;
//import static com.example.a3dmodel.MainActivity.bitmapALindex;

import org.apache.commons.lang3.RandomStringUtils;

import static com.example.a3dmodel.MainActivity.bitmapArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.example.a3dmodel.adapter.GridAdapter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class tabPhoto extends Fragment {
    static public RecyclerView recyclerView;
    static public List<ImageData> imageDataList = new ArrayList<>();
    public static FrameLayout frameLayout;

    public static final int CAMERA_PIC_REQUEST = 1888; // ?
    public static final int GALLERY_PIC_REQUEST = 1777; // ?

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
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // TODO ??

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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                List<Bitmap> bitmapListOfSelectedImages = new ArrayList<>();
                for (int i = 0; i < GridAdapter.selectedImageDataItems.size(); i++) {
                    bitmapListOfSelectedImages.add(GridAdapter.selectedImageDataItems.get(i).getImageBitmap());
                }

                File root = Environment.getExternalStorageDirectory();
                // TODO
                //  inside "jpegFiles" create dir with a name of current project, if we decide
                //  to save snapshot of current project
                //  it will be easier to recover version from storage
                File dir = new File(root.getAbsolutePath() + "/jpegFiles/currentProject");
                dir.mkdirs();

                int length = 10;
                boolean useLetters = true;
                boolean useNumbers = false;
                List<File> listOfJPEGFiles = new ArrayList<>();
                for (int i = 0; i < GridAdapter.selectedImageDataItems.size(); i++) {

                    String generatedFileNameForJPEGPhoto = RandomStringUtils.random(length, useLetters, useNumbers);
                    boolean f = Files.exists(Path.of(dir +  generatedFileNameForJPEGPhoto));

                    if(f == true){
                        i--;
                        continue;
                    }

                    File curJPEGFile = new File(dir.toString(), generatedFileNameForJPEGPhoto);
//                    Log.i(TAG,)


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
                GridAdapter.imageDataList.removeAll(selectedImages);
                GridAdapter.selectedImageDataItems.clear();
                GridAdapter.isSelectMode = false;
                GridAdapter.selectedImagesViewWithBackgroundColor
                        .stream()
                        .forEach(imageView -> imageView.setBackgroundColor(Color.TRANSPARENT));
                GridAdapter.selectedImagesViewWithBackgroundColor.clear();
                assert recyclerView.getAdapter() != null;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };

        deleteButton.setOnClickListener(deleteButtonOnClickListener);


        makeTwoButtonsHide(buildButton, deleteButton);

        scrollToPosition();

    }

    public static void makeTwoButtonsHide(Button button1, Button button2) {

        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
    }

    // TODO add photo to gallery
//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(currentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

    // TODO i guess it should be placed somewhere else (?) or called
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

}