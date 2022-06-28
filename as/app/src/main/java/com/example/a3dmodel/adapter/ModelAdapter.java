package com.example.a3dmodel.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a3dmodel.R;
import com.example.a3dmodel.data.ModelData;

import java.util.ArrayList;
import java.util.List;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ModelViewHolder>{
    public static List<ModelData> models = new ArrayList<>();
    private int position;
    public ModelAdapter(List<ModelData> modelList) {
        models = modelList;
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

        holder.modelName.setText(model.getModelName());
        holder.modelIcon.setImageBitmap(model.getModelIcon());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

    }

    @Override
    public void onViewRecycled(@NonNull ModelViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        Log.d("ModelAdapter", "getItemSize returned " + models.size());
        return models.size();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static class ModelViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView modelName;
        public ImageView modelIcon;

        public ModelViewHolder(View v) {
            super(v);
            modelName = (TextView) v.findViewById(R.id.modelTextView);
            modelIcon = (ImageView) v.findViewById(R.id.modelIcon);
            v.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onCreateContextMenu(@NonNull ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.model_draw_option,
                    Menu.NONE, R.string.draw_option);
        }
    }
}
