package com.example.hellochat.Adapter.Feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.DTO.Feed.NotificationData;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NotificationAdapter extends  RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private ArrayList<NotificationData> mData;
    final static int following_new = 9;
    final static int comment = 11;
    final static int reply = 12;
    final static int like = 10;

    public  NotificationAdapter(ArrayList<NotificationData> mData){
        this.mData = mData;
    }

    @NonNull
    @NotNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.norification_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotificationAdapter.ViewHolder holder, int position) {
        if(mData.size()-1 == position){
            holder.line.setVisibility(View.GONE);
        }
        if(mData.get(position).type == following_new){
            holder.name.setText(mData.get(position).name);
            holder.content.setText("님이 새 글을 게시하였습니다.");
            holder.date.setText(mData.get(position).date);
            if(!mData.get(position).profile.equals("") &&mData.get(position).profile != null){
                //이미지를 넣어주기 위해 이미지url을 가져온다.
                String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position).profile;
                Glide.with(holder.profile)
                        .load(imageUrl)
                        .into(holder.profile);
            }
            if(!mData.get(position).image.equals("") &&mData.get(position).image != null && !mData.get(position).image.equals("[]")){
                String image[] = mData.get(position).image.replace("[" , "").replace("]" , "").split(",");
                String imageUrl = "http://3.37.204.197/hellochat/" + image[0];
                Glide.with(holder.board_img)
                        .load(imageUrl)
                        .into(holder.board_img);
                holder.board_img.setVisibility(View.VISIBLE);
            }else if (!mData.get(position).content.equals("")&&mData.get(position).content != null) {
                holder.board_content.setText(mData.get(position).content);
                holder.board_content.setVisibility(View.VISIBLE);
            }else if(!mData.get(position).record.equals("")&&mData.get(position).record!=null){
                holder.board_content.setText("음성메시지");
                holder.board_content.setVisibility(View.VISIBLE);
            }
        }else if(mData.get(position).type == like){
            holder.name.setText(mData.get(position).name);
            holder.content.setText("님이 회원님의 게시글을 좋아합니다.");
            holder.date.setText(mData.get(position).date);
            if(!mData.get(position).profile.equals("") &&mData.get(position).profile != null){
                //이미지를 넣어주기 위해 이미지url을 가져온다.
                String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position).profile;
                Glide.with(holder.profile)
                        .load(imageUrl)
                        .into(holder.profile);
            }
            if(!mData.get(position).image.equals("") &&mData.get(position).image != null && !mData.get(position).image.equals("[]")){
                String image[] = mData.get(position).image.replace("[" , "").replace("]" , "").split(",");
                String imageUrl = "http://3.37.204.197/hellochat/" + image[0];
                Glide.with(holder.board_img)
                        .load(imageUrl)
                        .into(holder.board_img);
                holder.board_img.setVisibility(View.VISIBLE);
            }else if (!mData.get(position).content.equals("")&&mData.get(position).content != null) {
                holder.board_content.setText(mData.get(position).content);
                holder.board_content.setVisibility(View.VISIBLE);
            }else if(!mData.get(position).record.equals("")&&mData.get(position).record!=null){
                holder.board_content.setText("음성메시지");
                holder.board_content.setVisibility(View.VISIBLE);
            }
        }else if(mData.get(position).type == comment || mData.get(position).type == reply ){
            holder.name.setText(mData.get(position).name);
            holder.content.setText(mData.get(position).comment);
            holder.date.setText(mData.get(position).date);
            if(!mData.get(position).profile.equals("") && mData.get(position).profile != null){
                //이미지를 넣어주기 위해 이미지url을 가져온다.
                String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position).profile;
                Glide.with(holder.profile)
                        .load(imageUrl)
                        .into(holder.profile);
            }
            if(!mData.get(position).image.equals("") &&mData.get(position).image != null && !mData.get(position).image.equals("[]")){
                String image[] = mData.get(position).image.replace("[" , "").replace("]" , "").split(",");
                String imageUrl = "http://3.37.204.197/hellochat/" + image[0];
                Glide.with(holder.board_img)
                        .load(imageUrl)
                        .into(holder.board_img);
                holder.board_img.setVisibility(View.VISIBLE);
            }else if (!mData.get(position).content.equals("")&&mData.get(position).content != null) {
                holder.board_content.setText(mData.get(position).content);
                holder.board_content.setVisibility(View.VISIBLE);
            }else if(!mData.get(position).record.equals("")&&mData.get(position).record!=null){
                holder.board_content.setText("음성메시지");
                holder.board_content.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name , content, date , board_content;
        ImageView profile , board_img;
        View line;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);
            board_content = itemView.findViewById(R.id.board_content);
            profile =itemView.findViewById(R.id.profile);
            board_img =itemView.findViewById(R.id.board_image);
            line = itemView.findViewById(R.id.view);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if(mListener != null){
                        mListener.onNotificationClick(v , pos);
                    }
                }
            });

            profile.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    if(mListener2 != null){
                        mListener2.onProfileClick(v , pos);
                    }
                }
            });

        }
    }

    private OnNotificationClick mListener = null;
    public interface OnNotificationClick {
        void onNotificationClick(View v, int pos);
    }
    public void setOnNotification(OnNotificationClick listener) {
        this.mListener = listener;
    }


    private OnProfileClick mListener2 = null;
    public interface OnProfileClick {
        void onProfileClick(View v, int pos);
    }
    public void setOnProfileClick(OnProfileClick listener) {
        this.mListener2 = listener;
    }


}
