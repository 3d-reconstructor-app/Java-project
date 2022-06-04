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
        ViewHolder snapshotViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) projectsContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.project_snapshot, parent, false);
            snapshotViewHolder.projectIcon = (ImageView) convertView.findViewById(R.id.snapshotImageView);
            snapshotViewHolder.projectName = (TextView) convertView.findViewById(R.id.snapshotTextView);
            snapshotViewHolder.creationDate = (EditText) convertView.findViewById(R.id.snapshotDateView);
            convertView.setTag(snapshotViewHolder);
        }
        else {
            snapshotViewHolder = (ViewHolder) convertView.getTag();
        }
            ProjectSnapshotData snapshot = projectsData.get(position);
            snapshotViewHolder.projectIcon.setImageBitmap(snapshot.getProjectIcon());
            snapshotViewHolder.projectName.setText(snapshot.getProjectName());
            snapshotViewHolder.creationDate.setText(snapshot.getCreationTime());

        return convertView;
    }

    static class ViewHolder {
        ImageView projectIcon;
        TextView projectName;
        EditText creationDate;
    }
}
