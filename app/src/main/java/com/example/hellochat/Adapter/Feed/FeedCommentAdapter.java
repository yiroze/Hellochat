package com.example.hellochat.Adapter.Feed;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class FeedCommentAdapter extends RecyclerView.Adapter<FeedCommentAdapter.ViewHolder> {

    ArrayList<ReplyList> mList;
    Context c;
    public FeedCommentAdapter(ArrayList<ReplyList> Data) {
        mList = Data;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ViewHolder holder;
        View view;
        c = parent.getContext();
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_comment, parent, false);
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
        if (!mList.get(position).record.equals("") && mList.get(position).record != null) {
            holder.player.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, contents;
        ImageView profile ,player_control;
        protected ConstraintLayout player;
        protected SeekBar seekBar;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.date = itemView.findViewById(R.id.date);
            this.contents = itemView.findViewById(R.id.contents);
            this.profile = itemView.findViewById(R.id.profile);
            this.player = itemView.findViewById(R.id.player_layout);
            this.player_control = itemView.findViewById(R.id.player_control);
            this.seekBar = itemView.findViewById(R.id.SeekBar);
        }

    }

    public String getPref() {
        SharedPreferences pref = c.getSharedPreferences("LOGIN", MODE_PRIVATE);
        return pref.getString("Login_data", "");
    }

}
