package com.example.hellochat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hellochat.DTO.Chatting;
import com.example.hellochat.R;
import com.example.hellochat.Util.Util;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class ChattingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ChattingAdapter";
    private ArrayList<Chatting> mList;
    private final int CONTENT_TYPE_TEXT = 1;
    private final int CONTENT_TYPE_IMAGE = 2;
    private final int CONTENT_TYPE_CALL = 4;
    private final int CONTENT_TYPE_CALL_REFUSE = 5;
    private final int CONTENT_TYPE_CALL_CANCEL = 6;
    private final int CONTENT_TYPE_CALL_DISCONNECT = 8;
    Util util = new Util();

    private Context c;
    int my_idx;

    public ChattingAdapter(ArrayList<Chatting> list, int my_idx) {
        this.mList = list;
        this.my_idx = my_idx;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view;
        c = parent.getContext();

        if (mList.get(viewType).send_idx == my_idx) {
            if (mList.get(viewType).content_type == CONTENT_TYPE_TEXT) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem_my, parent, false);
                holder = new ViewHolder(view);
            } else if (mList.get(viewType).content_type == CONTENT_TYPE_IMAGE) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_my_image, parent, false);
                holder = new ImageViewHolder(view);
            } else if (mList.get(viewType).content_type == CONTENT_TYPE_CALL || mList.get(viewType).content_type == CONTENT_TYPE_CALL_REFUSE || mList.get(viewType).content_type == CONTENT_TYPE_CALL_CANCEL || mList.get(viewType).content_type == CONTENT_TYPE_CALL_DISCONNECT) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_call_item_my, parent, false);
                holder = new CallViewHolder(view);
            }
        } else {
            if (mList.get(viewType).content_type == CONTENT_TYPE_TEXT) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatiem, parent, false);
                holder = new LeftViewHolder(view);
            } else if (mList.get(viewType).content_type == CONTENT_TYPE_IMAGE) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_image, parent, false);
                holder = new LeftImageViewHolder(view);
            } else if (mList.get(viewType).content_type == CONTENT_TYPE_CALL || mList.get(viewType).content_type == CONTENT_TYPE_CALL_REFUSE || mList.get(viewType).content_type == CONTENT_TYPE_CALL_CANCEL || mList.get(viewType).content_type == CONTENT_TYPE_CALL_DISCONNECT) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_call_item, parent, false);
                holder = new LeftCallViewHolder(view);
            }
        }
//        if (viewType == RIGHT_CHAT) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatitem_my, parent, false);
//            holder = new ViewHolder(view);
//        } else if (viewType == RIGHT_IMAGE) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_my_image, parent, false);
//            holder = new ImageViewHolder(view);
//        } else if (viewType == LEFT_CHAT) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatiem, parent, false);
//            holder = new LeftViewHolder(view);
//        } else if (viewType == LEFT_IMAGE) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_image, parent, false);
//            holder = new LeftImageViewHolder(view);
//        }
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        Date before_date = new Date();
        Date after_date = new Date();
        date.setTime(Long.parseLong(mList.get(position).date));
        Log.d(TAG, "onBindViewHolder: " + mList.get(position).checked);

        if (position != 0) {
            before_date.setTime(Long.parseLong(mList.get(position - 1).date));
        }
        if (position + 1 != mList.size()) {
            after_date.setTime(Long.parseLong(mList.get(position + 1).date));
        }
        Log.d(TAG, "onBindViewHolder: " + simpleDateFormat.format(date) + simpleDateFormat.format(before_date));
        //프로필사진과 이름
        //1. 바로 위의 메시지와 받는사람이 다르다.
        //2. 바로 위의 메시지와 시간이 다르다.
        //시간이 표시되는 경우
        //1. 바로 밑의 데이터가 없다.
        //or
        //1. 바로 밑의 데이터가 받는사람이 다르다.
        //2. 바로 밑의 데이터의 시간이 다르다.
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.content.setText(mList.get(position).content);
            date.setTime(Long.parseLong(mList.get(position).date));
            viewHolder.date.setText(simpleDateFormat.format(date));
            if (mList.get(position).checked == 1) {
                viewHolder.checked.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checked.setVisibility(View.GONE);
            }
