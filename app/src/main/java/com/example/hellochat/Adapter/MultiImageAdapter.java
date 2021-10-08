package com.example.hellochat.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MultiImageAdapter extends RecyclerView.Adapter<MultiImageAdapter.ViewHolder> {
    private ArrayList<Uri> mData;
    private Context mContext;
    private static final String TAG = "'MultiImageAdapter'";
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, delete;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.image_item);
            this.delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    Log.d(TAG, "ViewHolder: "+mListener);
                    if(mListener != null){
                        mListener.onItemClick(v , pos);
                    }
                }
            });

        }
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public MultiImageAdapter(ArrayList<Uri> list, Context context) {
        mData = list;
        mContext = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_item, parent, false);
        MultiImageAdapter.ViewHolder vh = new MultiImageAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Uri image_uri = mData.get(position);
        Glide.with(mContext)
                .load(image_uri)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
