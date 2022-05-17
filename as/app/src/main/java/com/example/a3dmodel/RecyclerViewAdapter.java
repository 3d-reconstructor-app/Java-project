package com.example.a3dmodel;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.a3dmodel.adapter.GridAdapter;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<ViewModel> items;
    private OnItemClickListener onItemClickListener;

    public RecyclerViewAdapter(ArrayList<ViewModel> items) {
        this.items = items;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<ViewModel> viewModels) {
        items.clear();
        items.addAll(viewModels);
        notifyDataSetChanged();
    }

    public void addItem(int position, ViewModel viewModel) {
        items.add(position, viewModel);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewModel item = items.get(position);
        holder.title.setText(item.getTitle());
        Picasso.with(holder.image.getContext()).load(item.getImage()).into(holder.image);
        holder.price.setText(item.getPrice());
        holder.credit.setText(item.getCredit());
        holder.description.setText(item.getDescription());

        holder.itemView.setTag(item);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public void onClick(final View v) {
        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemClickListener.onItemClick(v, (ViewModel) v.getTag());
                }
            }, 0);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private Bitmap bitmap;
        private final RequestManager requestManager; // a class for managing and starting requests for Glide
//        private final GridAdapter.ViewHolderListener viewHolderListener;

        public ViewHolder(View itemView, RequestManager requestManager, Bitmap bitmap) {
            super(itemView);
//            image = newImg;
            this.bitmap = bitmap;
            this.requestManager = requestManager;
        }

        void onBind(){
            int adapterPosition = getAdapterPosition();
            // image.setTransitionName().... TODO maybe its if i want to opne image in another view screen

            requestManager.load(image)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource,
                                                       Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            return true;
                        }
                    })
                    .into(image);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, ViewModel viewModel);

    }
}
