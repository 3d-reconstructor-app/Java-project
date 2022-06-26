package com.example.a3dmodel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3dmodel.App;
import com.example.a3dmodel.R;
import com.example.a3dmodel.data.ModelData;
import com.example.a3dmodel.data.ProjectSnapshot;
import com.example.a3dmodel.exeption.ProjectException;

import java.util.ArrayList;
import java.util.List;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ModelViewHolder>{
    public static List<ModelData> models = new ArrayList<>();
    private int position;
//    private final ArrayList<Integer> highlightedRows;

    public ModelAdapter(List<ModelData> modelList) {
        models = modelList;
//        highlightedRows = new ArrayList<>();
    }

    @NonNull
    @Override
    public ModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.model, parent, false);
        return new ModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelViewHolder holder, int position) {
        ModelData model = models.get(position);

//        holder.projectIcon.setImageResource(projectSnapshot.getProjectIcon());
        holder.modelName.setText(model.getModelName());
//        holder.creationDate.setText(projectSnapshot.getModTime());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

//        if (highlightedRows.contains(position)) {
//            Resources res = App.getContext().getResources();
//            holder.itemView.setBackgroundColor(Color.rgb(30,129,176));
//        }
//        else {
//            holder.itemView.setBackgroundColor(Color.parseColor("white"));
//        }
    }


//    @Override
//    public void onBindViewHolder(@NonNull ProjectSnapshotAdapter.SnapshotViewHolder holder, int position) {
//        ProjectSnapshot projectSnapshot = projects.get(position);
//
//        holder.projectIcon.setImageResource(projectSnapshot.getProjectIcon());
//        holder.projectName.setText(projectSnapshot.getProjectName());
//        holder.creationDate.setText(projectSnapshot.getModTime());
//
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                setPosition(holder.getPosition());
//                return false;
//            }
//        });
//
//        if (highlightedRows.contains(position)) {
//            Resources res = App.getContext().getResources();
//            holder.itemView.setBackgroundColor(Color.rgb(30,129,176));
//        }
//        else {
//            holder.itemView.setBackgroundColor(Color.parseColor("white"));
//        }
//    }

    @Override
    public void onViewRecycled(ModelViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

//    @SuppressLint("NotifyDataSetChanged")
//    public void highlightRow(int index) {
//        highlightedRows.clear();
//        highlightedRows.add(index);
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        Log.d("ModelAdapter", "getItemSize returned " + models.size());
        return models.size();
    }

//    public void findItemAndHighlight(String projectName) {
//        for (int i = 0; i < models.size(); i++) {
//            if (models.get(i).getModelName().equals(projectName)) {
//                highlightRow(i);
//                return;
//            }
//        }
//    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static class ModelViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
//        public ImageView projectIcon;
        public TextView modelName;
//        public TextView creationDate;

        public ModelViewHolder(View v) {
            super(v);
            modelName = (TextView) v.findViewById(R.id.modelTextView);
//            projectIcon = (ImageView) v.findViewById(R.id.snapshotImageView);
//            creationDate = (TextView) v.findViewById(R.id.snapshotDateView);

            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("ViewHolder", "Element " + getAdapterPosition() + " clicked.");
////                    try {
//                        String modelName = ModelAdapter.models.get(getAdapterPosition()).getModelName();
////                        App.getProjectStorage().openExistingProject(modelName);
////                        Toast.makeText(itemView.getContext(), modelName + " successfully loaded", Toast.LENGTH_SHORT).show();
////                    }
////                    catch(ProjectException e) {
////                        Toast.makeText(itemView.getContext(), "Couldn't load the project", Toast.LENGTH_LONG).show();
////                    }
//                }
//            });
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
            menu.add(Menu.NONE, R.id.model_draw_option,
                    Menu.NONE, R.string.draw_option);
            // TODO add model.delete.option
        }
    }
}
