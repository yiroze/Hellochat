package com.example.hellochat.Adapter.UserPage;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.Adapter.Feed.FeedCommentAdapter;
import com.example.hellochat.Adapter.Image.BigImageAdapter;
import com.example.hellochat.Adapter.Image.ImageAdapter;
import com.example.hellochat.DTO.UserPage.MypageData;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class MyPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context c;
    private final int TYPE_MYPAGE = 0;
    private final int TYPE_CONTENTS = 1;
    private ArrayList<MypageData> mData;
    private static final String TAG = "MyPageAdapter";
    MediaPlayer mPlayer;


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
            //????????? ??? 2,3??? ???????????? ??????????????? ????????? ??????????????? ??????
            //?????????
            myPageViewHolder.mylang.setText(mData.get(position).mylang);
            //?????????2
            if (mData.get(position).mylang2 == null) {
                myPageViewHolder.layout_mylang2.setVisibility(View.GONE);
            } else {
                myPageViewHolder.mylang2.setText(mData.get(position).mylang2);
            }
            //?????????3
            if (mData.get(position).mylang3 == null) {
                myPageViewHolder.layout_mylang3.setVisibility(View.GONE);
            } else {
                myPageViewHolder.mylang3.setText(mData.get(position).mylang3);
            }
            //????????????
            myPageViewHolder.studylang.setText(mData.get(position).studylang);
            myPageViewHolder.progress_studylang.setProgress(mData.get(position).studylang_level);
            //????????????2
            if (mData.get(position).studylang2 == null) {
                myPageViewHolder.layout_studylang2.setVisibility(View.GONE);
            } else {
                myPageViewHolder.studylang2.setText(mData.get(position).studylang2);
                myPageViewHolder.progress_studylang2.setProgress(mData.get(position).studylang_level2);
            }
            //????????????3
            if (mData.get(position).studylang3 == null) {
                myPageViewHolder.layout_studylang3.setVisibility(View.GONE);
            } else {
                myPageViewHolder.studylang3.setText(mData.get(position).studylang3);
                myPageViewHolder.progress_studylang3.setProgress(mData.get(position).studylang_level3);
            }
            //??????
            myPageViewHolder.name.setText(mData.get(position).name);
            //????????????
            myPageViewHolder.introduce.setText(mData.get(position).contents);
            //????????? ??????
            myPageViewHolder.feed_cnt.setText(mData.get(position).feed_cnt+"");
            //??? ????????? ??????
            myPageViewHolder.like_cnt.setText(mData.get(position).all_like_cnt+"");
            if (mData.get(position).profile != null && !mData.get(position).profile.equals("")) {
                //???????????? ???????????? ?????? ?????????url??? ????????????.
                String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position).profile;
                //?????? ????????? ??????
                Glide.with(myPageViewHolder.profile)
                        .load(imageUrl)
                        .into(myPageViewHolder.profile);
            }
            myPageViewHolder.follower_cnt.setText(Integer.toString(mData.get(position).follower));
            myPageViewHolder.following_cnt.setText(Integer.toString(mData.get(position).following));

        }else if(holder instanceof ContentsViewHolder){
            ContentsViewHolder contentsViewHolder = (ContentsViewHolder) holder;
            //????????? ??? 2,3??? ???????????? ??????????????? ????????? ??????????????? ??????
            contentsViewHolder.mylang.setText(mData.get(position).mylang);
            if(Integer.parseInt(getPref())!=mData.get(position).user_idx){
                contentsViewHolder.more.setVisibility(View.GONE);
            }
            //?????????2
            if (mData.get(position).mylang2 == null) {
                contentsViewHolder.layout_mylang2.setVisibility(View.GONE);
            } else {
                contentsViewHolder.mylang2.setText(mData.get(position).mylang2);
            }
            //?????????3
            if (mData.get(position).mylang3 == null) {
                contentsViewHolder.layout_mylang3.setVisibility(View.GONE);
            } else {
                contentsViewHolder.mylang3.setText(mData.get(position).mylang3);
            }
            //????????????2
            contentsViewHolder.studylang.setText(mData.get(position).studylang);
            contentsViewHolder.progress_studylang.setProgress(mData.get(position).studylang_level);
            if (mData.get(position).studylang2 == null) {
                contentsViewHolder.layout_studylang2.setVisibility(View.GONE);
            } else {
                contentsViewHolder.studylang2.setText(mData.get(position).studylang2);
                contentsViewHolder.progress_studylang2.setProgress(mData.get(position).studylang_level2);
            }
            //????????????3
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
                //???????????? ???????????? ?????? ?????????url??? ????????????.
                String imageUrl = "http://3.37.204.197/hellochat/" + mData.get(position).profile;
                //?????? ????????? ??????
                Glide.with(contentsViewHolder.profile)
                        .load(imageUrl)
                        .into(contentsViewHolder.profile);
            }
            if(mData.get(position).comment != null){
                contentsViewHolder.comment_recycler.setAdapter(new FeedCommentAdapter(mData.get(position).comment));
                contentsViewHolder.comment_recycler.setLayoutManager(new LinearLayoutManager(c));
            }else {
                contentsViewHolder.comment_recycler.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyPageViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout update_myinfo_bnt;
        TextView name, mylang, mylang2, mylang3, studylang, studylang2, studylang3, introduce ,feed_cnt ,like_cnt , follower_cnt , following_cnt;
        LinearLayout layout_mylang2, layout_mylang3, layout_studylang2, layout_studylang3 , follower , following;
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
            this.follower_cnt = itemView.findViewById(R.id.follower_cnt);
            this.following_cnt = itemView.findViewById(R.id.following_cnt);
            this.follower = itemView.findViewById(R.id.follower);
            this.following = itemView.findViewById(R.id.following);

            follower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(onFollowerClickListener!=null){
                            onFollowerClickListener.onFollowerClick(v , pos);
                        }
                    }
                }
            });
            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(onFollowingClickListener!=null){
                            onFollowingClickListener.onFollowingClick(v , pos);
                        }
                    }
                }
            });

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
        ImageView profile, more ,heart, player_control;
        protected ConstraintLayout player;
        protected SeekBar seekBar;
        protected RecyclerView image;
        RecyclerView comment_recycler;

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
            this.comment_recycler = itemView.findViewById(R.id.comment_recycler);

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
                    if (mPlayer != null) {    // ???????????? ??????
                        mPlayer.release();  // ????????? ??????
                        mPlayer = null;
                        Log.d(TAG, "onCreate: ???????????????");
                    }
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource("http://3.37.204.197/hellochat/"+mData.get(pos).record); // ?????? ?????? ?????? ??????
                    mPlayer.prepare();  // ?????? ??????
                    mPlayer.start();    // ??????
                } catch (IOException e) {
                    e.printStackTrace();
                }
                seekBar.setMax(mPlayer.getDuration());  // ????????? ??? ????????? ????????? ???????????? ??????
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser)  // ???????????? ???????????? ????????????
                            mPlayer.seekTo(progress);   // ??????????????? ????????????(????????? ???????????? ????????????)
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                mPlayer.start();
                new Thread(new Runnable() {  // ????????? ??????
                    @Override
                    public void run() {
                        while (mPlayer.isPlaying()) {  // ????????? ??????????????? ?????? ???????????? ???
                            try {
                                Thread.sleep(500); // 1????????? ????????? ???????????? ???
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // ?????? ???????????? ????????? ????????? ???????????? ??????
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
    private OnMyPageClickListener myPageClickListener = null;
    private OnFeedItemClickListener feedItemClickListener = null;
    private OnLikeClickListener likeClickListener = null;
    private OnMoreBntClickListener moreBntClickListener = null;
    private OnFollowerClickListener onFollowerClickListener = null;
    private OnFollowingClickListener onFollowingClickListener = null;
    private OnContentLongClickListener longClickListener;

    public interface OnMyPageClickListener { void onmyPageClick(View v, int pos);}
    public interface OnFeedItemClickListener { void onFeedItemClick(View v, int pos);}
    public interface OnLikeClickListener { void onLikeClick(View v, int pos);}
    public interface OnMoreBntClickListener { void onMoreBntClick(View v, int pos);}
    public interface OnFollowerClickListener { void onFollowerClick(View v, int pos);}
    public interface OnFollowingClickListener { void onFollowingClick(View v, int pos);}
    public interface OnContentLongClickListener { void onContentLongClick(View v, int position);}

    public void setOnMyPageClickListener(MyPageAdapter.OnMyPageClickListener listener) { this.myPageClickListener = listener; }
    public void setOnFeedItemClickListener(MyPageAdapter.OnFeedItemClickListener listener) { this.feedItemClickListener = listener; }
    public void setOnLikeClickListener(MyPageAdapter.OnLikeClickListener listener) { this.likeClickListener = listener; }
    public void setOnMoreBntClickListener(MyPageAdapter.OnMoreBntClickListener listener){ this.moreBntClickListener = listener; }
    public void setOnFollowerClickListener(MyPageAdapter.OnFollowerClickListener listener) { this.onFollowerClickListener = listener; }
    public void setOnFollowingClickListener(MyPageAdapter.OnFollowingClickListener listener){ this.onFollowingClickListener = listener; }
    public void setOnLongClickListener(OnContentLongClickListener listener) { this.longClickListener = listener; }


}
