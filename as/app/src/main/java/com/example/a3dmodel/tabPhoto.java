
package com.example.a3dmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.GridView;

import com.example.a3dmodel.activity.NewMemoryActivity;
import com.example.a3dmodel.activity.PhotoTabActivity;
import com.example.a3dmodel.db.MemoriesAdapter;
import com.example.a3dmodel.db.MemoryDbHelper;
import com.example.a3dmodel.activity.PhotoTabActivity;
//import com.example.a3dmodel.photo_fragment.GridFragment;

//public class tabPhoto extends Fragment {
//    //    int CAMERA_PIC_REQUEST = 2;
//    private static final int CAMERA_REQUEST = 1888;
//    private static final int MY_CAMERA_PERMISSION_CODE = 100;
//    private ImageView imageView;
//
//    private static final int CAMERA_PIC_REQUEST = 1337;
//    public static int currentPosition;
//    private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";
//    private Fragment fragment;
//
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_tab_photos, container, false);
//
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        Button cameraButton = (Button) view.findViewById(R.id.button_camera);
//        View.OnClickListener CameraButtonOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
//            }
//        };
//
//        cameraButton.setOnClickListener(CameraButtonOnClickListener);
//
//
//        Button buildModelButton = (Button) view.findViewById(R.id.button_build_3dmodel);
//        View.OnClickListener buildModelButtonOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        };
//
//        buildModelButton.setOnClickListener(buildModelButtonOnClickListener);
//    }
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
//            // Return here to prevent adding additional GridFragments when changing orientation.
//            return;
//        }
//
//        FragmentManager fragmentManager = getFragmentManager();
//        assert fragmentManager != null;
//        fragmentManager
//                .beginTransaction()
//                .setReorderingAllowed(true) // Optimize for shared element transition
////                .addSharedElement(transitioningView, transitioningView.getTransitionName())
//                .replace(R.id.fragment_photo, new GridFragment(), GridFragment.class.getSimpleName())
//                .addToBackStack(null)
//                .commit();
//
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//            } else {
//                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(photo);
//        }
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.SinglePhotoLayout, CardLayout.class, null)
//                .addToBackStack(null)
//                .commit();
//        super.onViewCreated(view, savedInstanceState);
//    }


//}
//


public class tabPhoto extends Fragment {
//    private MemoryDbHelper dbHelper;
    private GridView gridView;
    private static final int CAMERA_PIC_REQUEST = 200; // ??

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_photos, container, false);
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
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);


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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    @Override
//    public void onResume() {
//        super.onResume();
//
//        ((CursorAdapter) gridView.getAdapter()).swapCursor(this.dbHelper.readAllMemories());
//    }


}