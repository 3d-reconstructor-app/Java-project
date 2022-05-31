package com.example.a3dmodel;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageData {
    private final Bitmap imageBitmap;
    private final String imagePathInSystem;

    public ImageData(Bitmap image) {
        this.imageBitmap = image;
        imagePathInSystem = null;
    }

    public ImageData(Bitmap image, String imagePathInSystem) {
        this.imageBitmap = image;
        this.imagePathInSystem = imagePathInSystem;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public static List<ImageData> createImageDataList(List<Bitmap> bitmapList) {
        List<ImageData> imageDataList = new ArrayList<>();
        for (Bitmap bitmap : bitmapList) {
            imageDataList.add(new ImageData(bitmap));
        }
        return imageDataList;
    }

}
