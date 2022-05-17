
package com.example.a3dmodel;
import static com.example.a3dmodel.MainActivity.bitmapALindex;
import static com.example.a3dmodel.MainActivity.bitmapArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.example.a3dmodel.adapter.GridAdapter;

import java.io.File;
import java.util.List;
import java.util.Map;


public class tabPhoto extends Fragment {
    private RecyclerView recyclerView;

    private GridView gridView;
    private static final int CAMERA_PIC_REQUEST = 1888; // ??

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_photos, container, false);
        assert getActivity() != null;
        recyclerView = view.findViewById(R.id.fragment_photo_grid);
        recyclerView.setAdapter(new GridAdapter(this));
        prepareTransitions();
        postponeEnterTransition();
//        setContentView(R.layout.activity_main);
//        System.out.println(1);
//        this.gridView = (GridView) view.findViewById(R.id.fragment_photo_grid_view);
//        this.dbHelper = new MemoryDbHelper(getContext());
//        this.gridView.setAdapter(new MemoriesAdapter(getContext(), this.dbHelper.readAllMemories(), false));
//        this.gridView.setEmptyView(view.findViewById(R.id.fragment_photo_empty_view));
//        Intent intent = new Intent(getActivity(), PhotoTabActivity.class);
//        startActivity(intent);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button galleryButton = (Button) view.findViewById(R.id.button_gallery);
        View.OnClickListener galleryButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        galleryButton.setOnClickListener(galleryButtonOnClickListener);


        // TODO need to store photo directly in the system and save path for them
        Button cameraButton = (Button) view.findViewById(R.id.button_camera);
        View.OnClickListener cameraButtonOnClickListener = new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                assert getActivity() != null;
                getActivity().startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                Bitmap lastPhotoBitmap = bitmapArrayList[bitmapArrayList.length - 1];
                // TODO make adapter draw new taken photo which saved as Bitmap
                assert recyclerView.getAdapter() != null;
//                recyclerView.getAdapter().notifyItemChanged(bitmapALindex);
                recyclerView.getAdapter().notifyDataSetChanged();
//                recyclerView.getAdapter().setNewTakenPhoto(); //TODO uncomment
//                recyclerView.getAdapter().notifyItemChanged(0);
//                recyclerView.setAdapter(new GridAdapter(tabPhoto.this));
//                prepareTransitions();
//                postponeEnterTransition();
//                recyclerView.getAdapter().bindViewHolder(recyclerView.findContainingViewHolder(view), 0);
//                recyclerView.getAdapter().onBindViewHolder(recyclerView.findContainingViewHolder(view),
//                        0);
            }

        };

        cameraButton.setOnClickListener(cameraButtonOnClickListener);


        Button buildButton = (Button) view.findViewById(R.id.button_build);
        View.OnClickListener selectButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        buildButton.setOnClickListener(selectButtonOnClickListener);



        Button deleteButton = (Button) view.findViewById(R.id.button_delete);
        View.OnClickListener deleteButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        deleteButton.setOnClickListener(deleteButtonOnClickListener);


        scrollToPosition();

    }

    // add photo to gallery
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