package com.example.hellochat.Adapter.Feed;

import android.content.Context;
import android.content.Intent;
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
import com.example.hellochat.Activity.Feed.Activity_Detail;
import com.example.hellochat.Adapter.Image.BigImageAdapter;
import com.example.hellochat.Adapter.Image.ImageAdapter;
import com.example.hellochat.R;
import com.example.hellochat.DTO.Feed.ViewData;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.NewsfeedViewHolder> {
    private Context c;
    private ArrayList<ViewData> datalist = new ArrayList<>();
    int idx;
    private static final String TAG = "NewsfeedAdapter";
    MediaPlayer mPlayer;


    private OnContentLongClickListener longClickListener;
    private OnItemClickListener mListener;
    private OnMoreBntClickListener moreBntClickListener;
    private OpenUserDetail openUserDetail;
    private OpenMyDetail openMyDetail;

    public interface OnContentLongClickListener {
        void onContentLongClick(View v, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface OpenUserDetail {
        void openUserDetail(View v, int position);
    }

    public interface OpenMyDetail {
        void openMyDetail(View v, int position);
    }

    public interface OnMoreBntClickListener {
        void onMoreBntClick(View v, int position);
    }

    public void setOnMoreClickListener(OnMoreBntClickListener listener) {
        this.moreBntClickListener = listener;
    }

    public void setOnLongClickListener(OnContentLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOpenUserDetail(OpenUserDetail listener) {
        this.openUserDetail = listener;
    }

    public void setOpenMyDetail(OpenMyDetail listener) {
        this.openMyDetail = listener;
    }



    public NewsfeedAdapter(ArrayList<ViewData> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public NewsfeedAdapter.NewsfeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        c = parent.getContext();
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeeditem, parent, false);
        NewsfeedViewHolder viewHolder = new NewsfeedViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsfeedViewHolder holder, int position) {

        // ?????? ???????????? ???????????? ??????????????? ???????????????
        idx = Integer.parseInt(getPref());
        if (datalist.get(position).user_idx != idx) {
            holder.more.setVisibility(View.INVISIBLE);
        } else {
            holder.more.setVisibility(View.VISIBLE);
        }
        //????????? ??? 2,3??? ???????????? ??????????????? ????????? ??????????????? ??????
        //?????????2
        if (datalist.get(position).mylang2 == null) {
            holder.mylang_layout2.setVisibility(View.GONE);
        } else {
            holder.mylang2.setText(datalist.get(position).mylang2);
        }
        //?????????3
        if (datalist.get(position).mylang3 == null) {
            holder.mylang_layout3.setVisibility(View.GONE);
        } else {
            holder.mylang3.setText(datalist.get(position).mylang3);
        }
        //????????????2
        if (datalist.get(position).studylang2 == null) {
            holder.study_lang_layout2.setVisibility(View.GONE);
        } else {
            holder.study_lang2.setText(datalist.get(position).studylang2);
            holder.study_lang_level2.setProgress(datalist.get(position).studylang_level2);
        }
        //????????????3
        if (datalist.get(position).studylang3 == null) {
            holder.study_lang_layout3.setVisibility(View.GONE);
        } else {
            holder.study_lang3.setText(datalist.get(position).studylang3);
            holder.study_lang_level3.setProgress(datalist.get(position).studylang_level3);
        }
        if (!datalist.get(position).comment_cnt.equals("0")) {
            holder.comment_count.setText(datalist.get(position).comment_cnt);
        }
        if (!datalist.get(position).like_cnt.equals("0")) {
            holder.heart_count.setText(datalist.get(position).like_cnt);
        }
        holder.name.setText(datalist.get(position).name);
        holder.contents.setText(datalist.get(position).contents);
        holder.date.setText(datalist.get(position).date);
        holder.mylang.setText(datalist.get(position).mylang);
        holder.study_lang.setText(datalist.get(position).studylang);
        holder.study_lang_level.setProgress(datalist.get(position).studylang_level);
        if (datalist.get(position).islike == 0) {
            holder.heart.setImageResource(R.drawable.heart_off);
        } else {
            holder.heart.setImageResource(R.drawable.heart_on);
        }
        if (datalist.get(position).profile != null && !datalist.get(position).profile.equals("")) {
            //???????????? ???????????? ?????? ?????????url??? ????????????.
            String imageUrl = "http://3.37.204.197/hellochat/" + datalist.get(position).profile;
            //?????? ????????? ??????
            Glide.with(holder.profile)
                    .load(imageUrl)
                    .into(holder.profile);
        }
        String a = datalist.get(position).image.replace("[", "").replace("]", "").replace(" ", "");
        String[] item = a.split(",");
        ArrayList<String> image_item = new ArrayList<>(Arrays.asList(item));
        if (datalist.get(position).image.equals("") || datalist.get(position).image.equals("[]")) {
            holder.image.setVisibility(View.GONE);
        } else {
            if (image_item.size() == 1) {
                holder.image.setAdapter(new BigImageAdapter(image_item, c));
                holder.image.setLayoutManager(new GridLayoutManager(c, 1));
                holder.image.setHasFixedSize(false);
            } else {
                holder.image.setAdapter(new ImageAdapter(image_item, c));
                holder.image.setLayoutManager(new GridLayoutManager(c, 3));
                holder.image.setHasFixedSize(true);
            }
        }
        if (datalist.get(position).record.equals("") || datalist.get(position).record == null) {
            holder.player.setVisibility(View.GONE);
        }
        if(datalist.get(position).comment != null){
            holder.comment.setAdapter(new FeedCommentAdapter(datalist.get(position).comment));
            holder.comment.setLayoutManager(new LinearLayoutManager(c));
        }else {
            holder.comment.setVisibility(View.GONE);
        }
        if(datalist.get(position).contents.equals("")){
            holder.contents.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return (null != datalist ? datalist.size() : 0);
    }


    public class NewsfeedViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout mylang_layout2, mylang_layout3, study_lang_layout2, study_lang_layout3;
        protected TextView name, mylang, mylang2, mylang3, study_lang, study_lang2, study_lang3, heart_count, comment_count, contents, date;
        protected ProgressBar mylang_level, study_lang_level, study_lang_level2, study_lang_level3;
        protected ImageView heart, profile, more, player_control;
        protected RecyclerView image, comment;
        protected ConstraintLayout player;
        protected SeekBar seekBar;
        boolean isplaying;
        int pauseposition;

        public NewsfeedViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.mylang = itemView.findViewById(R.id.mylang);
            this.mylang2 = itemView.findViewById(R.id.mylang2);
            this.mylang3 = itemView.findViewById(R.id.mylang3);
            this.mylang_level = itemView.findViewById(R.id.mylang_level);
            this.study_lang = itemView.findViewById(R.id.study_lang);
            this.study_lang_level = itemView.findViewById(R.id.studylang_level);
            this.study_lang2 = itemView.findViewById(R.id.study_lang2);
            this.study_lang_level2 = itemView.findViewById(R.id.studylang_level2);
            this.study_lang3 = itemView.findViewById(R.id.study_lang3);
            this.study_lang_level3 = itemView.findViewById(R.id.studylang_level3);
            this.profile = itemView.findViewById(R.id.profile);
            this.heart = itemView.findViewById(R.id.heart);
            this.heart_count = itemView.findViewById(R.id.heart_count);
            this.comment_count = itemView.findViewById(R.id.comment_count);
            this.contents = itemView.findViewById(R.id.contents);
            this.date = itemView.findViewById(R.id.date);
            this.more = itemView.findViewById(R.id.more);
            this.mylang_layout2 = itemView.findViewById(R.id.mylang2_layout);
            this.mylang_layout3 = itemView.findViewById(R.id.mylang3_layout);
            this.study_lang_layout2 = itemView.findViewById(R.id.study_lang2_layout);
            this.study_lang_layout3 = itemView.findViewById(R.id.study_lang3_layout);
            this.image = itemView.findViewById(R.id.image_recyclerview);
            this.player = itemView.findViewById(R.id.player_layout);
            this.player_control = itemView.findViewById(R.id.player_control);
            this.seekBar = itemView.findViewById(R.id.SeekBar);
            this.comment = itemView.findViewById(R.id.comment_recycler);
            
            
            contents.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "onLongClick: onLongClick: onLongClick: onLongClick: ");
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

            player_control.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                try {
                    if (mPlayer != null) {    // ???????????? ??????
                        mPlayer.release();  // ????????? ??????
                        mPlayer = null;
                        Log.d(TAG, "onCreate: ???????????????");
                    }
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource("http://3.37.204.197/hellochat/" + datalist.get(pos).record); // ?????? ?????? ?????? ??????
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

            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d(TAG, "onClick: " + mListener);
                    if (mListener != null) {
                        mListener.onItemClick(v, pos);
                    } else {
                        Log.d(TAG, "onClick: null");
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d(TAG, "onClick: ???????????????");
                    Intent intent = new Intent(c, Activity_Detail.class);
                    intent.putExtra("feed_idx", datalist.get(pos).feed_idx);
                    v.getContext().startActivity(intent);
                }
            });
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    //alert
                    if (pos != RecyclerView.NO_POSITION) {
                        if (moreBntClickListener != null) {
                            moreBntClickListener.onMoreBntClick(v, pos);
                        }
                    }
                }
            });
            profile.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (datalist.get(pos).user_idx == Integer.parseInt(getPref())) {
                    if(pos != RecyclerView.NO_POSITION){
                        if(openMyDetail != null){
                            openMyDetail.openMyDetail(v,pos);
                        }
                    }
                } else {
                    //alert
                    if (pos != RecyclerView.NO_POSITION) {
                        if (openUserDetail != null) {
                            openUserDetail.openUserDetail(v, pos);
                        }
                    }
                }
            });
            name.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (datalist.get(pos).user_idx == Integer.parseInt(getPref())) {
                    if(pos != RecyclerView.NO_POSITION){
                        if(openMyDetail != null){
                            openMyDetail.openMyDetail(v,pos);
                        }
                    }
                } else {
                    //alert
                    if (pos != RecyclerView.NO_POSITION) {
                        if (openUserDetail != null) {
                            openUserDetail.openUserDetail(v, pos);
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


    public void clear() {
        datalist.clear();
    }


}



