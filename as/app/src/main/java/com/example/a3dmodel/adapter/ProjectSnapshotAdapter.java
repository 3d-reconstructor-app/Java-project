package com.example.a3dmodel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a3dmodel.R;
import com.example.a3dmodel.data.ProjectSnapshotData;

import java.util.List;

// TODO @@@ANDREY
public class ProjectSnapshotAdapter extends ArrayAdapter<String> {
    List<ProjectSnapshotData> projectsData;
    Context projectsContext;

    public ProjectSnapshotAdapter(@NonNull Context context, List<ProjectSnapshotData> snapshotData) {
        super(context, R.layout.project_snapshot);
        this.projectsData = snapshotData;
        this.projectsContext = context;
    }

    @Override
    public int getCount() {
        return projectsData.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) projectsContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // TODO : make a viewHolder
            convertView = inflater.inflate(R.layout.project_snapshot, parent, false);
            ImageView projectImageView = (ImageView) convertView.findViewById(R.id.snapshotImageView);
            TextView projectNameView = (TextView) convertView.findViewById(R.id.snapshotTextView);
            EditText projectDate = (EditText) convertView.findViewById(R.id.snapshotDateView);

            ProjectSnapshotData snapshot = projectsData.get(position);
            projectImageView.setImageBitmap(snapshot.getProjectIcon());
            projectNameView.setText(snapshot.getProjectName());
            projectDate.setText(snapshot.getCreationTime());
        }
        return convertView;
    }
}
