package com.example.a3dmodel.exeption;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class TabPhotoException extends Exception {

    public TabPhotoException() {

    }

    public TabPhotoException(String message){
        super(message);
    }

    public TabPhotoException(String message, Throwable cause){
        super(message, cause);
    }

    public TabPhotoException(Throwable cause){
        super(cause);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public TabPhotoException(String message,
                             Throwable cause,
                             boolean enableSuppression,
                             boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
