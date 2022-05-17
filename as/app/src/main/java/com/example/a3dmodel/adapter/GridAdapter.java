package com.example.a3dmodel.adapter;

import static com.example.a3dmodel.MainActivity.bitmapALindex;
import static com.example.a3dmodel.MainActivity.bitmapArrayList;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.example.a3dmodel.MainActivity;
import com.example.a3dmodel.R;
import com.example.a3dmodel.adapter.GridAdapter.ImageViewHolder;
import com.squareup.picasso.Picasso;
//import com.example.a3dmodel.photo_fragment.ImagePagerFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A fragment for displaying a grid of images.
 */


public class GridAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    ImageView newImage;
    List<Bitmap> bitmapListOfImagesInGrid;

    /**
     * A listener that is attached to all ViewHolders to handle image loading events and clicks.
     */

    private interface ViewHolderListener {

        void onLoadCompleted(ImageView view, int adapterPosition);

        void onItemClicked(View view, int adapterPosition);
    }

    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;

    /**
     * Constructs a new grid adapter for the given {@link Fragment}.
     */
    public GridAdapter(Fragment fragment) {
        this.requestManager = Glide.with(fragment);
        this.viewHolderListener = new ViewHolderListenerImpl(fragment);
        bitmapListOfImagesInGrid = new ArrayList<>();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_card, parent, false);
        return new ImageViewHolder(view, requestManager, viewHolderListener);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.onBind();
    }

    @Override
    public int getItemCount() {
        return bitmapALindex;
    }

//    void setNewTakenPhoto(Bitmap img){
//        bitmapListOfImagesInGrid.add(img);
//        getImageViewHolder().setImage(bitmapALindex);
//
////        onBindViewHolder(, 4);
//    }


    /**
     * Default {@link ViewHolderListener} implementation.
     */
    static class ViewHolderListenerImpl implements ViewHolderListener {

        private Fragment fragment;
        private AtomicBoolean enterTransitionStarted;

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

        // TODO i guess if i want to pick photo code should be here or if open it
        @Override
        public void onItemClicked(View view, int position) {
//            Toast.makeText(view.getContext(), "Adapter position is : " + position, Toast.LENGTH_SHORT );
//            // Update the position.
//            MainActivity.currentPosition = position;
//
//            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
//            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
//            ((TransitionSet) fragment.getExitTransition()).excludeTarget(view, true);
//
////            ImageView transitioningView = view.findViewById(R.id.card_image);
////            assert fragment.getFragmentManager() != null;
////            fragment.getFragmentManager()
////                    .beginTransaction()
////                    .setReorderingAllowed(true) // Optimize for shared element transition
////                    .addSharedElement(transitioningView, transitioningView.getTransitionName())
////                    .replace(R.id.fragment_photo, new ImagePagerFragment(), ImagePagerFragment.class
////                            .getSimpleName())
////                    .addToBackStack(null)
////                    .commit();
        }
    }

    /*
     * another Holder for Data in grid View
     */
    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }

    /**
     * ViewHolder for the grid's images.
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private final ImageView image;
        private final RequestManager requestManager; // a class for managing and starting requests for Glide
        private final ViewHolderListener viewHolderListener;

        ImageViewHolder(View itemView, RequestManager requestManager,
                        ViewHolderListener viewHolderListener) {
            super(itemView);
            this.image = itemView.findViewById(R.id.card_image);
            this.image.setImageBitmap(bitmapArrayList[bitmapArrayList.length - 1]);
            this.requestManager = requestManager;
            this.viewHolderListener = viewHolderListener;
            itemView.findViewById(R.id.card_view).setOnClickListener(this);
        }

        /**
         * Binds this view holder to the given adapter position.
         * <p>
         * The binding will load the image into the image view, as well as set its transition name for
         * later.
         */
        void onBind() {
            int adapterPosition = getAdapterPosition();
            setImage(adapterPosition);
            // Set the string value of the image resource as the unique transition name for the view.
            image.setTransitionName(String.valueOf(image));
        }

        void setImage(final int adapterPosition) {
            // Load the image with Glide to prevent OOM error when the image drawables are very large.
            requestManager
                    .load(image)
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

        @Override
        public void onClick(View view) {
//            Toast.makeText(view.getContext(), "Adapter position is : " + getAdapterPosition(), Toast.LENGTH_SHORT );

            // Let the listener start the ImagePagerFragment.
            viewHolderListener.onItemClicked(view, getAdapterPosition());
        }
    }

}


