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
import com.punjabi.statussaver.ImageViewerActivity;
import com.punjabi.statussaver.R;
import com.punjabi.statussaver.adapters.ImageAdapter;
import com.punjabi.statussaver.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class ImagesFragment extends Fragment{

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private boolean isSaved;


    public ImagesFragment(boolean isSaved){
        // Required empty public constructor
        this.isSaved = isSaved;
    }

    public static ImagesFragment newInstance(boolean isSaved){
        return new ImagesFragment(isSaved);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_images,container,false);
        recyclerView = v.findViewById(R.id.recyclerImages);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        setAdapter();
        ImageView imageView = v.findViewById(R.id.img_bg_i);
        Glide.with(this).load(R.drawable.bg_pic_1).thumbnail(0.1f).into(imageView);
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        AsyncTask.execute(() -> {
            ArrayList<File> arrayList1;
            if(isSaved){
                arrayList1 = FileUtil.getSavedImageFilesList(getContext());
            }else {
                arrayList1 = FileUtil.getImageFilesList();
            }
            ArrayList<File> arrayList2 = new ArrayList<>(imageAdapter.getPictureFilesList());;

            Collections.sort(arrayList1);
            Collections.sort(arrayList2);
            if(!arrayList1.equals(arrayList2) && getActivity()!=null){
                getActivity().runOnUiThread(this::setAdapter);
            }
        });
    }

    private void setAdapter(){
        if(isSaved){
            imageAdapter  = new ImageAdapter(getContext(),FileUtil.getSavedImageFilesList(getContext()));
        }else {
            imageAdapter  = new ImageAdapter(getContext(),FileUtil.getImageFilesList());
        }

        imageAdapter.setOnItemClickListener(this::onItemClick);
        if(imageAdapter.getPictureFilesList().size()==0){
            recyclerView.setVisibility(View.INVISIBLE);
        }else {
            recyclerView.setAdapter(imageAdapter);
            if(recyclerView.getVisibility() == View.INVISIBLE){
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void onItemClick(int p){
        Intent intent = new Intent(getContext(),ImageViewerActivity.class);
        if(isSaved){
            intent.putExtra("isSaved",true);
        }
        intent.putExtra("index",p);
        startActivity(intent);
    }


}
