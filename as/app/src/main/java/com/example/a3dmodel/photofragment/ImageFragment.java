package com.example.a3dmodel.photofragment;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.a3dmodel.R;

import org.jetbrains.annotations.NotNull;

public class ImageFragment extends Fragment {

    private static final String KEY_IMAGE_RES = "key.BitmapImage";

    public static ImageFragment newInstance(@NotNull Bitmap bitmap) {
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_IMAGE_RES, bitmap);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_image, container, false);

        Bundle arguments = getArguments();
        assert arguments != null;
        Bitmap bitmapOfSelectedImage = arguments.getParcelable(KEY_IMAGE_RES);

        view.findViewById(R.id.image).setTransitionName(String.valueOf(bitmapOfSelectedImage));

        Glide.with(this)
                .load(bitmapOfSelectedImage)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                Target<Drawable> target,
                                                boolean isFirstResource) {
                        assert getParentFragment() != null;
                        getParentFragment().startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource,
                                                   Object model,
                                                   Target<Drawable> target,
                                                   DataSource dataSource,
                                                   boolean isFirstResource) {
                        assert getParentFragment() != null;
                        getParentFragment().startPostponedEnterTransition();
                        return false;
                    }
                })
                .into((ImageView) view.findViewById(R.id.image));


        Button getBackButton = (Button) view.findViewById(R.id.back_button_in_image_view_transition);
        assert getBackButton != null;
        View.OnClickListener getBackButtonClickListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        };

        getBackButton.setOnClickListener(getBackButtonClickListener);

        return view;
    }
}
