package com.example.hellochat.Adapter.Chatting;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.Activity.Activity_ImageViewer;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatImageAdapter extends RecyclerView.Adapter<ChatImageAdapter.ViewHolder> {
    private ArrayList<String> mData;
    private Context mContext;
    private static final String TAG = ChatImageAdapter.class.getSimpleName();
    private final int SINGLE = 1;
    private final int BIG = 2;
    private final int SMALL = 3;

    public ChatImageAdapter(ArrayList<String> list, Context context) {
        this.mData = list;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.size() == 1) {
            return SINGLE;
        } else if (mData.size() == 2 || mData.size() == 4) {
            return BIG;
        } else {
            return SMALL;
        }
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
        ViewHolder viewHolder;
        if(viewType == SINGLE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_image_item3, parent, false);
        }
        else if (viewType == BIG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_image_item2, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_image_item, parent, false);
        }
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        if (mData.size() > 9) {
            if (position == 8) {
                holder.total_cnt.setVisibility(View.VISIBLE);
                holder.total_cnt.setText(new StringBuilder().append("+").append((mData.size())-9).toString());
            }
            if (position > 8) {
                holder.imageView.setVisibility(View.GONE);
            }
        }

        String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position);
        Glide.with(holder.imageView)
                .load(imageUrl)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, Activity_ImageViewer.class);
            intent.putExtra("image", mData.toString());
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView total_cnt;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.image_item);
            this.total_cnt = itemView.findViewById(R.id.total_item_count);
        }
    }


}
