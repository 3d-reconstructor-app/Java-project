package com.example.a3dmodel.helperclass;

import androidx.recyclerview.widget.RecyclerView;

import com.example.a3dmodel.TabPhoto;
import com.example.a3dmodel.adapter.GridAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class UpdaterOfFragmentsData {
    public static void updateTabPhotoInformationOnLoading(){
        TabPhoto.clearFieldsWhenUpdatingProjectInfo();
    }
}
