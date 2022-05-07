package com.example.a3dmodel.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a3dmodel.R;
import com.example.a3dmodel.db.MemoriesAdapter;
import com.example.a3dmodel.db.MemoryDbHelper;

public class PhotoTabActivity extends AppCompatActivity {
    private MemoryDbHelper dbHelper;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.gridView = (GridView) findViewById(R.id.fragment_photo_grid_view);
        this.dbHelper = new MemoryDbHelper(this);
        this.gridView.setAdapter(new MemoriesAdapter(this, this.dbHelper.readAllMemories(), false));
        this.gridView.setEmptyView(findViewById(R.id.fragment_photo_grid_view));
    }

    @Override
    protected void onResume() {
        super.onResume();

        ((CursorAdapter)gridView.getAdapter()).swapCursor(this.dbHelper.readAllMemories());
    }

    public void addNewMemory(View view) {
        Intent intent = new Intent(this, NewMemoryActivity.class);
        startActivity(intent);
    }
}