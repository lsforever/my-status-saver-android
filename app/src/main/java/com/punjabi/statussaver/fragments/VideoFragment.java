package com.punjabi.statussaver.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.punjabi.statussaver.R;
import com.punjabi.statussaver.VideoPlayerActivity;
import com.punjabi.statussaver.adapters.ImageAdapter;
import com.punjabi.statussaver.adapters.VideoAdapter;
import com.punjabi.statussaver.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class VideoFragment extends Fragment{

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private boolean isSaved;


    public VideoFragment(boolean isSaved){
        // Required empty public constructor
        this.isSaved = isSaved;
    }


    public static VideoFragment newInstance(boolean isSaved){
        return new VideoFragment(isSaved);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_video,container,false);
        recyclerView = v.findViewById(R.id.recyclerVideo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        setAdapter();
        ImageView imageView = v.findViewById(R.id.img_bg_v);
        Glide.with(this).load(R.drawable.bg_pic_1).thumbnail(0.1f).into(imageView);

        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        AsyncTask.execute(() -> {
            ArrayList<File> arrayList1;
            if(isSaved){
                arrayList1 = FileUtil.getSavedVideoFilesList(getContext());
            }else {
                arrayList1 = FileUtil.getVideoFilesList();
            }

            ArrayList<File> arrayList2 = new ArrayList<>(videoAdapter.getVideoFilesList());
            Collections.sort(arrayList1);
            Collections.sort(arrayList2);
            if(!arrayList1.equals(arrayList2) && getActivity()!=null){
                getActivity().runOnUiThread(this::setAdapter);
            }
        });
    }

    private void setAdapter(){
        if(isSaved){
            videoAdapter  = new VideoAdapter(getContext(),FileUtil.getSavedVideoFilesList(getContext()));
        }else {
            videoAdapter  = new VideoAdapter(getContext(),FileUtil.getVideoFilesList());
        }

        videoAdapter.setOnItemClickListener(this::onItemClick);
        if(videoAdapter.getVideoFilesList().size()==0){
            recyclerView.setVisibility(View.INVISIBLE);
        }else {
            recyclerView.setAdapter(videoAdapter);
            if(recyclerView.getVisibility() == View.INVISIBLE){
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }


    private void onItemClick(int p){
        Intent intent = new Intent(getContext(),VideoPlayerActivity.class);
        if(isSaved){
            intent.putExtra("isSaved",true);
        }
        intent.putExtra("index",p);
        startActivity(intent);
    }

}
