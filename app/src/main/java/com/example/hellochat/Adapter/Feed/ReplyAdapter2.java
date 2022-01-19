package com.example.hellochat.Adapter.Feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.DTO.Feed.ReplyList;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ReplyAdapter2 extends RecyclerView.Adapter<ReplyAdapter2.ViewHolder> {

    ArrayList<ReplyList> mList;
    Context c;
    MediaPlayer mPlayer;

    public ReplyAdapter2(ArrayList<ReplyList> Data) {
        mList = Data;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ViewHolder holder;
        View view;
        c = parent.getContext();
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_test, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        String a = getPref();
        int user_idx = Integer.parseInt(a);
        holder.contents.setText(mList.get(position).contents);
        if (mList.get(position).profile != null && !mList.get(position).profile.equals("")) {
            String imageUrl = "http://3.37.204.197/hellochat/" + mList.get(position).profile;
            //영상 썸네일 세팅
            Glide.with(holder.profile)
                    .load(imageUrl)
                    .into(holder.profile);
        }
        holder.name.setText(mList.get(position).name);
        holder.date.setText(mList.get(position).date);
        if (!mList.get(position).record.equals("")) {
            holder.player.setVisibility(View.VISIBLE);
        }
        if (mList.get(position).user_idx != user_idx) {
            holder.more.setVisibility(View.GONE);
        } else {
            holder.more.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, contents;
        ImageView profile, more, player_control;
        protected ConstraintLayout player;
        protected SeekBar seekBar;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.date = itemView.findViewById(R.id.date);
            this.contents = itemView.findViewById(R.id.contents);
            this.more = itemView.findViewById(R.id.more);
            this.profile = itemView.findViewById(R.id.profile);
            this.player = itemView.findViewById(R.id.player_layout);
            this.player_control = itemView.findViewById(R.id.player_control);
            this.seekBar = itemView.findViewById(R.id.SeekBar);

            player_control.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                getPlayer(pos, seekBar);
            });

            more.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (moreClickListener != null) {
                        moreClickListener.onReplyMoreClickListener(v, pos);
                    }
                }
            });

        }

    }

    public String getPref() {
        SharedPreferences pref = c.getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }


    public void getPlayer(int pos, SeekBar seekBar) {
        try {
            if (mPlayer != null) {    // 사용하기 전에
                mPlayer.release();  // 리소스 해제
                mPlayer = null;
            }
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource("http://3.37.204.197/hellochat/" + mList.get(pos).record); // 음악 파일 위치 지정
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

    public interface OnReplyMoreClick {
        void onReplyMoreClickListener(View v, int pos);
    }

    private OnReplyMoreClick moreClickListener;

    public void setOnReplyClick(OnReplyMoreClick listener) {
        this.moreClickListener = listener;
    }

}
