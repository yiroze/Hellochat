package com.example.hellochat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NewsfeedAdapter extends RecyclerView.Adapter<NewsfeedAdapter.NewsfeedViewHolder> {
    private Context c;
    private ArrayList<ViewData> datalist =new ArrayList<>();
    int idx;
    private static final String TAG = "NewsfeedAdapter";

    private OnItemClickListener mListener  ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public NewsfeedAdapter(ArrayList<ViewData> datalist ) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public NewsfeedAdapter.NewsfeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        c = parent.getContext();
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeeditem ,parent , false);
        NewsfeedViewHolder viewHolder = new NewsfeedViewHolder(view );
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsfeedViewHolder holder, int position) {
        // 나의 게시글이 아닐경우 수정삭제가 없도록한다
        idx = Integer.parseInt(getPref());
        if (datalist.get(position).user_idx != idx) {
            holder.more.setVisibility(View.INVISIBLE);
        } else {
            holder.more.setVisibility(View.VISIBLE);
        }
        //유저의 제 2,3의 모국어나 학습언어가 없을시 보이지않게 처리
        //모국어2
        if (datalist.get(position).mylang2 == null) {
            holder.mylang_layout2.setVisibility(View.GONE);
        } else {
            holder.mylang2.setText(datalist.get(position).mylang2);
        }
        //모국어3
        if (datalist.get(position).mylang3 == null) {
            holder.mylang_layout3.setVisibility(View.GONE);
        } else {
            holder.mylang3.setText(datalist.get(position).mylang3);
        }
        //학습언어2
        if (datalist.get(position).studylang2 == null) {
            holder.study_lang_layout2.setVisibility(View.GONE);
        } else {
            holder.study_lang2.setText(datalist.get(position).studylang2);
            holder.study_lang_level2.setProgress(datalist.get(position).studylang_level2);
        }
        //학습언어3
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
            //이미지를 넣어주기 위해 이미지url을 가져온다.
            String imageUrl = "http://3.37.204.197/hellochat/" + datalist.get(position).profile;
            //영상 썸네일 세팅
            Glide.with(holder.profile)
                    .load(imageUrl)
                    .into(holder.profile);
        }

    }
    @Override
    public int getItemCount() {
        return (null != datalist ? datalist.size() : 0);
    }


    public class NewsfeedViewHolder extends RecyclerView.ViewHolder  {

        protected LinearLayout mylang_layout2, mylang_layout3, study_lang_layout2, study_lang_layout3;
        protected TextView name, mylang, mylang2, mylang3, study_lang, study_lang2, study_lang3, heart_count, comment_count, contents, date;
        protected ProgressBar mylang_level, study_lang_level, study_lang_level2, study_lang_level3;
        protected ImageView heart, profile, more;

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


            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d(TAG, "onClick: " + mListener);
                    if(mListener != null){
                        mListener.onItemClick(v , pos);
                    }else {
                        Log.d(TAG, "onClick: null");
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d(TAG, "onClick: 아이템클릭");
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
                    String items[] = {"수정하기", "삭제하기"};
                    AlertDialog.Builder dia = new AlertDialog.Builder(c);
                    dia.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent intent = new Intent(c, Activity_modify.class);
                                intent.putExtra("feed_idx", datalist.get(pos).feed_idx);
                                v.getContext().startActivity(intent);
                                dialog.dismiss();
                            } else if (which == 1) {
                                Delete(datalist.get(pos).feed_idx, pos);
                            }
                        }
                    });
                    AlertDialog alertDialog = dia.create();
                    alertDialog.show();
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



    public void Delete(int feed_idx, int pos) {
        AlertDialog.Builder dia = new AlertDialog.Builder(c);
        dia.setTitle("삭제하시겠습니까?");
        dia.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
                Call<ResultData> call = service.delete_post(feed_idx);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        //메시지 받기
                        //alert띄우고 리사이클러뷰 새로고침
                        if (response.isSuccessful()) {
                            ResultData resultData = response.body();
                            Log.d(TAG, "onResponse: " + resultData.body);
                            if (resultData.body.equals("ok")) {
                                Log.d(TAG, "onResponse: ");
                                datalist.remove(pos);
                                notifyItemRemoved(pos);
                                notifyDataSetChanged();
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                    }
                });
            }
        });
        dia.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = dia.create();
        alertDialog.show();
    }





}



