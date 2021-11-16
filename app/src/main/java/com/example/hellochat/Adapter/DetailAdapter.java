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
import com.example.hellochat.DTO.DetailData;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class DetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_CONTENTS = 0;
    private final int TYPE_COMMENT = 1;
    private final int TYPE_REPLY = 2;
    private List<DetailData> datalist;
    private Context c;
    private static final String TAG = "DetailAdapter";
    private OnLikeClick mListener = null;
    private OnMoreBntClick_Comment comment_bnt = null;
    private OnMoreBntClick_Reply reply_bnt = null;
    private OnCommentClickListener commentClickListener = null;
    private OnMoreBntClick_Contents contents_bnt = null;
    private OpenUserDetail openUserDetail;
    private NewsfeedAdapter.OpenMyDetail openMyDetail;
    private OnContentLongClickListener longClickListener;

    MediaPlayer mPlayer;

    @Override
    public int getItemViewType(int position) {
        return datalist.get(position).getView_type();
    }

    public DetailAdapter(List<DetailData> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view;
        c = parent.getContext();
        if (viewType == TYPE_CONTENTS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contents, parent, false);
            holder = new HeaderViewHolder(view);
        } else if (viewType == TYPE_COMMENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);
            holder = new CommentViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply, parent, false);
            holder = new ReplyViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        String a = getPref();
        int user_idx = Integer.parseInt(a);


        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            //이미지를 넣어주기 위해 이미지url을 가져온다.
            if (datalist.get(position).profile != null && !datalist.get(position).profile.equals("")) {
                String imageUrl = "http://3.37.204.197/hellochat/" + datalist.get(position).profile;
                //영상 썸네일 세팅
                Glide.with(headerViewHolder.profile)
                        .load(imageUrl)
                        .into(headerViewHolder.profile);
            }
            //수정삭제 버튼
            if (datalist.get(position).user_idx != user_idx) {
                headerViewHolder.more.setVisibility(View.GONE);
            } else {
                headerViewHolder.more.setVisibility(View.VISIBLE);
            }
            //유저의 제 2,3의 모국어나 학습언어가 없을시 보이지않게 처리
            //모국어2
            if (datalist.get(position).mylang2 == null) {
                headerViewHolder.layout_mylang2.setVisibility(View.GONE);
            } else {
                headerViewHolder.mylang2.setText(datalist.get(position).mylang2);
            }
            //모국어3
            if (datalist.get(position).mylang3 == null) {
                headerViewHolder.layout_mylang3.setVisibility(View.GONE);
            } else {
                headerViewHolder.mylang3.setText(datalist.get(position).mylang3);
            }
            //학습언어2
            if (datalist.get(position).studylang2 == null) {
                headerViewHolder.layout_studylang2.setVisibility(View.GONE);
            } else {
                headerViewHolder.studylang2.setText(datalist.get(position).studylang2);
                headerViewHolder.progress_studylang2.setProgress(datalist.get(position).studylang_level2);
            }
            //학습언어3
            if (datalist.get(position).studylang3 == null) {
                headerViewHolder.layout_studylang3.setVisibility(View.GONE);
            } else {
                headerViewHolder.studylang3.setText(datalist.get(position).studylang3);
                headerViewHolder.progress_studylang3.setProgress(datalist.get(position).studylang_level3);
            }
            headerViewHolder.name.setText(datalist.get(position).name);
            headerViewHolder.mylang.setText(datalist.get(position).mylang);
            headerViewHolder.studylang.setText(datalist.get(position).studylang);
            headerViewHolder.progress_studylang.setProgress(datalist.get(position).studylang_level);
            headerViewHolder.contents.setText(datalist.get(position).contents);
            headerViewHolder.date.setText(datalist.get(position).date);
            if (datalist.get(position).islike == 0) {
                headerViewHolder.heart.setImageResource(R.drawable.heart_off);
            } else {
                headerViewHolder.heart.setImageResource(R.drawable.heart_on);
            }
            if (datalist.get(position).like_cnt != 0) {
                headerViewHolder.like_cnt.setText(Integer.toString(datalist.get(position).like_cnt));
            }
            if (datalist.get(position).comment_cnt != 0) {
                headerViewHolder.comment_cnt.setText(Integer.toString(datalist.get(position).comment_cnt));
            }
            String datalistForSplit = datalist.get(position).image.replace("[", "").replace("]", "").replace(" ", "");
            String[] item = datalistForSplit.split(",");
            ArrayList<String> image_item = new ArrayList<>(Arrays.asList(item));
            if (datalist.get(position).image.equals("") || datalist.get(position).image.equals("[]")) {
                headerViewHolder.image.setVisibility(View.GONE);
            } else {
                if(image_item.size() ==1){
                    headerViewHolder.image.setAdapter(new BigImageAdapter(image_item, c));
                    headerViewHolder.image.setLayoutManager(new GridLayoutManager(c, 1));
                    headerViewHolder.image.setHasFixedSize(false);
                }else {
                    headerViewHolder.image.setAdapter(new ImageAdapter(image_item, c));
                    headerViewHolder.image.setLayoutManager(new GridLayoutManager(c, 3));
                    headerViewHolder.image.setHasFixedSize(true);
                }
            }
            if (datalist.get(position).record.equals("") || datalist.get(position).record == null) {
                headerViewHolder.player.setVisibility(View.GONE);
            }
        } else if (holder instanceof CommentViewHolder) {
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            //이미지를 넣어주기 위해 이미지url을 가져온다.
            if (datalist.get(position).profile != null && !datalist.get(position).profile.equals("")) {
                String imageUrl = "http://3.37.204.197/hellochat/" + datalist.get(position).profile;
                //영상 썸네일 세팅
                Glide.with(commentViewHolder.profile)
                        .load(imageUrl)
                        .into(commentViewHolder.profile);
            }
            //수정삭제 버튼
            if (!datalist.get(position).contents.equals("삭제된 댓글입니다.")) {
                if (datalist.get(position).user_idx != user_idx) {
                    commentViewHolder.more.setVisibility(View.GONE);
                } else {
                    commentViewHolder.more.setVisibility(View.VISIBLE);
                }
            } else {
                commentViewHolder.more.setVisibility(View.GONE);
            }
            commentViewHolder.name.setText(datalist.get(position).name);
            commentViewHolder.contents.setText(datalist.get(position).contents);
            commentViewHolder.date.setText(datalist.get(position).date);
            if (!datalist.get(position).record.equals("") && datalist.get(position).record != null) {
                commentViewHolder.player.setVisibility(View.VISIBLE);
            }
        } else {
            ReplyViewHolder replyViewHolder = (ReplyViewHolder) holder;
            //이미지를 넣어주기 위해 이미지url을 가져온다.
            if (datalist.get(position).profile != null && !datalist.get(position).profile.equals("")) {
                String imageUrl = "http://3.37.204.197/hellochat/" + datalist.get(position).profile;
                //영상 썸네일 세팅
                Glide.with(replyViewHolder.profile)
                        .load(imageUrl)
                        .into(replyViewHolder.profile);
            }
            //수정삭제 버튼
            if (datalist.get(position).user_idx != user_idx) {
                replyViewHolder.more.setVisibility(View.GONE);
            } else {
                replyViewHolder.more.setVisibility(View.VISIBLE);
            }
            replyViewHolder.name.setText(datalist.get(position).name);
            replyViewHolder.contents.setText(datalist.get(position).contents);
            replyViewHolder.date.setText(datalist.get(position).date);
            if (!datalist.get(position).record.equals("") && datalist.get(position).record != null) {
                replyViewHolder.player.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, contents;
        ImageView profile, more ,player_control;
        protected ConstraintLayout player;
        protected SeekBar seekBar;;

        public CommentViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            contents = itemView.findViewById(R.id.contents);
            more = itemView.findViewById(R.id.more);
            profile = itemView.findViewById(R.id.profile);
            this.player = itemView.findViewById(R.id.player_layout);
            this.player_control = itemView.findViewById(R.id.player_control);
            this.seekBar = itemView.findViewById(R.id.SeekBar);


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

            player_control.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                getPlayer(pos , seekBar);
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (comment_bnt != null) {
                            comment_bnt.onMoreBntClick_Comment(v, pos);
                        }
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (commentClickListener != null) {
                            commentClickListener.onCommentClickListener(v, pos);
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

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, contents;
        ImageView profile, more, player_control;
        protected ConstraintLayout player;
        protected SeekBar seekBar;

        public ReplyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            contents = itemView.findViewById(R.id.contents);
            more = itemView.findViewById(R.id.more);
            profile = itemView.findViewById(R.id.profile);
            this.player = itemView.findViewById(R.id.player_layout);
            this.player_control = itemView.findViewById(R.id.player_control);
            this.seekBar = itemView.findViewById(R.id.SeekBar);

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

            player_control.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                getPlayer(pos , seekBar);
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (reply_bnt != null) {
                            reply_bnt.onMoreBntClick_Reply(v, pos);
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

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView name, mylang, mylang2, mylang3, studylang, studylang2, studylang3, date, contents, like_cnt, comment_cnt;
        LinearLayout layout_mylang2, layout_mylang3, layout_studylang2, layout_studylang3;
        ProgressBar progress_mylang, progress_mylang2, progress_mylang3, progress_studylang, progress_studylang2, progress_studylang3;
        ImageView profile, more, heart ,player_control;
        protected ConstraintLayout player;
        protected SeekBar seekBar;
        protected RecyclerView image;

        public HeaderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            mylang = itemView.findViewById(R.id.mylang);
            mylang2 = itemView.findViewById(R.id.mylang2);
            mylang3 = itemView.findViewById(R.id.mylang3);
            studylang = itemView.findViewById(R.id.study_lang);
            studylang2 = itemView.findViewById(R.id.study_lang2);
            studylang3 = itemView.findViewById(R.id.study_lang3);
            date = itemView.findViewById(R.id.date);
            contents = itemView.findViewById(R.id.contents);
            layout_mylang2 = itemView.findViewById(R.id.mylang2_layout);
            layout_mylang3 = itemView.findViewById(R.id.mylang3_layout);
            layout_studylang2 = itemView.findViewById(R.id.study_lang2_layout);
            layout_studylang3 = itemView.findViewById(R.id.study_lang3_layout);
            progress_mylang = itemView.findViewById(R.id.mylang_level);
            progress_mylang2 = itemView.findViewById(R.id.mylang_level2);
            progress_mylang3 = itemView.findViewById(R.id.mylang_level3);
            progress_studylang = itemView.findViewById(R.id.studylang_level);
            progress_studylang2 = itemView.findViewById(R.id.studylang_level2);
            progress_studylang3 = itemView.findViewById(R.id.studylang_level3);
            more = itemView.findViewById(R.id.more);
            profile = itemView.findViewById(R.id.profile);
            like_cnt = itemView.findViewById(R.id.heart_count);
            heart = itemView.findViewById(R.id.heart);
            comment_cnt = itemView.findViewById(R.id.comment_count);
            this.image = itemView.findViewById(R.id.image_recyclerview);
            this.player = itemView.findViewById(R.id.player_layout);
            this.player_control = itemView.findViewById(R.id.player_control);
            this.seekBar = itemView.findViewById(R.id.SeekBar);


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

            player_control.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                getPlayer(pos , seekBar);
            });
            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onLikeClick(v, pos);
                        }
                    }
                }
            });
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if(contents_bnt != null){
                            contents_bnt.onMoreBntClick_Contents(v , pos);
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

    public interface OpenMyDetail {
        void openMyDetail(View v, int position);
    }
    public interface OnLikeClick {
        void onLikeClick(View v, int pos);
    }

    public interface OnMoreBntClick_Comment {
        void onMoreBntClick_Comment(View v, int pos);
    }

    public interface OnMoreBntClick_Reply {
        void onMoreBntClick_Reply(View v, int pos);
    }

    public interface OnCommentClickListener {
        void onCommentClickListener(View v, int pos);
    }

    public interface OnMoreBntClick_Contents {
        void onMoreBntClick_Contents(View v, int pos);
    }

    public interface OpenUserDetail {
        void openUserDetail(View v, int position);
    }
    public interface OnContentLongClickListener {void onContentLongClick(View v, int position);}


    public void setOnClickListener(OnLikeClick listener) {
        this.mListener = listener;
    }

    public void setOnMoreBntClick_Comment(OnMoreBntClick_Comment listener) {
        this.comment_bnt = listener;
    }

    public void setOpenMyDetail(NewsfeedAdapter.OpenMyDetail listener) {
        this.openMyDetail = listener;
    }

    public void setOnMoreBntClick_Reply(OnMoreBntClick_Reply listener) {
        this.reply_bnt = listener;
    }

    public void setOnMoreBntClick_Contents(OnMoreBntClick_Contents listener) {
        this.contents_bnt = listener;
    }

    public void setOnCommentClickListener(OnCommentClickListener listener) {
        this.commentClickListener = listener;
    }

    public void setOpenUserDetail(OpenUserDetail listener) {
        this.openUserDetail = listener;
    }

    public void setOnLongClickListener(OnContentLongClickListener listener) { this.longClickListener = listener; }


    public void getPlayer(int pos , SeekBar seekBar){
        try {
            if (mPlayer != null) {    // 사용하기 전에
                mPlayer.release();  // 리소스 해제
                mPlayer = null;
                Log.d(TAG, "onCreate: 리소스해제");
            }
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource("http://3.37.204.197/hellochat/"+datalist.get(pos).record); // 음악 파일 위치 지정
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

    }

}
