package com.punjabi.statussaver.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SquareLayout extends FrameLayout {

    public SquareLayout(@NonNull Context context){
        super(context);
    }

    public SquareLayout(@NonNull Context context,@Nullable AttributeSet attrs){
        super(context,attrs);
    }

    public SquareLayout(@NonNull Context context,@Nullable AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        //noinspection SuspiciousNameCombination
        super.onMeasure(widthMeasureSpec,widthMeasureSpec);
    }

}
