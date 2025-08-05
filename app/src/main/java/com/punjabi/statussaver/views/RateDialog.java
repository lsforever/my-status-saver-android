package com.punjabi.statussaver.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.punjabi.statussaver.R;
import com.punjabi.statussaver.utils.FileUtil;

import static android.content.Context.MODE_PRIVATE;

public class RateDialog{

    public  void showRateDialog(@NonNull Context context){
        new AlertDialog.Builder(context)
                .setView(
                        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                .inflate(R.layout.dialog_rate,null,false)
                        )
                .setCancelable(true)
                .setPositiveButton("RATE",(dialog,which) -> FileUtil.rateApp(context))
                .setNegativeButton("CANCEL",null)
                .setNeutralButton("NEVER SHOW AGAIN",(dialog,which) -> {
                    SharedPreferences prefs = context.getSharedPreferences("app_data",MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("last_time",-2);
                    editor.apply();
                })
                .create()
                .show();
    }

}
