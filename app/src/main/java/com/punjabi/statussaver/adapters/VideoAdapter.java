package com.punjabi.statussaver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.punjabi.statussaver.R;

import java.io.File;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<File> mVideosList;
    private OnItemClickListener mListener;

    public VideoAdapter(Context context, ArrayList<File> list) {
        this.mContext = context;
        this.mVideosList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_image,parent,false);
        return new MyViewHolder(v,mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position){
        Glide.with(mContext).load(mVideosList.get(position)).thumbnail(0.25f).into(holder.imageView);
    }

    @Override
    public int getItemCount(){
        return mVideosList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        MyViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_pic);
            imageView.setOnClickListener(v -> {
                if(listener!=null){
                    int position  = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION){
                        listener.onImageClick(position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onImageClick(int p);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public  ArrayList<File> getVideoFilesList() {
        return mVideosList;
    }

}

