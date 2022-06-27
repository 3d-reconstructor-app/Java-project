package com.example.a3dmodel.helperclass;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public final class CheckerForPermissions {
    public static final int PERMISSION_REQUEST_CODE = 100;

    private final Context context;
    private final Activity activity;

    public CheckerForPermissions(Activity activity, Context context) {
        this.context = context;
        this.activity = activity;
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(
                            context,
                            "Write External Storage permission allows us to create files. Please allow this permission in App Settings.",
                            Toast.LENGTH_LONG
                    )
                    .show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
}
