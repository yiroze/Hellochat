package com.example.hellochat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImageViewerAdapter extends PagerAdapter {
    private static final String TAG = ImageViewerAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<String> image;


    public ImageViewerAdapter(Context context , ArrayList<String> image){
        this.mContext = context;
        this.image = image;
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.imageview, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        String imageUrl = "http://3.37.204.197/hellochat/" + image.get(position);
        Glide.with(imageView)
                .load(imageUrl)
                .into(imageView);
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return image.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return (view == (View)object);
    }
}
