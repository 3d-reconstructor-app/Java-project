package com.example.a3dmodel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.a3dmodel.ui.main.SectionsPagerAdapter;
import com.example.a3dmodel.databinding.ActivityMainBinding;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
//import com.example.a3dmodel.photo_fragment.GridFragment;

public class MainActivity extends AppCompatActivity {

    static public Bitmap[] bitmapArrayList = new Bitmap[1000];
    static public int  bitmapALindex = 0;
    private ActivityMainBinding binding;
    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition";
    private static final int CAMERA_PIC_REQUEST = 1888;

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
//            ImageView imageView = findViewById(R.id.imageView);
//            imageView.setImageBitmap(imageBitmap);
            bitmapArrayList[bitmapALindex++] = imageBitmap;
        }


        super.onActivityResult(requestCode, resultCode, data);

    }



}