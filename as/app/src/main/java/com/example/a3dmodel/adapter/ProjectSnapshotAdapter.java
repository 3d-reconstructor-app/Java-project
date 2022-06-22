package com.example.a3dmodel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
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
    private int position;
    private final ArrayList<Integer> highlightedRows;
    public ProjectSnapshotAdapter(List<ProjectSnapshot> snapshotList) {
       projects = snapshotList;
       highlightedRows = new ArrayList<>();
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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getPosition());
                return false;
            }
        });

        if (highlightedRows.contains(position)) {
            Resources res = App.getContext().getResources();
            holder.itemView.setBackgroundColor(Color.rgb(30,129,176));
        }
        else {
            holder.itemView.setBackgroundColor(Color.parseColor("white"));
        }
    }

    @Override
    public void onViewRecycled(SnapshotViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void highlightRow(int index) {
        highlightedRows.clear();
        highlightedRows.add(index);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        Log.d("SnapshotAdapter", "getItemSize returned " + projects.size());
        return projects.size();
    }

    public void findItemAndHighlight(String projectName) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectName().equals(projectName)) {
                highlightRow(i);
                return;
            }
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static class SnapshotViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
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
            v.setOnCreateContextMenuListener(this);
//            v.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                }
//            });

        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.option_1,
                    Menu.NONE, R.string.delete_option);
        }
    }
}
