package com.example.hellochat.Adapter.UserPage;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.DTO.UserPage.FollowData;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {
    private ArrayList<FollowData> mData;


    public FollowerAdapter(ArrayList<FollowData> Data) {
        this.mData = Data;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.name.setText(mData.get(position).name);
        //유저의 제 2,3의 모국어나 학습언어가 없을시 보이지않게 처리
        //모국어2
        if (mData.get(position).mylang2 == null) {
            holder.mylang_layout2.setVisibility(View.GONE);
        } else {
            holder.mylang2.setText(mData.get(position).mylang2);
        }
        //모국어3
        if (mData.get(position).mylang3 == null) {
            holder.mylang_layout3.setVisibility(View.GONE);
        } else {
            holder.mylang3.setText(mData.get(position).mylang3);
        }
        //학습언어2
        if (mData.get(position).studylang2 == null) {
            holder.study_lang_layout2.setVisibility(View.GONE);
        } else {
            holder.studylang2.setText(mData.get(position).studylang2);
            holder.study_lang_level2.setProgress(mData.get(position).studylang_level2);
        }
        //학습언어3
        if (mData.get(position).studylang3 == null) {
            holder.study_lang_layout3.setVisibility(View.GONE);
        } else {
            holder.studylang3.setText(mData.get(position).studylang3);
            holder.study_lang_level3.setProgress(mData.get(position).studylang_level3);
        }
        holder.mylang1.setText(mData.get(position).mylang);
        holder.studylang1.setText(mData.get(position).studylang);
        holder.study_lang_level.setProgress(mData.get(position).studylang_level);
        if (mData.get(position).profile != null && !mData.get(position).profile.equals("")) {
            //이미지를 넣어주기 위해 이미지url을 가져온다.
            String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position).profile;
            //영상 썸네일 세팅
            Glide.with(holder.profile)
                    .load(imageUrl)
                    .into(holder.profile);
        }
        if(mData.get(position).followed == 0){
            holder.plus.setImageResource(R.drawable.plus);
            holder.plus.setColorFilter(Color.parseColor("#5877E1"));
        }else {
            holder.plus.setImageResource(R.drawable.ok);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView profile , plus;
        protected TextView name , mylang1, mylang2, mylang3, studylang1, studylang2, studylang3;
        protected ProgressBar mylang_level, study_lang_level, study_lang_level2, study_lang_level3;
        protected LinearLayout mylang_layout2, mylang_layout3, study_lang_layout2, study_lang_layout3;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.profile = itemView.findViewById(R.id.profile);
            this.name = itemView.findViewById(R.id.name);
            this.mylang1 = itemView.findViewById(R.id.mylang);
            this.mylang2 = itemView.findViewById(R.id.mylang2);
            this.mylang3 = itemView.findViewById(R.id.mylang3);
            this.mylang_level = itemView.findViewById(R.id.mylang_level);
            this.studylang1 = itemView.findViewById(R.id.study_lang);
            this.study_lang_level = itemView.findViewById(R.id.studylang_level);
            this.studylang2 = itemView.findViewById(R.id.study_lang2);
            this.study_lang_level2 = itemView.findViewById(R.id.studylang_level2);
            this.studylang3 = itemView.findViewById(R.id.study_lang3);
            this.study_lang_level3 = itemView.findViewById(R.id.studylang_level3);
            this.mylang_layout2 = itemView.findViewById(R.id.mylang2_layout);
            this.mylang_layout3 = itemView.findViewById(R.id.mylang3_layout);
            this.study_lang_layout2 = itemView.findViewById(R.id.study_lang2_layout);
            this.study_lang_layout3 = itemView.findViewById(R.id.study_lang3_layout);
            this.plus = itemView.findViewById(R.id.plus);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    if(mListener != null){
                        mListener.onItemClick(v , pos);
                    }
                }
            });
            plus.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    if(followClickListener != null){
                        followClickListener.onFollowClick(v , pos);
                    }
                }
            });

        }
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
    private OnFollowClickListener followClickListener;
    public interface OnFollowClickListener {
        void onFollowClick(View v, int position);
    }
    public void setOnFollowClick(OnFollowClickListener listener) {
        this.followClickListener = listener;
    }


}
