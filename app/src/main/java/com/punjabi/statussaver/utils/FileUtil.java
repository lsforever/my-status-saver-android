package com.punjabi.statussaver.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import com.punjabi.statussaver.BuildConfig;
import com.punjabi.statussaver.R;
import com.punjabi.statussaver.SavedStatusesActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FileUtil{

    //public static final String ADMOB_APP_ID = "ca-app-pub-2699966296354925~6504612649";

    //Original Ids
//    public static final String ADMOB_INTERSTITIAL_AD_ID = "ca-app-pub-2401740192017724/3519127089";
//    public static final String ADMOB_BANNER_AD_ID = "ca-app-pub-2401740192017724/5023780448";
//    public static final String ADMOB_NATIVE_AD_ID = "ca-app-pub-2401740192017724/8852581919";
    /////////////////public static final String ADMOB_INTERSTITIAL_AD_ID_EXIT = "ca-app-pub-240174019­2017724/­5023780448";

//    //Test Ads
    public static final String ADMOB_INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712";
    public static final String ADMOB_BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111";
    public static final String ADMOB_NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110";

    private final static String GOOGLE_PLAY_STORE_ID = "NriPunjabi";
    //public final static String CONTACT_EMAIL = "hundas143@gmail.com";


    public static final File normalStatusDirectory = new File(Environment.getExternalStorageDirectory()+"/WhatsApp/Media/.Statuses");
    public static final File businessStatusDirectory = new File(Environment.getExternalStorageDirectory()+"/WhatsApp Business/Media/.Statuses");
    public static File currentDir = normalStatusDirectory;
    private static final String DEFAULT_SAVE_DIR_PATH = "/storage/emulated/0/Status Saver";
    public static final String TYPE_IMAGE = "image/*";
    public static final String TYPE_VIDEO = "video/*";
    public static final String PACKAGE_WHATSAPP = "com.whatsapp";
    public static final String PACKAGE_WHATSAPP_BUSINESS = "com.whatsapp.w4b";

    //public static final String ADMOB_APP_ID = "ca-app-pub-2699966296354925~6504612649";




    public static ArrayList<File> getImageFilesList(){
        ArrayList<File> files = new ArrayList<>();
        try {
            for(File file : Objects.requireNonNull(currentDir.listFiles())){
                if(checkType(file.getPath(),"image")){
                    files.add(file);
                }
            }
        }catch (Exception e){
            Log.w("error",e);
        }
        Collections.sort(files,(o1,o2) -> Long.compare(o2.lastModified(),o1.lastModified()));
        return files;
    }

    public static ArrayList<File> getVideoFilesList(){
        ArrayList<File> files = new ArrayList<>();
        try {
            for(File file : Objects.requireNonNull(currentDir.listFiles())){
                if(checkType(file.getPath(),"video")){
                    files.add(file);
                }
            }
        }catch (Exception e){
            Log.w("error",e);
        }
        Collections.sort(files,(o1,o2) -> Long.compare(o2.lastModified(),o1.lastModified()));
        return files;
    }




    private static boolean checkType(String path,String FileType){
        int index = path.lastIndexOf(".");
        if(index!= -1){
            String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(path.substring(index+1));
            return type!=null && type.startsWith(FileType);
        }
        return false;
    }


    private static boolean copyFile(File source,File destination){
        try {
            FileInputStream inputStream = new FileInputStream(source);
            FileOutputStream outputStream = new FileOutputStream(destination);
            FileChannel inChannel = inputStream.getChannel();
            FileChannel outChannel = outputStream.getChannel();
            inChannel.transferTo(0,inChannel.size(),outChannel);
            inputStream.close();
            outputStream.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean checkAppIsInstalledOrNot(Context context, String packageName){
        try {
            context.getPackageManager().getApplicationInfo(packageName,0);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    public static int getThemeForMainActivity(Context context){
        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
        String theme = prefs.getString("theme","theme01");
        if(theme==null){
            return R.style.AppTheme_whats_app_NoActionBar;
        }else if(theme.equals("theme01")){
            return R.style.AppTheme_whats_app_NoActionBar;
        }else {
            return R.style.AppTheme_mint_blue_NoActionBar;
        }
    }

    public static int getThemeForNormalActivity(Context context){
        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
        String theme = prefs.getString("theme","theme01");
        if(theme==null){
            return R.style.AppTheme_whats_app;
        }else if(theme.equals("theme01")){
            return R.style.AppTheme_whats_app;
        }else {
            return R.style.AppTheme_mint_blue;
        }
    }

    private static String getSaveFolderPath(Context context){
        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
        String path = prefs.getString("storage",DEFAULT_SAVE_DIR_PATH);
        if(path==null){
            return DEFAULT_SAVE_DIR_PATH;
        }else {
            return path;
        }
    }

    public static void saveMediaFile(Context context,File file,String toastMsg){
        File saveDir = new File(getSaveFolderPath(context));
        if(!saveDir.exists()){
            if(!saveDir.mkdirs()){
                return;
            }
        }

        if(!file.exists()){
            return;
        }

        File saveFile = new File(saveDir,file.getName());
        if(saveFile.exists()){
            if(!saveFile.delete()){
                return;
            }
        }
        if(FileUtil.copyFile(file,saveFile)){
            Toast.makeText(context,toastMsg,Toast.LENGTH_LONG).show();
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(saveFile));
            context.sendBroadcast(mediaScanIntent);
        }else {
            Toast.makeText(context,"Error Saving Status",Toast.LENGTH_LONG).show();
        }
    }

    public static void shareMediaFile(Context context,File mediaFile,String type){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM,FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID + ".fileprovider",mediaFile));
        shareIntent.setType(type);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(shareIntent,"send"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void rePostMediaFile(Context context,File mediaFile,String type){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(type);
        if(currentDir.equals(normalStatusDirectory)){
            intent.setPackage(PACKAGE_WHATSAPP);
        }else {
            intent.setPackage(PACKAGE_WHATSAPP_BUSINESS);
        }
        intent.putExtra(Intent.EXTRA_STREAM,FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID + ".fileprovider",mediaFile));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            Log.w("error",e);
            Toast.makeText(context,"App Not Installed In Device",Toast.LENGTH_LONG).show();
        }

    }

    public static void rateApp(Context context){
        Uri uri  = Uri.parse("market://details?id="+BuildConfig.APPLICATION_ID);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW,uri);
        if(Build.VERSION.SDK_INT>= 21){
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK );
        }else {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK );
        }
        try{
            context.startActivity(goToMarket);
        }catch (ActivityNotFoundException e ){
            context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID)));
        }
        Toast.makeText(context,"Thank You",Toast.LENGTH_SHORT).show();
    }


    public static void shareApp(Context context){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,context.getString(R.string.app_name));
        shareIntent.setType("text/plain");
        String shareMsg = "Hey! Try this awesome app '"+context.getString(R.string.app_name)+"' which helps you to easy download all the WhatsApp Statuses...!\n"+getEmoji(0x1F447)+"\nhttps://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID+"\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT,shareMsg);
        try {
            context.startActivity(Intent.createChooser(shareIntent,"Choose one"));
            Toast.makeText(context,"Thank You",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void aboutApp(Context context){
        String message = "How to use?\n" +
                "01. Check the Desired Status/Story...\n" +
                "02. Come Back to App, Click on Any Image or Video to View...\n" +
                "03. Click the Save Button... The Image or Video is Instantly Saved to Your Gallery :)\n" +
                "\n" +
                "Note. You can change theme color and save location from settings." +
                "\n\n\nProud to be Punjabi "+getEmoji(0x2764);
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.app_name))
                .setMessage(message)
                .setPositiveButton("OK",null)
                .setCancelable(true)
                .show();
    }

    public static void useApp(Context context){
        String message = "01. Check the Desired Status/Story...\n" +
                "02. Come Back to App, Click on Any Image or Video to View...\n" +
                "03. Click the Save Button...The Image or Video is Instantly Saved to Your Gallery :)\n" +
                "\n" +
                "Note. You can change theme color and save location from settings.";
        new AlertDialog.Builder(context)
                .setTitle("How to Use?")
                .setMessage(message)
                .setPositiveButton("OK!",null)
                .setCancelable(true)
                .show();
    }

    private static String getEmoji(int uniCodePoint){
        return new String(Character.toChars(uniCodePoint));
    }

