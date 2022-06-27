package com.example.a3dmodel;

import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import com.example.a3dmodel.ui.main.SectionsPagerAdapter;

import com.example.a3dmodel.databinding.ActivityMainBinding;
import com.example.a3dmodel.visualisation.fragments.View3DFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a3dmodel.ui.main.SectionsPagerAdapter;
//import com.example.a3dmodel.databinding.ActivityMainBinding;

import static com.example.a3dmodel.TabPhoto.CAMERA_PIC_REQUEST;
import static com.example.a3dmodel.TabPhoto.GALLERY_PIC_REQUEST;
import static com.example.a3dmodel.helperclass.CheckerForPermissions.PERMISSION_REQUEST_CODE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static Map<Integer, Uri> positionUriMapForAllFilesInPhotoGrid = new HashMap<>();
    public static List<Bitmap> bitmapArrayList = new ArrayList<>();
    private ActivityMainBinding binding;
    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "key.currentPosition";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;

        viewPager.setCurrentItem(1);
        tabs.setupWithViewPager(viewPager);

        if (!checkPermission()) {
            requestPermission();
        }

//        start();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(
                            this,
                            "Write External Storage permission allows us to create files. Please allow this permission in App Settings.",
                            Toast.LENGTH_LONG
                    )
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }


//    protected void start() {
////        setContentView(R.layout.activity_main);
////        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
////        ViewPager viewPager = findViewById(R.id.view_pager);
////        viewPager.setAdapter(sectionsPagerAdapter);
////        TabLayout tabs = findViewById(R.id.tabs);
////        tabs.setupWithViewPager(viewPager);
//    }

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
//        ViewPager viewPager = findViewById(R.id.view_pager);
//        viewPager.setAdapter(sectionsPagerAdapter);
//        TabLayout tabs = findViewById(R.id.tabs);
//        tabs.setupWithViewPager(viewPager);
//    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
//            if(data.getClipData() != null){
//
//            }
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            assert imageBitmap != null;

            bitmapArrayList.add(imageBitmap);
            TabPhoto.updateImageBitmapListAndSendItToTheAdapter();
            TextView textView = findViewById(R.id.fragment_photo_empty_view);
            textView.setVisibility(View.GONE);
        }

//        if (data.getClipData() != null) {
//            ClipData mClipData = data.getClipData();
//            int cout = data.getClipData().getItemCount();
//            for (int i = 0; i < cout; i++) {
//                // adding imageuri in array
//                Uri imageurl = data.getClipData().getItemAt(i).getUri();
//                mArrayUri.add(imageurl);
//            }
//            // setting 1st selected image into image switcher
//            imageView.setImageURI(mArrayUri.get(0));
//            position = 0;
//        } else {
//            Uri imageurl = data.getData();
//            mArrayUri.add(imageurl);
//            imageView.setImageURI(mArrayUri.get(0));
//            position = 0;
//        }

        if (requestCode == GALLERY_PIC_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            System.out.println(-1);

            if (data.getClipData() != null) {
                System.out.println(-2);
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    System.out.println(i);
                    // adding imageuri in array
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
//                    mArrayUri.add(imageurl);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert bitmap != null;
                    bitmapArrayList.add(bitmap);
                    TabPhoto.updateImageBitmapListAndSendItToTheAdapter();
                    TextView textView = findViewById(R.id.fragment_photo_empty_view);
                    textView.setVisibility(View.GONE);

                }
                // setting 1st selected image into image switcher
//                imageView.setImageURI(mArrayUri.get(0));
//                position = 0;
            } else {
                System.out.println("uno");

                Uri imageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert bitmap != null;
                bitmapArrayList.add(bitmap);
                TabPhoto.updateImageBitmapListAndSendItToTheAdapter();
                TextView textView = findViewById(R.id.fragment_photo_empty_view);
                textView.setVisibility(View.GONE);
            }

//            Uri imageUri = data.getData(); // TODO SAVE IT
//            Bitmap bitmap = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            assert bitmap != null;
//            bitmapArrayList.add(bitmap);
//            TabPhoto.updateImageBitmapListAndSendItToTheAdapter();
//            TextView textView = findViewById(R.id.fragment_photo_empty_view);
//            textView.setVisibility(View.GONE);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
            default:
                break;
        }
    }


//    public void onBackPressed() {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("MainActivity", "popping backstack");
//            fm.popBackStackImmediate();
////            fm.popBackStack();
//        } else {
//            Log.i("MainActivity", "nothing on backstack, calling super");
//            super.onBackPressed();
//        }



        @Override
        public void onBackPressed() {
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentById(R.id.view_3d);
            // If there is something in the back stack AND the current fragment is the LoggedInFragment
            System.out.println("manager.getBackStackEntryCount() = " +  manager.getBackStackEntryCount());
            System.out.println("fragment instanceof View3DFragment " +  (fragment instanceof View3DFragment));
            System.out.println("fragment instanceof tab3DPlain " +  (fragment instanceof tab3DPlain));
//            if (manager.getBackStackEntryCount() > 0
//                    && fragment instanceof View3DFragment) {

            if (manager.getBackStackEntryCount() > 0) {
                manager.popBackStack(tab3DPlain.class.getSimpleName(), 1);
            } else {
                Log.i("MainActivity", "nothing on backstack, calling super");
                super.onBackPressed();

        }

        }
}