package com.example.a3dmodel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.a3dmodel.MainActivity;
import com.example.a3dmodel.data.ImageData;

import com.example.a3dmodel.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.a3dmodel.photofragment.ImagePagerFragment;
import com.example.a3dmodel.TabPhoto;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageViewHolder> {

    public static List<ImageData> imageDataList;
    public static List<ImageData> selectedImageDataItems = new ArrayList<>();
    public static List<View> selectedImagesViewWithBackgroundColor = new ArrayList<>();
    public static boolean isSelectMode = false;
    private final Set<Integer> selectedPositionsOfImagesViews = new HashSet<>();
    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;

    @SuppressLint("NotifyDataSetChanged")
    public void clearFieldsWhenUpdatingProjectInfo() {
        selectedImageDataItems.clear();
        selectedPositionsOfImagesViews.clear();
        selectedImagesViewWithBackgroundColor.forEach(e -> e.setBackgroundColor(Color.TRANSPARENT));
        selectedImagesViewWithBackgroundColor.clear();

        isSelectMode = false;


        checkButtonsVisibility();
    }

    private final FrameLayout frameLayout;

    public GridAdapter(List<ImageData> imageDataList, @NonNull TabPhoto fragment) {
        frameLayout = fragment.getFrameLayout();
        assert frameLayout != null;
        GridAdapter.imageDataList = imageDataList;
        this.requestManager = Glide.with(fragment);
        this.viewHolderListener = new ViewHolderListenerImpl(fragment);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.image_card, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(
                view,
                requestManager,
                viewHolderListener
        );
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageData imageData = imageDataList.get(position);

        ImageView imageView = holder.image;
        imageView.setImageBitmap(imageData.getImageBitmap());

        holder.onBind();
    }


    @Override
    public int getItemCount() {
        return imageDataList.size();
    }

    public void checkButtonsVisibility() {
        @NotNull Button buttonGallery = frameLayout.findViewById(R.id.button_gallery);
        @NotNull Button buttonCamera = frameLayout.findViewById(R.id.button_camera);
        @NotNull Button buttonBuild3DModel = frameLayout.findViewById(R.id.button_build);
        @NotNull Button buttonDelete = frameLayout.findViewById(R.id.button_delete);


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
        private final ImageView image;
        private final RequestManager requestManager;
        private final ViewHolderListener viewHolderListener;

        public ImageViewHolder(View itemView,
                               RequestManager requestManager,
                               ViewHolderListener viewHolderListener) {
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.card_image);
            this.requestManager = requestManager;
            this.viewHolderListener = viewHolderListener;
            itemView.setOnLongClickListener(view -> {
                isSelectMode = true;
                if (selectedImageDataItems.contains(imageDataList.get(getAdapterPosition()))) {
                    itemView.setBackgroundColor(Color.TRANSPARENT);
                    selectedImageDataItems.remove(imageDataList.get(getAdapterPosition()));
                    selectedPositionsOfImagesViews.remove(getAdapterPosition());
                } else {
                    itemView.setBackgroundResource(R.color.purple_200);
                    selectedImageDataItems.add(imageDataList.get(getAdapterPosition()));
                    selectedImagesViewWithBackgroundColor.add(itemView);
                    selectedPositionsOfImagesViews.add(getAdapterPosition());
                }

                checkButtonsVisibility();

                if (selectedImageDataItems.size() == 0) {
                    isSelectMode = false;

                }
                return true;
            });

            itemView.setOnClickListener(view -> {
                if (isSelectMode) {
                    if (selectedImageDataItems.contains(imageDataList.get(getAdapterPosition()))) {
                        itemView.setBackgroundColor(Color.TRANSPARENT);
                        selectedImageDataItems.remove(imageDataList.get(getAdapterPosition()));
                        selectedPositionsOfImagesViews.remove(getAdapterPosition());

                    } else {
                        itemView.setBackgroundResource(R.color.purple_200);
                        selectedImageDataItems.add(imageDataList.get(getAdapterPosition()));
                        selectedImagesViewWithBackgroundColor.add(itemView);
                        selectedPositionsOfImagesViews.add(getAdapterPosition());
                    }

                    checkButtonsVisibility();
                    if (selectedImageDataItems.size() == 0) {
                        isSelectMode = false;
                    }
                } else {
                    viewHolderListener.onItemClicked(view, getAdapterPosition());
                }
            });

        }


        void onBind() {
            int adapterPosition = getAdapterPosition();
            setImage(adapterPosition);
            image.setTransitionName(String.valueOf(imageDataList.get(adapterPosition).getImageBitmap()));
        }

        void setImage(final int adapterPosition) {
            Bitmap bitmap = imageDataList.get(adapterPosition).getImageBitmap();
            Drawable bitmapDrawable = new BitmapDrawable(bitmap);
            requestManager
                    .load(bitmapDrawable)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(image, adapterPosition);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource,
                                                       Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(image, adapterPosition);
                            return false;
                        }
                    })
                    .into(image);
        }
    }

    private interface ViewHolderListener {
        void onLoadCompleted(ImageView view, int adapterPosition);

        void onItemClicked(View view, int adapterPosition);
    }


    private static class ViewHolderListenerImpl implements ViewHolderListener {

        private final Fragment fragment;
        private final AtomicBoolean enterTransitionStarted;

        ViewHolderListenerImpl(Fragment fragment) {
            this.fragment = fragment;
            this.enterTransitionStarted = new AtomicBoolean();
        }

        @Override
        public void onLoadCompleted(ImageView view, int position) {
            if (MainActivity.currentPosition != position) {
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            fragment.startPostponedEnterTransition();
        }

        @Override
        public void onItemClicked(View view, int position) {
            MainActivity.currentPosition = position;
            assert fragment.getExitTransition() != null;
            ((TransitionSet) fragment.getExitTransition()).excludeTarget(view, true);

            ImageView transitioningView = view.findViewById(R.id.card_image);
            assert fragment.getFragmentManager() != null;
            fragment.getFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true) // Optimize for shared element transition
                    .addSharedElement(transitioningView, transitioningView.getTransitionName())
                    .replace(R.id.fragment_photo, new ImagePagerFragment(), ImagePagerFragment.class
                            .getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
    }
}