//public class GridAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {
//
//    private ArrayList<ViewModel> items;
//    private OnItemClickListener onItemClickListener;
//
//    public GridAdapter(ArrayList<ViewModel> items) {
//        this.items = items;
//    }
//
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);
//        v.setOnClickListener(this);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull GridAdapter.ViewHolder holder, int position) {
//
//    }
//
//
//
//    public void updateData(ArrayList<ViewModel> viewModels) {
//        items.clear();
//        items.addAll(viewModels);
//        notifyDataSetChanged();
//    }
//    public void addItem(int position, ViewModel viewModel) {
//        items.add(position, viewModel);
//        notifyItemInserted(position);
//    }
//
//    public void removeItem(int position) {
//        items.remove(position);
//        notifyItemRemoved(position);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        ViewModel item = items.get(position);
//        holder.title.setText(item.getTitle());
//        Picasso.with(holder.image.getContext()).load(item.getImage()).into(holder.image);
//        holder.price.setText(item.getPrice());
//        holder.credit.setText(item.getCredit());
//        holder.description.setText(item.getDescription());
//
//        holder.itemView.setTag(item);
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//
//    @Override
//    public void onClick(final View v) {
//        // Give some time to the ripple to finish the effect
//        if (onItemClickListener != null) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    onItemClickListener.onItemClick(v, (ViewModel) v.getTag());
//                }
//            }, 0);
//        }
//    }
//
//    static class ImageViewHolder extends RecyclerView.ViewHolder implements
//            View.OnClickListener {
//
//        private final ImageView image;
//        private final RequestManager requestManager; // a class for managing and starting requests for Glide
//        private final ViewHolderListener viewHolderListener;
//
//        ImageViewHolder(View itemView, RequestManager requestManager,
//                        ViewHolderListener viewHolderListener) {
//            super(itemView);
//            this.image = itemView.findViewById(R.id.card_image);
//            this.image.setImageBitmap(bitmapArrayList[bitmapArrayList.length-1]);
//            this.requestManager = requestManager;
//            this.viewHolderListener = viewHolderListener;
//            itemView.findViewById(R.id.card_view).setOnClickListener(this);
//        }
//
//        /**
//         * Binds this view holder to the given adapter position.
//         *
//         * The binding will load the image into the image view, as well as set its transition name for
//         * later.
//         */
//        void onBind() {
//            int adapterPosition = getAdapterPosition();
//            setImage(adapterPosition);
//            // Set the string value of the image resource as the unique transition name for the view.
//            image.setTransitionName(String.valueOf(image));
//        }
//
//        void setImage(final int adapterPosition) {
//            // Load the image with Glide to prevent OOM error when the image drawables are very large.
//            requestManager
//                    .load(image)
//                    .listener(new RequestListener<Drawable>() {
//                        @Override
//                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
//                                                    Target<Drawable> target, boolean isFirstResource) {
//                            viewHolderListener.onLoadCompleted(image, adapterPosition);
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
//                                target, DataSource dataSource, boolean isFirstResource) {
//                            viewHolderListener.onLoadCompleted(image, adapterPosition);
//                            return false;
//                        }
//                    })
//                    .into(image);
//        }
//
//        @Override
//        public void onClick(View view) {
//            // Let the listener start the ImagePagerFragment.
//            viewHolderListener.onItemClicked(view, getAdapterPosition());
//        }
//    }
//
//    public interface OnItemClickListener {
//
//        void onItemClick(View view, ViewModel viewModel);
//
//    }
//}