package com.example.hellochat.Adapter.Setting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellochat.DTO.Feed.LevelData;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SelectLangLevelAdapter extends RecyclerView.Adapter<SelectLangLevelAdapter.ViewHolder> {

    ArrayList<LevelData> mList;

    public SelectLangLevelAdapter(ArrayList<LevelData> data){
        mList = data;
    }
    @NonNull
    @NotNull
    @Override
    public SelectLangLevelAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.languagelevelitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SelectLangLevelAdapter.ViewHolder holder, int position) {
        holder.level.setText(mList.get(position).Level_name);
        holder.info.setText(mList.get(position).Level_info);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView level , info;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            level = itemView.findViewById(R.id.level);
            info = itemView.findViewById(R.id.info);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (mListener != null) {
                        mListener.onItemClick(v, pos);
                    }
                }
            });
        }
    }
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

}
