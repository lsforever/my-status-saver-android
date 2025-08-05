package com.punjabi.statussaver.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.chrisbanes.photoview.PhotoView;
import com.punjabi.statussaver.R;

import java.io.File;
import java.util.ArrayList;

public class SliderAdapter extends PagerAdapter{

    private Context context;
    private ArrayList<File> files;

    public SliderAdapter(Context context, ArrayList<File> list){
        this.context = context;
        this.files = list;
    }

    @Override
    public int getCount(){
        return files.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view,@NonNull Object object){
        return view == object;
    }



    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container,int position){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_image_slide,container,false);

        PhotoView photoView  = itemView.findViewById(R.id.imageViewItem);
        photoView.setImageURI(Uri.fromFile(files.get(position)));
        photoView.setBackgroundColor(Color.BLACK);
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container,int position,@NonNull Object object){
        //container.removeView((FrameLayout) object);
        container.removeView((View) object);
    }

    public File getItemAtPosition(int position){
        return files.get(position);
    }

    public void removeItem(int position) {
        if (position > -1 && position < files.size()) {
            files.remove(position);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object){
        return PagerAdapter.POSITION_NONE;
    }
}
