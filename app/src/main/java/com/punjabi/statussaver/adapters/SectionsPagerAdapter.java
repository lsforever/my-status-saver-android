package com.punjabi.statussaver.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.punjabi.statussaver.R;
import com.punjabi.statussaver.fragments.ImagesFragment;
import com.punjabi.statussaver.fragments.VideoFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter{

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1,R.string.tab_text_2};
    private final Context mContext;
    private boolean isSaved;


    public SectionsPagerAdapter(Context context,FragmentManager fm, boolean isSaved){
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        this.isSaved = isSaved;
    }

    @NonNull
    @Override
    public Fragment getItem(int position){
        if(position==0){
            return ImagesFragment.newInstance(isSaved);
        }else {
            return VideoFragment.newInstance(isSaved);
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        return mContext.getResources().getString(TAB_TITLES[position]);
    }



    @Override
    public int getCount(){
        return TAB_TITLES.length;
    }
}