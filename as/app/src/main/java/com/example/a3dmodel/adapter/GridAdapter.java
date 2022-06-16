package com.example.a3dmodel.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.a3dmodel.photofragment.ImagePagerFragment;
import com.example.a3dmodel.tabPhoto;

/**
 * A fragment for displaying a grid of images.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageViewHolder> {

    public static List<ImageData> imageDataList;
    public static List<ImageData> selectedImageDataItems = new ArrayList<>();
    public static List<View> selectedImagesViewWithBackgroundColor = new ArrayList<>();
    public static boolean isSelectMode = false;
    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;

    public GridAdapter(List<ImageData> imageDataList, Fragment fragment) {
        GridAdapter.imageDataList = imageDataList;
        this.requestManager = Glide.with(fragment);
        this.viewHolderListener = new ViewHolderListenerImpl(fragment);
    }

    public GridAdapter(Fragment fragment) {
        this.requestManager = Glide.with(fragment);
        this.viewHolderListener = new ViewHolderListenerImpl(fragment);
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
        private final RequestManager requestManager;
        private final ViewHolderListener viewHolderListener;

        // create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ImageViewHolder(View itemView,
                               RequestManager requestManager,
                               ViewHolderListener viewHolderListener) {
            // stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.image = (ImageView) itemView.findViewById(R.id.card_image);
            this.requestManager = requestManager;
            this.viewHolderListener = viewHolderListener;
//            itemView.findViewById(R.id.card_view).setOnClickListener(this);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    isSelectMode = true;
                    if (selectedImageDataItems.contains(imageDataList.get(getAdapterPosition()))) {
                        itemView.setBackgroundColor(Color.TRANSPARENT);
                        selectedImageDataItems.remove(imageDataList.get(getAdapterPosition()));
                    } else {
                        itemView.setBackgroundResource(R.color.purple_200);
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
                            itemView.setBackgroundResource(R.color.purple_200);
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

                        viewHolderListener.onItemClicked(view, getAdapterPosition());
                    }
                }
            });

        }


        void onBind() {
            int adapterPosition = getAdapterPosition();
            setImage(adapterPosition);
            // Set the string value of the image resource as the unique transition name for the view.
            image.setTransitionName(String.valueOf(imageDataList.get(adapterPosition).getImageBitmap()));
        }

        void setImage(final int adapterPosition) {
            // Load the image with Glide to prevent OOM error when the image drawables are very large.
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


    /// ---------------- HolderListener

    /**
     * A listener that is attached to all ViewHolders to handle image loading events and clicks.
     */
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
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if (MainActivity.currentPosition != position) {
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            fragment.startPostponedEnterTransition();
        }

        /**
         * Handles a view click by setting the current position to the given {@code position} and
         * starting a {@link  ImagePagerFragment} which displays the image at the position.
         *
         * @param view     the clicked {@link ImageView} (the shared element view will be re-mapped at the
         *                 GridFragment's SharedElementCallback)
         * @param position the selected view position
         */
        @Override
        public void onItemClicked(View view, int position) {
            // Update the position.
            MainActivity.currentPosition = position;

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
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

