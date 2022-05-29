//package com.example.a3dmodel.adapter;
//
//import static com.example.a3dmodel.adapter.GridAdapter.imageDataList;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentStatePagerAdapter;
//import com.example.a3dmodel.photofragment.ImageFragment;
//
//public class ImagePagerAdapter extends FragmentStatePagerAdapter {
//
//    public ImagePagerAdapter(Fragment fragment) {
//        // Note: Initialize with the child fragment manager.
//        super(fragment.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//    }
//
//    @Override
//    public int getCount() {
//        return imageDataList.size();
//    }
//
//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        return ImageFragment.newInstance(imageDataList.get(position).getImageBitmap());
//    }
//}