package com.example.hellochat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.Activity.Activity_ImageViewer;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<String> mData;
    private Context mContext;
    private static final String TAG = "ImageAdapter";

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.image_item);

        }
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public ImageAdapter(ArrayList<String> list, Context context) {
        mData = list;
        mContext = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder: ");
        String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position);
        Glide.with(holder.imageView)
                .load(imageUrl)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, Activity_ImageViewer.class);
            intent.putExtra("image" , mData.toString());
            intent.putExtra("position" , position);
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
