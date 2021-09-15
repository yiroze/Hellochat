package com.example.hellochat.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hellochat.Adapter.DetailAdapter;
import com.example.hellochat.DTO.DetailData;
import com.example.hellochat.DTO.DetailResult;
import com.example.hellochat.NewsfeedApi;
import com.example.hellochat.R;
import com.example.hellochat.DTO.ResultData;
import com.example.hellochat.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Detail extends AppCompatActivity {
    DetailAdapter mAdapter;
    DetailResult datalist;
    RecyclerView mRecyclerView;
    List<DetailData> datainfo;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    EditText editText;
    Button submit;
    ImageView cancel;
    TextView writer, parent;
    RelativeLayout relativelayout;
    int page = 1, limit = 5;
    int feed , user;
    private static final String TAG = "Activity_Detail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        int feednum = intent.getIntExtra("feed_idx", 0);
        SharedPreferences pref = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String user = pref.getString("Login_data", "");
        int user_idx = Integer.parseInt(user);
        datainfo = new ArrayList<>();
        final InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mAdapter = new DetailAdapter(datainfo);
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(Activity_Detail.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //리사이클러뷰 구분선
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), R.drawable.divider_line));

        getDetail(feednum, user_idx, page, limit);

        submit = (Button) findViewById(R.id.submit);
        editText = (EditText) findViewById(R.id.comment_edit);
        writer = (TextView) findViewById(R.id.writer);
        parent = (TextView) findViewById(R.id.parent);
        relativelayout = (RelativeLayout) findViewById(R.id.relativelayout);
        cancel = (ImageView) findViewById(R.id.cancel);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_view);


        submit.setOnClickListener(v -> {
            if (!editText.getText().toString().equals("")) {
                if (parent.getText().toString().equals("")) {
                    setComment(feednum, user_idx, editText.getText().toString(), 0, page, limit);
                    editText.setText("");
                } else {
                    setComment(feednum, user_idx, editText.getText().toString(), Integer.parseInt(parent.getText().toString()), page, limit);
                    editText.setText("");
                    parent.setText("");
                    relativelayout.setVisibility(View.GONE);
                }
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        });

        cancel.setOnClickListener(v -> {
            relativelayout.setVisibility(View.GONE);
            writer.setText("");
            parent.setText("");
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    getDetail(feednum, user_idx, page, limit);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10){
            if(resultCode != 11){
                return;
            }
            getDetail(feed,user,page,limit);
        }


    }

    public void getDetail(int feed_idx, int user_idx, int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<DetailResult> call = service.get_detail(feed_idx, user_idx, page, limit);
        feed = feed_idx ;
        user = user_idx;
        call.enqueue(new Callback<DetailResult>() {
            @Override
            public void onResponse(Call<DetailResult> call, Response<DetailResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    datalist = response.body();
                    datainfo = datalist.body;
                    Log.d(TAG, "onResponse: " + datainfo);
                    mAdapter = new DetailAdapter(datainfo);
                    Log.d(TAG, "onResponse: setadapter");
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    //댓글 아이템 클릭
                    mAdapter.setOnCommentClickListener(new DetailAdapter.OnCommentClickListener() {
                        @Override
                        public void onCommentClickListener(View v, int pos) {
                            Log.d(TAG, "onClick: 뷰타입 1번");
                            writer.setText(String.format("%s에게 답글", datainfo.get(pos).name));
                            writer.setVisibility(View.VISIBLE);
                            String a = String.valueOf(datainfo.get(pos).comment_idx);
                            parent.setText(a);
                            relativelayout.setVisibility(View.VISIBLE);
                        }
                    });
                    //글 좋아요 클릭
                    mAdapter.setOnClickListener(new DetailAdapter.OnLikeClick() {
                        @Override
                        public void onLikeClick(View v, int pos) {
                            ClickHeart(feed_idx, user_idx, page, limit);
                        }
                    });
                    //답글 수정삭제버튼
                    mAdapter.setOnMoreBntClick_Reply(new DetailAdapter.OnMoreBntClick_Reply() {
                        @Override
                        public void onMoreBntClick_Reply(View v, int pos) {
                            String items[] = {"수정하기", "삭제하기"};
                            AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
                            dia.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        Intent intent = new Intent(Activity_Detail.this, Activity_modify_comment.class);
                                        intent.putExtra("contents" , datainfo.get(pos).contents);
                                        intent.putExtra("comment_idx", datainfo.get(pos).comment_idx);
                                        startActivityForResult(intent , 10);
                                    } else if (which == 1) {
                                        Delete_Reply(datainfo.get(pos).comment_idx, feed_idx, user_idx, page, limit);
                                    }
                                }
                            });
                            AlertDialog alertDialog = dia.create();
                            alertDialog.show();
                        }
                    });
                    //댓글 수정삭제버튼
                    mAdapter.setOnMoreBntClick_Comment(new DetailAdapter.OnMoreBntClick_Comment() {
                        @Override
                        public void onMoreBntClick_Comment(View v, int pos) {
                            String items[] = {"수정하기", "삭제하기"};
                            AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
                            dia.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        Intent intent = new Intent(Activity_Detail.this, Activity_modify_comment.class);
                                        intent.putExtra("contents" , datainfo.get(pos).contents);
                                        intent.putExtra("comment_idx", datainfo.get(pos).comment_idx);
                                        startActivityForResult(intent , 10);
                                    } else if (which == 1) {
                                        Delete_Comment(datainfo.get(pos).comment_idx, feed_idx, user_idx, page, limit);
                                    }
                                }
                            });
                            AlertDialog alertDialog = dia.create();
                            alertDialog.show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DetailResult> call, Throwable t) {
                Log.d(TAG, "onFailure: 통신실패" + t.getMessage());
            }
        });
    }

    public void setComment(int feed_idx, int user_idx, String comment, int parent, int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.set_comment(feed_idx, user_idx, comment, parent);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                if (response.isSuccessful()) {
                    getDetail(feed_idx, user_idx, page, limit);
                }
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {
            }
        });


    }

    //리사이클러뷰 구분선 없앤거
    public class DividerItemDecoration extends RecyclerView.ItemDecoration {
        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable divider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        /**
         * Custom divider will be used
         */
        public DividerItemDecoration(Context context, int resId) {
            divider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();
                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }

    public void ClickHeart(int feed_idx, int user_idx, int page, int limit) {
        NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
        Call<ResultData> call = service.click_like(feed_idx, user_idx);
        call.enqueue(new Callback<ResultData>() {
            @Override
            public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                getDetail(feed_idx, user_idx, page, limit);
            }

            @Override
            public void onFailure(Call<ResultData> call, Throwable t) {

            }
        });

    }

    public void Delete_Comment(int comment_idx, int feed_idx, int user_idx, int page, int limit) {
        //댓글은 업데이트
        AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
        dia.setTitle("삭제하시겠습니까?");
        dia.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
                Call<ResultData> call = service.delete_comment(comment_idx);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        //메시지 받기
                        //alert띄우고 리사이클러뷰 새로고침
                        if (response.isSuccessful()) {
                            ResultData resultData = response.body();
                            Log.d(TAG, "onResponse: " + resultData.body);
                            if (resultData.body.equals("ok")) {
                                getDetail(feed_idx, user_idx, page, limit);
                                Log.d(TAG, "onResponse: ");
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

    public void Delete_Reply(int comment_idx, int feed_idx, int user_idx, int page, int limit) {
        //대댓글은 삭제
        AlertDialog.Builder dia = new AlertDialog.Builder(Activity_Detail.this);
        dia.setTitle("삭제하시겠습니까?");
        dia.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NewsfeedApi service = RetrofitClientInstance.getRetrofitInstance().create(NewsfeedApi.class);
                Call<ResultData> call = service.delete_reply(comment_idx);
                call.enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        //메시지 받기
                        //alert띄우고 리사이클러뷰 새로고침
                        if (response.isSuccessful()) {
                            ResultData resultData = response.body();
                            Log.d(TAG, "onResponse: " + resultData.body);
                            if (resultData.body.equals("ok")) {
                                getDetail(feed_idx, user_idx, page, limit);
                                Log.d(TAG, "onResponse: ");
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