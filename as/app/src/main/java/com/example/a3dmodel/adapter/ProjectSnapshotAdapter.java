package com.example.a3dmodel.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3dmodel.App;
import com.example.a3dmodel.data.ProjectSnapshot;
import com.example.a3dmodel.R;
import com.example.a3dmodel.exeption.ProjectException;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

// TODO @@@ANDREY
public class ProjectSnapshotAdapter extends RecyclerView.Adapter<ProjectSnapshotAdapter.SnapshotViewHolder> {
    public static List<ProjectSnapshot> projects = new ArrayList<>();
    public Context projectsContext;

    public ProjectSnapshotAdapter(List<ProjectSnapshot> snapshotList) {
       projects = snapshotList;
    }

    @NonNull
    @Override
    public SnapshotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.project_snapshot, parent, false);
        SnapshotViewHolder snapshotViewHolder = new SnapshotViewHolder(view);
        return snapshotViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SnapshotViewHolder holder, int position) {
        ProjectSnapshot projectSnapshot = projects.get(position);

        holder.projectIcon.setImageResource(projectSnapshot.getProjectIcon());
        holder.projectName.setText(projectSnapshot.getProjectName());
        holder.creationDate.setText(projectSnapshot.getModTime());
    }

    @Override
    public int getItemCount() {
        Log.d("SnapshotAdapter", "getItemSize returned " + projects.size());
        return projects.size();
    }

    public static class SnapshotViewHolder extends RecyclerView.ViewHolder {
        public ImageView projectIcon;
        public TextView projectName;
        public TextView creationDate;

        public SnapshotViewHolder(View v) {
            super(v);
            projectName = (TextView) v.findViewById(R.id.snapshotTextView);
            projectIcon = (ImageView) v.findViewById(R.id.snapshotImageView);
            creationDate = (TextView) v.findViewById(R.id.snapshotDateView);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ViewHolder", "Element " + getAdapterPosition() + " clicked.");
                    try {
                        String projectName = ProjectSnapshotAdapter.projects.get(getAdapterPosition()).getProjectName();
                        App.getProjectStorage().openExistingProject(projectName);
                        Toast.makeText(itemView.getContext(), projectName + " successfully loaded", Toast.LENGTH_SHORT).show();
                    }
                    catch(ProjectException e) {
                        Toast.makeText(itemView.getContext(), "Couldn't load the project", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }
}