//            if (position + 1 >= mList.size()) {
//                viewHolder.date.setVisibility(View.VISIBLE);
//            } else {
//                after_date.setTime(Long.parseLong(mList.get(position + 1).date));
//                if (mList.get(position).accept_idx != mList.get(position + 1).accept_idx || !simpleDateFormat.format(date).equals(simpleDateFormat.format(after_date))) {
//                    viewHolder.date.setVisibility(View.VISIBLE);
//                }
//            }
        } else if (holder instanceof LeftViewHolder) {
            LeftViewHolder viewHolder = (LeftViewHolder) holder;

//            if (position > 0) {
//                if (mList.get(position - 1).accept_idx == mList.get(position).accept_idx && simpleDateFormat.format(date).equals(simpleDateFormat.format(before_date))) {
//                    viewHolder.name.setVisibility(View.GONE);
//                    viewHolder.profile.setVisibility(View.GONE);
//                }
//            }
//            if (position + 1 >= mList.size()) {
//                viewHolder.date.setVisibility(View.VISIBLE);
//            } else {
//                after_date.setTime(Long.parseLong(mList.get(position + 1).date));
//                if (mList.get(position).accept_idx != mList.get(position + 1).accept_idx || !simpleDateFormat.format(date).equals(simpleDateFormat.format(after_date))) {
//                    viewHolder.date.setVisibility(View.VISIBLE);
//                }
//            }
            if (position > 0) {
                if (mList.get(position - 1).accept_idx == mList.get(position).accept_idx) {
                    viewHolder.name.setVisibility(View.GONE);
                    viewHolder.profile.setVisibility(View.GONE);
                }
            }
            viewHolder.content.setText(mList.get(position).content);
            date.setTime(Long.parseLong(mList.get(position).date));
            viewHolder.date.setText(simpleDateFormat.format(date));
            viewHolder.name.setText(mList.get(position).name);
            if (mList.get(position).profile != null && !mList.get(position).profile.equals("")) {
                String imageUrl = "http://3.37.204.197/hellochat/" + mList.get(position).profile;
                //영상 썸네일 세팅
                Glide.with(viewHolder.profile)
                        .load(imageUrl)
                        .into(viewHolder.profile);
            }

        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            date.setTime(Long.parseLong(mList.get(position).date));
            viewHolder.date.setText(simpleDateFormat.format(date));

            date.setTime(Long.parseLong(mList.get(position).date));
            viewHolder.date.setText(simpleDateFormat.format(date));
            if (mList.get(position).checked == 1) {
                viewHolder.checked.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checked.setVisibility(View.GONE);
            }
//            if (position + 1 >= mList.size()) {
//                viewHolder.date.setVisibility(View.VISIBLE);
//            } else {
//                after_date.setTime(Long.parseLong(mList.get(position + 1).date));
//                if (mList.get(position).accept_idx != mList.get(position + 1).accept_idx || !simpleDateFormat.format(date).equals(simpleDateFormat.format(after_date))) {
//                    viewHolder.date.setVisibility(View.VISIBLE);
//                }
//            }

            String a = mList.get(position).content.replace("[", "").replace("]", "").replace(" ", "");
            String[] item = a.split(",");
            ArrayList<String> image_item = new ArrayList<>(Arrays.asList(item));
            if (image_item.size() == 1) {
                viewHolder.image.setAdapter(new ChatImageAdapter(image_item, c));
                viewHolder.image.setLayoutManager(new GridLayoutManager(c, 1));
                viewHolder.image.setHasFixedSize(false);
            } else if (image_item.size() == 2 || image_item.size() == 4) {
                viewHolder.image.setAdapter(new ChatImageAdapter(image_item, c));
                viewHolder.image.setLayoutManager(new GridLayoutManager(c, 2));
                viewHolder.image.setHasFixedSize(true);
            } else {
                viewHolder.image.setAdapter(new ChatImageAdapter(image_item, c));
                viewHolder.image.setLayoutManager(new GridLayoutManager(c, 3));
                viewHolder.image.setHasFixedSize(true);
            }
        } else if (holder instanceof LeftImageViewHolder) {
            LeftImageViewHolder viewHolder = (LeftImageViewHolder) holder;
            date.setTime(Long.parseLong(mList.get(position).date));
            viewHolder.date.setText(simpleDateFormat.format(date));
            viewHolder.name.setText(mList.get(position).name);
            if (position > 0) {
                if (mList.get(position - 1).accept_idx == mList.get(position).accept_idx) {
                    viewHolder.name.setVisibility(View.GONE);
                    viewHolder.profile.setVisibility(View.GONE);
                }
            }
//            if (position + 1 >= mList.size()) {
//                viewHolder.date.setVisibility(View.VISIBLE);
//            } else {
//                after_date.setTime(Long.parseLong(mList.get(position + 1).date));
//                if (mList.get(position).accept_idx != mList.get(position + 1).accept_idx || !simpleDateFormat.format(date).equals(simpleDateFormat.format(after_date))) {
//                    viewHolder.date.setVisibility(View.VISIBLE);
//                }
//            }
            if (mList.get(position).profile != null && !mList.get(position).profile.equals("")) {
                String imageUrl = "http://3.37.204.197/hellochat/" + mList.get(position).profile;
                //영상 썸네일 세팅
                Glide.with(viewHolder.profile)
                        .load(imageUrl)
                        .into(viewHolder.profile);
            }
            String a = mList.get(position).content.replace("[", "").replace("]", "").replace(" ", "");
            String[] item = a.split(",");
            ArrayList<String> image_item = new ArrayList<>(Arrays.asList(item));
            if (image_item.size() == 1) {
                viewHolder.image.setAdapter(new ChatImageAdapter(image_item, c));
                viewHolder.image.setLayoutManager(new GridLayoutManager(c, 1));
                viewHolder.image.setHasFixedSize(false);
            } else if (image_item.size() == 2 || image_item.size() == 4) {
                viewHolder.image.setAdapter(new ChatImageAdapter(image_item, c));
                viewHolder.image.setLayoutManager(new GridLayoutManager(c, 2));
                viewHolder.image.setHasFixedSize(true);
            } else {
                viewHolder.image.setAdapter(new ChatImageAdapter(image_item, c));
                viewHolder.image.setLayoutManager(new GridLayoutManager(c, 3));
                viewHolder.image.setHasFixedSize(true);
            }
        } else if (holder instanceof CallViewHolder) {
            CallViewHolder viewHolder = (CallViewHolder) holder;
            viewHolder.content.setText(mList.get(position).content);
            date.setTime(Long.parseLong(mList.get(position).date));
            viewHolder.date.setText(simpleDateFormat.format(date));
            if (mList.get(position).checked == 1) {
                viewHolder.checked.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checked.setVisibility(View.GONE);
            }

            if (mList.get(position).content_type == CONTENT_TYPE_CALL) {
                viewHolder.video_img.setImageResource(R.drawable.videocall);
                viewHolder.content.setText("영상통화");
            } else if (mList.get(position).content_type == CONTENT_TYPE_CALL_REFUSE) {
                viewHolder.video_img.setImageResource(R.drawable.videocall_cancel);
                viewHolder.content.setText("응답거부");
            } else if (mList.get(position).content_type == CONTENT_TYPE_CALL_CANCEL) {
                viewHolder.video_img.setImageResource(R.drawable.videocall_cancel);
                viewHolder.content.setText("취소");
            } else if (mList.get(position).content_type == CONTENT_TYPE_CALL_DISCONNECT) {
                if (mList.get(position).content.equals("0")) {
                    viewHolder.content.setText("취소");
                } else {
                    viewHolder.content.setText(util.secToText(Integer.parseInt(mList.get(position).content)));
                }
                viewHolder.video_img.setImageResource(R.drawable.videocall_cancel);
            }

        } else if (holder instanceof LeftCallViewHolder) {
            LeftCallViewHolder viewHolder = (LeftCallViewHolder) holder;
            if (position > 0) {
                if (mList.get(position - 1).accept_idx == mList.get(position).accept_idx) {
                    viewHolder.name.setVisibility(View.GONE);
                    viewHolder.profile.setVisibility(View.GONE);
                }
            }
            viewHolder.content.setText(mList.get(position).content);
            date.setTime(Long.parseLong(mList.get(position).date));
            viewHolder.date.setText(simpleDateFormat.format(date));
            viewHolder.name.setText(mList.get(position).name);
            if (mList.get(position).profile != null && !mList.get(position).profile.equals("")) {
                String imageUrl = "http://3.37.204.197/hellochat/" + mList.get(position).profile;
                //영상 썸네일 세팅
                Glide.with(viewHolder.profile)
                        .load(imageUrl)
                        .into(viewHolder.profile);
            }
            if (mList.get(position).content_type == CONTENT_TYPE_CALL) {
                viewHolder.video_img.setImageResource(R.drawable.videocall);
                viewHolder.content.setText("영상통화");
            } else if (mList.get(position).content_type == CONTENT_TYPE_CALL_REFUSE) {
                viewHolder.video_img.setImageResource(R.drawable.videocall_cancel);
                viewHolder.content.setText("응답거부");
            } else if (mList.get(position).content_type == CONTENT_TYPE_CALL_CANCEL) {
                viewHolder.video_img.setImageResource(R.drawable.videocall_cancel);
                viewHolder.content.setText("취소");
            } else if (mList.get(position).content_type == CONTENT_TYPE_CALL_DISCONNECT) {
                if (mList.get(position).content.equals("0")) {
                    viewHolder.content.setText("취소");
                } else {
                    viewHolder.content.setText(util.secToText(Integer.parseInt(mList.get(position).content)));
                }
                viewHolder.video_img.setImageResource(R.drawable.videocall_cancel);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView content, date, checked;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);
            checked = itemView.findViewById(R.id.checked);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView name, content, date;
        ImageView profile;

        public LeftViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);
            profile = itemView.findViewById(R.id.profile);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView date, checked;
        RecyclerView image;

        public ImageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            image = itemView.findViewById(R.id.image_recycler);
            checked = itemView.findViewById(R.id.checked);

        }
    }

    public class LeftImageViewHolder extends RecyclerView.ViewHolder {
        TextView name, content, date;
        ImageView profile;
        RecyclerView image;

        public LeftImageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);
            profile = itemView.findViewById(R.id.profile);
            image = itemView.findViewById(R.id.image_recycler);
        }
    }

    public class LeftCallViewHolder extends RecyclerView.ViewHolder {
        TextView name, content, date;
        ImageView profile, video_img;

        public LeftCallViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);
            video_img = itemView.findViewById(R.id.video_img);
            profile = itemView.findViewById(R.id.profile);
        }
    }

    public class CallViewHolder extends RecyclerView.ViewHolder {
        TextView content, date, checked;
        ImageView video_img;

        public CallViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.date);
            checked = itemView.findViewById(R.id.checked);
            video_img = itemView.findViewById(R.id.video_img);
        }
    }

    public int getPref() {
        SharedPreferences pref = c.getSharedPreferences("LOGIN", MODE_PRIVATE);
        return Integer.parseInt(pref.getString("Login_data", ""));
    }
}
