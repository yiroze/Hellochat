package com.example.hellochat.Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.DTO.ChatList;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private static final String TAG = "ChatListAdapter";

    private ArrayList<ChatList> mList;

    public ChatListAdapter(ArrayList<ChatList> list) {
        this.mList = list;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        date.setTime(mList.get(position).date);
        holder.name.setText(mList.get(position).name);
        holder.content.setText(mList.get(position).content);
        holder.date.setText(simpleDateFormat.format(date));
        if(mList.get(position).new_msg_cnt != 0){
            holder.new_msg_cnt.setVisibility(View.VISIBLE);
            holder.new_msg_cnt.setText(Integer.toString(mList.get(position).new_msg_cnt));
            Log.d(TAG, "onBindViewHolder: "+mList.get(position).new_msg_cnt);
        }
        if (mList.get(position).profile != null && !mList.get(position).profile.equals("")) {
            String imageUrl = "http://3.37.204.197/hellochat/" + mList.get(position).profile;
            Glide.with(holder.profile)
                    .load(imageUrl)
                    .into(holder.profile);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    private OnChatListClick mListener = null;
    public interface OnChatListClick {
        void onChatListClick(View v, int pos);
    }
    public void setonChatListClick(OnChatListClick listener) {
        this.mListener = listener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView content, date, name , new_msg_cnt ;
        protected ImageView profile;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            profile = itemView.findViewById(R.id.profile);
            new_msg_cnt = itemView.findViewById(R.id.new_msg_cnt);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if(mListener != null){
                        mListener.onChatListClick(v , pos);
                    }
                }
            });

        }
    }
}