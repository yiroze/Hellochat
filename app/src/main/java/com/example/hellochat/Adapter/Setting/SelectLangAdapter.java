package com.example.hellochat.Adapter.Setting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hellochat.DTO.Feed.LanguageData;
import com.example.hellochat.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SelectLangAdapter extends RecyclerView.Adapter<SelectLangAdapter.ViewHolder>{

    private ArrayList<LanguageData> mData;
    public SelectLangAdapter(ArrayList<LanguageData> list){
        mData = list;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.lang_en.setText(mData.get(position).Language_en);
        holder.lang_native.setText(mData.get(position).Language_native);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lang_en , lang_native;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            lang_en = itemView.findViewById(R.id.lang_en);
            lang_native = itemView.findViewById(R.id.lang_native);

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
