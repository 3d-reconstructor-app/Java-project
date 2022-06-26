package com.example.a3dmodel.data;

import java.io.Serializable;

public class ModelData implements Serializable {
    private final String modelName;

    public ModelData(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}
