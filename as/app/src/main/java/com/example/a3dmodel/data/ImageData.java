package com.example.a3dmodel.data;

import android.graphics.Bitmap;

public class ImageData {
    private final Bitmap imageBitmap;

    public ImageData(Bitmap image) {
        this.imageBitmap = image;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
}
