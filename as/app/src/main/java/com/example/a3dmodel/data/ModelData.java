package com.example.a3dmodel.data;

import android.graphics.Bitmap;

import com.example.a3dmodel.project.SerialBitmap;

import java.io.Serializable;

public class ModelData implements Serializable {
    private final String modelName;
    private final SerialBitmap modelIcon;

    public ModelData(String modelName, Bitmap icon) {
        this.modelName = modelName;
        modelIcon = new SerialBitmap(icon);
    }

    public String getModelName() {
        return modelName;
    }

    public Bitmap getModelIcon() {
        return modelIcon.getBitmap();
    }
}
