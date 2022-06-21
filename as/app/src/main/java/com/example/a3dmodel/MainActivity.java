package com.example.a3dmodel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.example.a3dmodel.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.a3dmodel.ui.main.SectionsPagerAdapter;
//import com.example.a3dmodel.databinding.ActivityMainBinding;

import static com.example.a3dmodel.TabPhoto.CAMERA_PIC_REQUEST;
import static com.example.a3dmodel.TabPhoto.GALLERY_PIC_REQUEST;
import static com.example.a3dmodel.TabPhoto.PERMISSION_REQUEST_CODE;

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
        tabs.setupWithViewPager(viewPager);

        start();
    }

    protected void start() {
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            assert imageBitmap != null;

            bitmapArrayList.add(imageBitmap);
            TabPhoto.updateImageBitmapListAndSendItToTheAdapter();
            TextView textView = findViewById(R.id.fragment_photo_empty_view);
            textView.setVisibility(View.GONE);
        }

        if (requestCode == GALLERY_PIC_REQUEST && resultCode == RESULT_OK) {
            assert data != null;

            Uri imageUri = data.getData(); // TODO SAVE IT

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

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void exitVisualisation(View v) {
        start();
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


}