//    public static void contactUs(Context context){
//        Intent intent = new Intent(Intent.ACTION_SENDTO
//        );
//        intent.setData(Uri.parse("mailto:"));
//
//        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{CONTACT_EMAIL});
//        intent.putExtra(Intent.EXTRA_SUBJECT,context.getString(R.string.app_name)+" Contact");
//        intent.putExtra(Intent.EXTRA_TEXT, "Enter your message here...");
//
//        try {
//            if(intent.resolveActivity(context.getPackageManager())!=null){
//                context.startActivity(Intent.createChooser(intent,"Send Mail"));
//            }else {
//                Toast.makeText(context,"There are no email clients installed",Toast.LENGTH_SHORT).show();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            Toast.makeText(context,"Unknown Error when trying to send email",Toast.LENGTH_SHORT).show();
//        }
//
//
//    }

//    public static void moreApps(Context context){
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(
//                "https://play.google.com/store/apps/dev?id="+GOOGLE_PLAY_STORE_ID
//        ));
//        //intent.setPackage(BuildConfig.APPLICATION_ID);
//        try {
//            context.startActivity(intent);
//        } catch (ActivityNotFoundException ae){
//            Toast.makeText(context,"Cannot find any app installed to handle the request",Toast.LENGTH_SHORT).show();
//        } catch (Exception e){
//            e.printStackTrace();
//            Toast.makeText(context,"Error Occurred",Toast.LENGTH_SHORT).show();
//        }
//    }

    public static void savedStatuses(Context context){
        Intent i = new Intent(context,SavedStatusesActivity.class);
        context.startActivity(i);
    }


    public static ArrayList<File> getSavedImageFilesList(Context context){
        ArrayList<File> files = new ArrayList<>();
        try {
            for(File file : Objects.requireNonNull(new File(getSaveFolderPath(context)).listFiles())){
                if(checkType(file.getPath(),"image")){
                    files.add(file);
                }
            }
        }catch (Exception e){
            Log.w("error",e);
        }
        Collections.sort(files,(o1,o2) -> Long.compare(o2.lastModified(),o1.lastModified()));
        return files;
    }


    public static ArrayList<File> getSavedVideoFilesList(Context context){
        ArrayList<File> files = new ArrayList<>();
        try {
            for(File file : Objects.requireNonNull(new File(getSaveFolderPath(context)).listFiles())){
                if(checkType(file.getPath(),"video")){
                    files.add(file);
                }
            }
        }catch (Exception e){
            Log.w("error",e);
        }
        Collections.sort(files,(o1,o2) -> Long.compare(o2.lastModified(),o1.lastModified()));
        return files;
    }

    public static boolean deleteMediaFile(Context context,File file){
        if(!file.exists()){
            return false;
        }else {
            if(file.delete()){
                Toast.makeText(context,"File Deleted",Toast.LENGTH_SHORT).show();
                return true;
            }else {
                Toast.makeText(context,"Error Deleting File",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }


}
