package com.punjabi.statussaver.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class Permissions{

    public static int PERMISSION_REQUEST_CODE = 1111;

    public static void checkPermission(Activity activity){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                activity.requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
            }
        }
    }
}
