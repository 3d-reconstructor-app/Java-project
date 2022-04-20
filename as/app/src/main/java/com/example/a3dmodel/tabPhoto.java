
package com.example.a3dmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class tabPhoto extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_photos, container, false);
    }

}

//package com.example.a3dmodel;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.icu.text.SimpleDateFormat;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.FileProvider;
//import androidx.fragment.app.Fragment;
//
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Date;
//
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
//
//import android.content.Context;
//
//public class tabPhoto extends Fragment  {
//
//    String currentPhotoPath;
//    private static final int PERMISSION_REQUEST_CODE = 200;
//    private ArrayList<String> imagePaths;
//    private RecyclerView imagesRV;
//    private RecyclerViewAdapter imageRVAdapter;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        // we are calling a method to request
//        // the permissions to read external storage.
//        requestPermissions();
//
//        // creating a new array list and
//        // initializing our recycler view.
//        imagePaths = new ArrayList<>();
////        imagesRV = findViewById(R.id.idRVImages);
//
//        // calling a method to
//        // prepare our recycler view.
//        prepareRecyclerView();
//
//        return inflater.inflate(R.layout.fragment_tab_photos, container, false);
//    }
//
//    private boolean checkPermission() {
//        // in this method we are checking if the permissions are granted or not and returning the result.
//        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
//        return result == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermissions() {
//        if (checkPermission()) {
//            // if the permissions are already granted we are calling
//            // a method to get all images from our external storage.
//            Toast.makeText(this, "Permissions granted..", Toast.LENGTH_SHORT).show();
//            getImagePath();
//        } else {
//            // if the permissions are not granted we are
//            // calling a method to request permissions.
//            requestPermission();
//        }
//
//
//    }
//
//    private void requestPermission() {
//        //on below line we are requesting the rea external storage permissions.
//        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//    }
//
//    private void prepareRecyclerView() {
//
//        // in this method we are preparing our recycler view.
//        // on below line we are initializing our adapter class.
//        imageRVAdapter = new RecyclerViewAdapter(MainActivity.this, imagePaths);
//
//        // on below line we are creating a new grid layout manager.
//        GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 4);
//
//        // on below line we are setting layout
//        // manager and adapter to our recycler view.
//        imagesRV.setLayoutManager(manager);
//        imagesRV.setAdapter(imageRVAdapter);
//    }
//
//    private void getImagePath() {
//        // in this method we are adding all our image paths
//        // in our arraylist which we have created.
//        // on below line we are checking if the device is having an sd card or not.
//        boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
//
//        if (isSDPresent) {
//
//            // if the sd card is present we are creating a new list in
//            // which we are getting our images data with their ids.
//            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
//
//            // on below line we are creating a new
//            // string to order our images by string.
//            final String orderBy = MediaStore.Images.Media._ID;
//
//            // this method will stores all the images
//            // from the gallery in Cursor
//            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
//
//            // below line is to get total number of images
//            int count = cursor.getCount();
//
//            // on below line we are running a loop to add
//            // the image file path in our array list.
//            for (int i = 0; i < count; i++) {
//
//                // on below line we are moving our cursor position
//                cursor.moveToPosition(i);
//
//                // on below line we are getting image file path
//                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//
//                // after that we are getting the image file path
//                // and adding that path in our array list.
//                imagePaths.add(cursor.getString(dataColumnIndex));
//            }
//            imageRVAdapter.notifyDataSetChanged();
//            // after adding the data to our
//            // array list we are closing our cursor.
//            cursor.close();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        // this method is called after permissions has been granted.
//        switch (requestCode) {
//            // we are checking the permission code.
//            case PERMISSION_REQUEST_CODE:
//                // in this case we are checking if the permissions are accepted or not.
//                if (grantResults.length > 0) {
//                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    if (storageAccepted) {
//                        // if the permissions are accepted we are displaying a toast message
//                        // and calling a method to get image path.
//                        Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show();
//                        getImagePath();
//                    } else {
//                        // if permissions are denied we are closing the app and displaying the toast message.
//                        Toast.makeText(this, "Permissions denined, Permissions are required to use the app..", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
//        }
//    }
//
//
////    private File createImageFile() throws IOException {
////        // Create an image file name
////        @SuppressLint("SimpleDateFormat")
////        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
////        String imageFileName = "JPEG_" + timeStamp + "_";
////        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
////        File image = File.createTempFile(
////                imageFileName,  /* prefix */
////                ".jpg",         /* suffix */
////                storageDir      /* directory */
////        );
////
////        // Save a file: path for use with ACTION_VIEW intents
////        currentPhotoPath = image.getAbsolutePath();
////        return image;
////    }
////
////    private void dispatchTakePictureIntent() {
////        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        // Ensure that there's a camera activity to handle the intent
////        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
////            // Create the File where the photo should go
////            File photoFile = null;
////            try {
////                photoFile = createImageFile();
////            } catch (IOException ex) {
////                // Error occurred while creating the File
////                //            ...
////            }
////            // Continue only if the File was successfully created
////            if (photoFile != null) {
////                Uri photoURI = FileProvider.getUriForFile(this,
////                        "com.example.android.fileprovider",
////                        photoFile);
////                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
////                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
////            }
////        }
////    }
//
//}