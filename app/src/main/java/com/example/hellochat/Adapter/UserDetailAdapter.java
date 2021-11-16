package com.example.hellochat.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.DTO.MypageData;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class UserDetailAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "UserDetailAdapter";
    private Context c;
    private final int TYPE_USERINFO = 0;
    private final int TYPE_CONTENTS = 1;
    private ArrayList<MypageData> mData;
    MediaPlayer mPlayer;
    int idx;

    public UserDetailAdapter(ArrayList<MypageData> Data){
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
        if (viewType == TYPE_USERINFO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userinfo, parent, false);
            holder = new UserInfoViewHolder(view);
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
        if(holder instanceof UserInfoViewHolder){
            UserInfoViewHolder userInfoViewHolder = (UserInfoViewHolder) holder;
            //유저의 제 2,3의 모국어나 학습언어가 없을시 보이지않게 처리
            //모국어
            userInfoViewHolder.mylang.setText(mData.get(position).mylang);
            //모국어2
            if (mData.get(position).mylang2 == null) {
                userInfoViewHolder.layout_mylang2.setVisibility(View.GONE);
            } else {
                userInfoViewHolder.mylang2.setText(mData.get(position).mylang2);
            }
            //모국어3
            if (mData.get(position).mylang3 == null) {
                userInfoViewHolder.layout_mylang3.setVisibility(View.GONE);
            } else {
                userInfoViewHolder.mylang3.setText(mData.get(position).mylang3);
            }
            //학습언어
            userInfoViewHolder.studylang.setText(mData.get(position).studylang);
            userInfoViewHolder.progress_studylang.setProgress(mData.get(position).studylang_level);
            //학습언어2
            if (mData.get(position).studylang2 == null) {
                userInfoViewHolder.layout_studylang2.setVisibility(View.GONE);
            } else {
                userInfoViewHolder.studylang2.setText(mData.get(position).studylang2);
                userInfoViewHolder.progress_studylang2.setProgress(mData.get(position).studylang_level2);
            }
            //학습언어3
            if (mData.get(position).studylang3 == null) {
                userInfoViewHolder.layout_studylang3.setVisibility(View.GONE);
            } else {
                userInfoViewHolder.studylang3.setText(mData.get(position).studylang3);
                userInfoViewHolder.progress_studylang3.setProgress(mData.get(position).studylang_level3);
            }
            //이름
            userInfoViewHolder.name.setText(mData.get(position).name);
            //자기소개
            userInfoViewHolder.introduce.setText(mData.get(position).contents);
            //게시물 갯수
            userInfoViewHolder.feed_cnt.setText(MessageFormat.format("게시물 {0}개", mData.get(position).feed_cnt));
            //총 좋아요 갯수
            userInfoViewHolder.like_cnt.setText(MessageFormat.format("좋아요 {0}개", mData.get(position).all_like_cnt));
            if (mData.get(position).profile != null && !mData.get(position).profile.equals("")) {
                //이미지를 넣어주기 위해 이미지url을 가져온다.
                String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position).profile;
                //영상 썸네일 세팅
                Glide.with(userInfoViewHolder.profile)
                        .load(imageUrl)
                        .into(userInfoViewHolder.profile);
            }

        }else if(holder instanceof ContentsViewHolder){
            ContentsViewHolder contentsViewHolder = (ContentsViewHolder) holder;
            //유저의 제 2,3의 모국어나 학습언어가 없을시 보이지않게 처리
            contentsViewHolder.mylang.setText(mData.get(position).mylang);

            idx = Integer.parseInt(getPref());
            if (mData.get(position).user_idx != idx) {
                contentsViewHolder.more.setVisibility(View.INVISIBLE);
            } else {
                contentsViewHolder.more.setVisibility(View.VISIBLE);
            }
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
            String a = mData.get(position).image.replace("[", "").replace("]", "").replace(" ", "");
            String[] item = a.split(",");
            ArrayList<String> image_item = new ArrayList<>(Arrays.asList(item));
            if (mData.get(position).image.equals("") || mData.get(position).image.equals("[]")) {
                contentsViewHolder.image.setVisibility(View.GONE);
            } else {
                if(image_item.size() ==1){
                    contentsViewHolder.image.setAdapter(new BigImageAdapter(image_item, c));
                    contentsViewHolder.image.setLayoutManager(new GridLayoutManager(c, 1));
                    contentsViewHolder.image.setHasFixedSize(false);
                }else {
                    contentsViewHolder.image.setAdapter(new ImageAdapter(image_item, c));
                    contentsViewHolder.image.setLayoutManager(new GridLayoutManager(c, 3));
                    contentsViewHolder.image.setHasFixedSize(true);
                }
            }
            if (mData.get(position).islike == 0) {
                contentsViewHolder.heart.setImageResource(R.drawable.heart_off);
            } else {
                contentsViewHolder.heart.setImageResource(R.drawable.heart_on);
            }
            if (mData.get(position).record.equals("") || mData.get(position).record == null) {
                contentsViewHolder.player.setVisibility(View.GONE);
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


    public class UserInfoViewHolder extends RecyclerView.ViewHolder {
        TextView name, mylang, mylang2, mylang3, studylang, studylang2, studylang3, introduce ,feed_cnt ,like_cnt;
        LinearLayout layout_mylang2, layout_mylang3, layout_studylang2, layout_studylang3;
        ProgressBar progress_mylang, progress_mylang2, progress_mylang3, progress_studylang, progress_studylang2, progress_studylang3;
        ImageView profile;

        public UserInfoViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
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

        }
    }

    public class ContentsViewHolder extends RecyclerView.ViewHolder {
        TextView name, mylang, mylang2, mylang3, studylang, studylang2, studylang3, date, contents ,heart_count, comment_count;
        LinearLayout layout_mylang2, layout_mylang3, layout_studylang2, layout_studylang3;
        ProgressBar progress_mylang, progress_mylang2, progress_mylang3, progress_studylang, progress_studylang2, progress_studylang3;
        ImageView profile, more ,heart, player_control;
        protected ConstraintLayout player;
        protected SeekBar seekBar;
        protected RecyclerView image;

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
            this.player = itemView.findViewById(R.id.player_layout);
            this.player_control = itemView.findViewById(R.id.player_control);
            this.seekBar = itemView.findViewById(R.id.SeekBar);
            this.image = itemView.findViewById(R.id.image_recyclerview);

            contents.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(longClickListener != null){
                            Log.d(TAG, "onLongClick: ");
                            longClickListener.onContentLongClick(v , pos);
                        }
                    }
                    return true;
                }
            });

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
            player_control.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                try {
                    if (mPlayer != null) {    // 사용하기 전에
                        mPlayer.release();  // 리소스 해제
                        mPlayer = null;
                        Log.d(TAG, "onCreate: 리소스해제");
                    }
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource("http://3.37.204.197/hellochat/"+mData.get(pos).record); // 음악 파일 위치 지정
                    mPlayer.prepare();  // 미리 준비
                    mPlayer.start();    // 재생
                } catch (IOException e) {
                    e.printStackTrace();
                }
                seekBar.setMax(mPlayer.getDuration());  // 음악의 총 길이를 시크바 최대값에 적용
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser)  // 사용자가 시크바를 움직이면
                            mPlayer.seekTo(progress);   // 재생위치를 바꿔준다(움직인 곳에서의 음악재생)
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                mPlayer.start();
                new Thread(new Runnable() {  // 쓰레드 생성
                    @Override
                    public void run() {
                        while (mPlayer.isPlaying()) {  // 음악이 실행중일때 계속 돌아가게 함
                            try {
                                Thread.sleep(500); // 1초마다 시크바 움직이게 함
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // 현재 재생중인 위치를 가져와 시크바에 적용
                            seekBar.setProgress(mPlayer.getCurrentPosition());
                        }
                    }
                }).start();

            });
        }
    }


    public String getPref() {
        SharedPreferences pref = c.getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }
    private MyPageAdapter.OnMyPageClickListener myPageClickListener = null;
    private MyPageAdapter.OnFeedItemClickListener feedItemClickListener = null;
    private MyPageAdapter.OnLikeClickListener likeClickListener = null;
    private MyPageAdapter.OnMoreBntClickListener moreBntClickListener = null;
    private OnContentLongClickListener longClickListener;

    public interface OnMyPageClickListener { void onmyPageClick(View v, int pos);}
    public interface OnFeedItemClickListener { void onFeedItemClick(View v, int pos);}
    public interface OnLikeClickListener { void onLikeClick(View v, int pos);}
    public interface OnMoreBntClickListener { void onMoreBntClick(View v, int pos);}
    public interface OnContentLongClickListener {void onContentLongClick(View v, int position);}

    public void setOnMyPageClickListener(MyPageAdapter.OnMyPageClickListener listener) { this.myPageClickListener = listener; }
    public void setOnFeedItemClickListener(MyPageAdapter.OnFeedItemClickListener listener) { this.feedItemClickListener = listener; }
    public void setOnLikeClickListener(MyPageAdapter.OnLikeClickListener listener) { this.likeClickListener = listener; }
    public void setOnMoreBntClickListener(MyPageAdapter.OnMoreBntClickListener listener){ this.moreBntClickListener = listener; }
    public void setOnLongClickListener(OnContentLongClickListener listener) { this.longClickListener = listener; }


}
