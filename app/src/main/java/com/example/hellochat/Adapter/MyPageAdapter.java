package com.example.hellochat.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.DTO.MypageData;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MyPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context c;
    private final int TYPE_MYPAGE = 0;
    private final int TYPE_CONTENTS = 1;
    private ArrayList<MypageData> mData;
    private static final String TAG = "MyPageAdapter";


    public MyPageAdapter(ArrayList<MypageData> Data){
        this.mData = Data;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getView_type();
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view;
        c = parent.getContext();
        if (viewType == TYPE_MYPAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mypage_myinfo, parent, false);
            holder = new MyPageViewHolder(view);
        } else if (viewType == TYPE_CONTENTS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeeditem, parent, false);
            holder = new ContentsViewHolder(view);
        }else {
            return null;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        int my_idx = Integer.parseInt(getPref());
        if(holder instanceof MyPageViewHolder){
            MyPageViewHolder myPageViewHolder = (MyPageViewHolder) holder;
            //유저의 제 2,3의 모국어나 학습언어가 없을시 보이지않게 처리
            //모국어
            myPageViewHolder.mylang.setText(mData.get(position).mylang);
            //모국어2
            if (mData.get(position).mylang2 == null) {
                myPageViewHolder.layout_mylang2.setVisibility(View.GONE);
            } else {
                myPageViewHolder.mylang2.setText(mData.get(position).mylang2);
            }
            //모국어3
            if (mData.get(position).mylang3 == null) {
                myPageViewHolder.layout_mylang3.setVisibility(View.GONE);
            } else {
                myPageViewHolder.mylang3.setText(mData.get(position).mylang3);
            }
            //학습언어
            myPageViewHolder.studylang.setText(mData.get(position).studylang);
            myPageViewHolder.progress_studylang.setProgress(mData.get(position).studylang_level);
            //학습언어2
            if (mData.get(position).studylang2 == null) {
                myPageViewHolder.layout_studylang2.setVisibility(View.GONE);
            } else {
                myPageViewHolder.studylang2.setText(mData.get(position).studylang2);
                myPageViewHolder.progress_studylang2.setProgress(mData.get(position).studylang_level2);
            }
            //학습언어3
            if (mData.get(position).studylang3 == null) {
                myPageViewHolder.layout_studylang3.setVisibility(View.GONE);
            } else {
                myPageViewHolder.studylang3.setText(mData.get(position).studylang3);
                myPageViewHolder.progress_studylang3.setProgress(mData.get(position).studylang_level3);
            }
            //이름
            myPageViewHolder.name.setText(mData.get(position).name);
            //자기소개
            myPageViewHolder.introduce.setText(mData.get(position).contents);
            //게시물 갯수
            myPageViewHolder.feed_cnt.setText("게시물 "+mData.get(position).feed_cnt+"개");
            //총 좋아요 갯수
            myPageViewHolder.like_cnt.setText("좋아요 "+mData.get(position).all_like_cnt+"개");
            if (mData.get(position).profile != null && !mData.get(position).profile.equals("")) {
                //이미지를 넣어주기 위해 이미지url을 가져온다.
                String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position).profile;
                //영상 썸네일 세팅
                Glide.with(myPageViewHolder.profile)
                        .load(imageUrl)
                        .into(myPageViewHolder.profile);
            }

        }else if(holder instanceof ContentsViewHolder){
            ContentsViewHolder contentsViewHolder = (ContentsViewHolder) holder;
            //유저의 제 2,3의 모국어나 학습언어가 없을시 보이지않게 처리
            contentsViewHolder.mylang.setText(mData.get(position).mylang);

            //모국어2
            if (mData.get(position).mylang2 == null) {
                contentsViewHolder.layout_mylang2.setVisibility(View.GONE);
            } else {
                contentsViewHolder.mylang2.setText(mData.get(position).mylang2);
            }
            //모국어3
            if (mData.get(position).mylang3 == null) {
                contentsViewHolder.layout_mylang3.setVisibility(View.GONE);
            } else {
                contentsViewHolder.mylang3.setText(mData.get(position).mylang3);
            }
            //학습언어2
            contentsViewHolder.studylang.setText(mData.get(position).studylang);
            contentsViewHolder.progress_studylang.setProgress(mData.get(position).studylang_level);
            if (mData.get(position).studylang2 == null) {
                contentsViewHolder.layout_studylang2.setVisibility(View.GONE);
            } else {
                contentsViewHolder.studylang2.setText(mData.get(position).studylang2);
                contentsViewHolder.progress_studylang2.setProgress(mData.get(position).studylang_level2);
            }
            //학습언어3
            if (mData.get(position).studylang3 == null) {
                contentsViewHolder.layout_studylang3.setVisibility(View.GONE);
            } else {
                contentsViewHolder.studylang3.setText(mData.get(position).studylang3);
                contentsViewHolder.progress_studylang3.setProgress(mData.get(position).studylang_level3);
            }
            if (!mData.get(position).comment_cnt.equals("0")) {
                contentsViewHolder.comment_count.setText(mData.get(position).comment_cnt);
            }
            if (!mData.get(position).like_cnt.equals("0")) {
                contentsViewHolder.heart_count.setText(mData.get(position).like_cnt);
            }
            contentsViewHolder.name.setText(mData.get(position).name);
            contentsViewHolder.contents.setText(mData.get(position).contents);
            contentsViewHolder.date.setText(mData.get(position).date);

            if (mData.get(position).islike == 0) {
                contentsViewHolder.heart.setImageResource(R.drawable.heart_off);
            } else {
                contentsViewHolder.heart.setImageResource(R.drawable.heart_on);
            }

            if (mData.get(position).profile != null && !mData.get(position).profile.equals("")) {
                //이미지를 넣어주기 위해 이미지url을 가져온다.
                String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position).profile;
                //영상 썸네일 세팅
                Glide.with(contentsViewHolder.profile)
                        .load(imageUrl)
                        .into(contentsViewHolder.profile);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyPageViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout update_myinfo_bnt;
        TextView name, mylang, mylang2, mylang3, studylang, studylang2, studylang3, introduce ,feed_cnt ,like_cnt;
        LinearLayout layout_mylang2, layout_mylang3, layout_studylang2, layout_studylang3;
        ProgressBar progress_mylang, progress_mylang2, progress_mylang3, progress_studylang, progress_studylang2, progress_studylang3;
        ImageView profile;

        public MyPageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.update_myinfo_bnt = itemView.findViewById(R.id.update_myinfo_bnt);
            this.name = itemView.findViewById(R.id.name);
            this.introduce = itemView.findViewById(R.id.introduce);
            this.mylang = itemView.findViewById(R.id.mylang);
            this.mylang2 = itemView.findViewById(R.id.mylang2);
            this.mylang3 = itemView.findViewById(R.id.mylang3);
            this.studylang = itemView.findViewById(R.id.study_lang);
            this.studylang2 = itemView.findViewById(R.id.study_lang2);
            this.studylang3 = itemView.findViewById(R.id.study_lang3);
            this.layout_mylang2 = itemView.findViewById(R.id.mylang2_layout);
            this.layout_mylang3 = itemView.findViewById(R.id.mylang3_layout);
            this.layout_studylang2 = itemView.findViewById(R.id.study_lang2_layout);
            this.layout_studylang3 = itemView.findViewById(R.id.study_lang3_layout);
            this.progress_mylang = itemView.findViewById(R.id.mylang_level);
            this.progress_mylang2 = itemView.findViewById(R.id.mylang_level2);
            this.progress_mylang3 = itemView.findViewById(R.id.mylang_level3);
            this.progress_studylang = itemView.findViewById(R.id.studylang_level);
            this.progress_studylang2 = itemView.findViewById(R.id.studylang_level2);
            this.progress_studylang3 = itemView.findViewById(R.id.studylang_level3);
            this.profile = itemView.findViewById(R.id.profile);
            this.feed_cnt = itemView.findViewById(R.id.feed_cnt);
            this.like_cnt = itemView.findViewById(R.id.like_cnt);

            update_myinfo_bnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(myPageClickListener!=null){
                            myPageClickListener.onmyPageClick(v , pos);
                        }
                    }
                }
            });
        }
    }

    public class ContentsViewHolder extends RecyclerView.ViewHolder {
        TextView name, mylang, mylang2, mylang3, studylang, studylang2, studylang3, date, contents ,heart_count, comment_count;
        LinearLayout layout_mylang2, layout_mylang3, layout_studylang2, layout_studylang3;
        ProgressBar progress_mylang, progress_mylang2, progress_mylang3, progress_studylang, progress_studylang2, progress_studylang3;
        ImageView profile, more ,heart;

        public ContentsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.mylang = itemView.findViewById(R.id.mylang);
            this.mylang2 = itemView.findViewById(R.id.mylang2);
            this.mylang3 = itemView.findViewById(R.id.mylang3);
            this.studylang = itemView.findViewById(R.id.study_lang);
            this.studylang2 = itemView.findViewById(R.id.study_lang2);
            this.studylang3 = itemView.findViewById(R.id.study_lang3);
            this.date = itemView.findViewById(R.id.date);
            this.contents = itemView.findViewById(R.id.contents);
            this.layout_mylang2 = itemView.findViewById(R.id.mylang2_layout);
            this.layout_mylang3 = itemView.findViewById(R.id.mylang3_layout);
            this.layout_studylang2 = itemView.findViewById(R.id.study_lang2_layout);
            this.layout_studylang3 = itemView.findViewById(R.id.study_lang3_layout);
            this.progress_mylang = itemView.findViewById(R.id.mylang_level);
            this.progress_mylang2 = itemView.findViewById(R.id.mylang_level2);
            this.progress_mylang3 = itemView.findViewById(R.id.mylang_level3);
            this.progress_studylang = itemView.findViewById(R.id.studylang_level);
            this.progress_studylang2 = itemView.findViewById(R.id.studylang_level2);
            this.progress_studylang3 = itemView.findViewById(R.id.studylang_level3);
            this.more = itemView.findViewById(R.id.more);
            this.profile = itemView.findViewById(R.id.profile);
            this.heart = itemView.findViewById(R.id.heart);
            this.heart_count = itemView.findViewById(R.id.heart_count);
            this.comment_count = itemView.findViewById(R.id.comment_count);
            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(likeClickListener!=null){
                            likeClickListener.onLikeClick(v , pos);
                        }
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(feedItemClickListener!=null){
                            feedItemClickListener.onFeedItemClick(v , pos);
                        }
                    }
                }
            });
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(moreBntClickListener!=null){
                            moreBntClickListener.onMoreBntClick(v , pos);
                        }
                    }
                }
            });

        }
    }


    public String getPref() {
        SharedPreferences pref = c.getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }
    private OnMyPageClickListener myPageClickListener = null;
    private OnFeedItemClickListener feedItemClickListener = null;
    private OnLikeClickListener likeClickListener = null;
    private OnMoreBntClickListener moreBntClickListener = null;

    public interface OnMyPageClickListener { void onmyPageClick(View v, int pos);}
    public interface OnFeedItemClickListener { void onFeedItemClick(View v, int pos);}
    public interface OnLikeClickListener { void onLikeClick(View v, int pos);}
    public interface OnMoreBntClickListener { void onMoreBntClick(View v, int pos);}

    public void setOnMyPageClickListener(MyPageAdapter.OnMyPageClickListener listener) { this.myPageClickListener = listener; }
    public void setOnFeedItemClickListener(MyPageAdapter.OnFeedItemClickListener listener) { this.feedItemClickListener = listener; }
    public void setOnLikeClickListener(MyPageAdapter.OnLikeClickListener listener) { this.likeClickListener = listener; }
    public void setOnMoreBntClickListener(MyPageAdapter.OnMoreBntClickListener listener){ this.moreBntClickListener = listener; }


}
