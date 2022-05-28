package com.example.a3dmodel.adapter;

import static com.example.a3dmodel.MainActivity.bitmapArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.example.a3dmodel.ImageData;
import com.example.a3dmodel.MainActivity;
import com.example.a3dmodel.R;
//import com.example.a3dmodel.RecyclerViewAdapter;
import com.example.a3dmodel.adapter.GridAdapter.ImageViewHolder;
import com.squareup.picasso.Picasso;
//import com.example.a3dmodel.photo_fragment.ImagePagerFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A fragment for displaying a grid of images.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageViewHolder> {

    private static List<ImageData> imageDataList;

    public GridAdapter(List<ImageData> imageDataList){
        this.imageDataList = imageDataList;
    }

    public GridAdapter(){
    }

    public static void addBitmapToImageDataList(Bitmap bitmap){
        imageDataList.add(new ImageData(bitmap));
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.image_card, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageData imageData = imageDataList.get(position);

        ImageView imageView = holder.image;
        imageView.setImageBitmap(imageData.getImageBitmap());
    }

    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    //    @NonNull
//    @Override
//    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()) .inflate(R.layout.image_card, parent, false);
//
//        return new ImageViewHolder(view);
//    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private final ImageView image;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ImageViewHolder(View view) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(view);
            this.image = (ImageView) itemView.findViewById(R.id.card_image);        }
    }

}

