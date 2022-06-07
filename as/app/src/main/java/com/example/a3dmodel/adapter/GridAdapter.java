package com.example.a3dmodel.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.a3dmodel.data.ImageData;

import com.example.a3dmodel.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import com.example.a3dmodel.tabPhoto;

/**
 * A fragment for displaying a grid of images.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageViewHolder> {

    public static List<ImageData> imageDataList;
    public static List<ImageData> selectedImageDataItems = new ArrayList<>();
    public static List<View> selectedImagesViewWithBackgroundColor = new ArrayList<>();
    public static boolean isSelectMode = false;

    public GridAdapter(List<ImageData> imageDataList) {
        GridAdapter.imageDataList = imageDataList;
    }

    public GridAdapter() {

    }

    public static void addBitmapToImageDataList(Bitmap bitmap) {
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

    public static void checkButtonsVisibility() {
        @NotNull Button buttonGallery = tabPhoto.frameLayout.findViewById(R.id.button_gallery);
        @NotNull Button buttonCamera = tabPhoto.frameLayout.findViewById(R.id.button_camera);
        @NotNull Button buttonBuild3DModel = tabPhoto.frameLayout.findViewById(R.id.button_build);
        @NotNull Button buttonDelete = tabPhoto.frameLayout.findViewById(R.id.button_delete);

        if (selectedImageDataItems.size() != 0) {
            buttonGallery.setVisibility(View.GONE);
            buttonCamera.setVisibility(View.GONE);

            buttonBuild3DModel.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
        } else {
            buttonGallery.setVisibility(View.VISIBLE);
            buttonCamera.setVisibility(View.VISIBLE);

            buttonBuild3DModel.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        // holder should contain a member variable for any view
        // that will be set as you render a row
        private final ImageView image;

        // create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ImageViewHolder(View itemView) {
            // stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.card_image);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    isSelectMode = true;
                    if (selectedImageDataItems.contains(imageDataList.get(getAdapterPosition()))) {
                        itemView.setBackgroundColor(Color.TRANSPARENT);
                        selectedImageDataItems.remove(imageDataList.get(getAdapterPosition()));
                    } else {
                        itemView.setBackgroundResource(R.color.selectedItemInPhoto);
                        selectedImageDataItems.add(imageDataList.get(getAdapterPosition()));
                        selectedImagesViewWithBackgroundColor.add(itemView);
                    }


                    checkButtonsVisibility();

                    if (selectedImageDataItems.size() == 0) {
                        isSelectMode = false;

                    }
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isSelectMode) {
                        if (selectedImageDataItems.contains(imageDataList.get(getAdapterPosition()))) {
                            itemView.setBackgroundColor(Color.TRANSPARENT);
                            selectedImageDataItems.remove(imageDataList.get(getAdapterPosition()));
                        } else {
                            itemView.setBackgroundResource(R.color.selectedItemInPhoto);
                            selectedImageDataItems.add(imageDataList.get(getAdapterPosition()));
                            selectedImagesViewWithBackgroundColor.add(itemView);
                        }

                        checkButtonsVisibility();

                        if (selectedImageDataItems.size() == 0) {
                            isSelectMode = false;
                        }
                    } else {
                        // TODO @@@SHER
                        //  what to do when just click on it -- open in another window
                    }
                }
            });

        }

    }

}

