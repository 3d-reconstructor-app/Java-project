package com.example.a3dmodel.adapter;

import static com.example.a3dmodel.MainActivity.bitmapArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.a3dmodel.tabPhoto;

/**
 * A fragment for displaying a grid of images.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageViewHolder> {

    public static List<ImageData> imageDataList;
    private static boolean isSelectMode = false;
    private List<ImageData> selectedImageDataItems = new ArrayList<>();

    public GridAdapter(List<ImageData> imageDataList){
        GridAdapter.imageDataList = imageDataList;
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
        public ImageViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.card_image);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    isSelectMode = true;
                    if(selectedImageDataItems.contains(imageDataList.get(getAdapterPosition()))){
                        itemView.setBackgroundColor(Color.TRANSPARENT);
                        selectedImageDataItems.remove(imageDataList.get(getAdapterPosition()));
                    } else {
                        itemView.setBackgroundResource(R.color.selectedItemInPhoto);
                        selectedImageDataItems.add(imageDataList.get(getAdapterPosition()));
                    }

                    if(selectedImageDataItems.size() != 0){
                        Button buttonGallery = tabPhoto.frameLayout.findViewById(R.id.button_gallery);
                        Button buttonCamera = tabPhoto.frameLayout.findViewById(R.id.button_camera);
                        buttonGallery.setVisibility(View.GONE);
                        buttonCamera.setVisibility(View.GONE);

                        Button buttonBuild3DModel = tabPhoto.frameLayout.findViewById(R.id.button_build);
                        Button buttonDelete = tabPhoto.frameLayout.findViewById(R.id.button_delete);
                        buttonBuild3DModel.setVisibility(View.VISIBLE);
                        buttonDelete.setVisibility(View.VISIBLE);
                    } else {
                        Button buttonGallery = tabPhoto.frameLayout.findViewById(R.id.button_gallery);
                        Button buttonCamera = tabPhoto.frameLayout.findViewById(R.id.button_camera);
                        buttonGallery.setVisibility(View.VISIBLE);
                        buttonCamera.setVisibility(View.VISIBLE);

                        Button buttonBuild3DModel = tabPhoto.frameLayout.findViewById(R.id.button_build);
                        Button buttonDelete = tabPhoto.frameLayout.findViewById(R.id.button_delete);
                        buttonBuild3DModel.setVisibility(View.GONE);
                        buttonDelete.setVisibility(View.GONE);
                    }

                    if(selectedImageDataItems.size() == 0){
                        isSelectMode = false;

                    }
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isSelectMode){
                        if(selectedImageDataItems.contains(imageDataList.get(getAdapterPosition()))){
                            itemView.setBackgroundColor(Color.TRANSPARENT);
                            selectedImageDataItems.remove(imageDataList.get(getAdapterPosition()));
                        } else {
                            itemView.setBackgroundResource(R.color.selectedItemInPhoto);
                            selectedImageDataItems.add(imageDataList.get(getAdapterPosition()));
                        }

                        if(selectedImageDataItems.size() != 0){
                            Button buttonGallery = tabPhoto.frameLayout.findViewById(R.id.button_gallery);
                            Button buttonCamera = tabPhoto.frameLayout.findViewById(R.id.button_camera);
                            buttonGallery.setVisibility(View.GONE);
                            buttonCamera.setVisibility(View.GONE);

                            Button buttonBuild3DModel = tabPhoto.frameLayout.findViewById(R.id.button_build);
                            Button buttonDelete = tabPhoto.frameLayout.findViewById(R.id.button_delete);
                            buttonBuild3DModel.setVisibility(View.VISIBLE);
                            buttonDelete.setVisibility(View.VISIBLE);
                        } else {

                            Button buttonGallery = tabPhoto.frameLayout.findViewById(R.id.button_gallery);
                            Button buttonCamera = tabPhoto.frameLayout.findViewById(R.id.button_camera);
                            buttonGallery.setVisibility(View.VISIBLE);
                            buttonCamera.setVisibility(View.VISIBLE);

                            Button buttonBuild3DModel = tabPhoto.frameLayout.findViewById(R.id.button_build);
                            Button buttonDelete = tabPhoto.frameLayout.findViewById(R.id.button_delete);
                            buttonBuild3DModel.setVisibility(View.GONE);
                            buttonDelete.setVisibility(View.GONE);
                        }


                        if(selectedImageDataItems.size() == 0){
                            isSelectMode = false;
                        }
                    } else {
                        // TODO? what to do when just click on it -- open in another window

                    }
                }
            });

        }


    }

}

