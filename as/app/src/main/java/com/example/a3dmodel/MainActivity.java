package com.example.a3dmodel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.a3dmodel.ui.main.SectionsPagerAdapter;
import com.example.a3dmodel.databinding.ActivityMainBinding;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.a3dmodel.tabPhoto.CAMERA_PIC_REQUEST;
import static com.example.a3dmodel.tabPhoto.GALLERY_PIC_REQUEST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import com.example.a3dmodel.photo_fragment.GridFragment;

public class MainActivity extends AppCompatActivity {

    static public List<Bitmap> bitmapArrayList = new ArrayList<>();
    private ActivityMainBinding binding;
    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        if(requestCode != RESULT_OK) return;

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bitmapArrayList.add(imageBitmap);


//            assert bitmapArrayList.size() != 0;
//            System.out.println(bitmapArrayList.get(0));
//            System.out.println("size = " + bitmapArrayList.size());
//            tabPhoto myFragment = (tabPhoto) getSupportFragmentManager().findFragmentById(R.id.fragment_photo);
//            System.out.println(myFragment);
            Bitmap lastPhotoBitmap = bitmapArrayList.get(bitmapArrayList.size() - 1);
            tabPhoto.updateImageBitmapListAndSendItToTheAdapter();
//            tabPhoto.imageDataList.add(new ImageData(lastPhotoBitmap));
//            tabPhoto.recyclerView.getAdapter().notifyDataSetChanged();


        }

        if (requestCode == GALLERY_PIC_REQUEST && resultCode == RESULT_OK) {
//            assert data != null;
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            bitmapArrayList.add(imageBitmap);
//
//            Bitmap lastPhotoBitmap = bitmapArrayList.get(bitmapArrayList.size() - 1);
            Uri imageUri = data.getData(); // TODO SAVE IT
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert bitmap != null;
            bitmapArrayList.add(bitmap);
            tabPhoto.updateImageBitmapListAndSendItToTheAdapter();



        }



        super.onActivityResult(requestCode, resultCode, data);

    }


}