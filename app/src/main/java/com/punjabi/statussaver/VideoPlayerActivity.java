package com.punjabi.statussaver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.punjabi.statussaver.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity{

    private PlayerView playerView;
    private SimpleExoPlayer  player;
    private PlayerControlView controlView;
    private SpeedDialView fab;
    private ArrayList<File> videoFiles;

    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playBackPosition = 0;

    //private boolean flag = true;
    private boolean isSaved = false;
    private ConcatenatingMediaSource concatenatingMediaSource;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        fab = findViewById(R.id.speed_dial_video);
        playerView = findViewById(R.id.player_view);
        controlView = findViewById(R.id.controls);


        isSaved = getIntent().getBooleanExtra("isSaved",false);

        if(isSaved){
            videoFiles = FileUtil.getSavedVideoFilesList(this);
        }else {
            videoFiles = FileUtil.getVideoFilesList();
        }

        currentWindow = getIntent().getIntExtra("index",0);
        if(videoFiles.size()<=currentWindow){
            finish();
            return;
        }
        initFab();
        playerView.setControllerHideOnTouch(true);


        controlView.addVisibilityListener(visibility -> {
            if(visibility == View.VISIBLE){
                fab.setVisibility(View.INVISIBLE);
            }else {
                fab.setVisibility(View.VISIBLE);
            }
        });


        playerView.setOnTouchListener((view,motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(controlView.isVisible()){
                        controlView.hide();
                    }else {
                        controlView.show();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return true;
        });

        AdView adView =  findViewById(R.id.adView_a_video);
        adView.loadAd( new AdRequest.Builder().build());

    }

    private void initializePlayer(){
        player = new SimpleExoPlayer.Builder(this).build();
        //player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        controlView.setPlayer(player);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow,playBackPosition);
        player.prepare(buildMediaSource(),false,false);
    }

    private ConcatenatingMediaSource buildMediaSource(){
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,"exoplayer-codelab");
        ProgressiveMediaSource.Factory mediaSourceFactory= new ProgressiveMediaSource.Factory(dataSourceFactory);
        concatenatingMediaSource = new ConcatenatingMediaSource();
        for(File file : videoFiles){
            concatenatingMediaSource.addMediaSource(mediaSourceFactory.createMediaSource(Uri.fromFile(file)));
        }
        return concatenatingMediaSource;
    }


    private void releasePlayer(){
        if(player!=null){
            playWhenReady = player.getPlayWhenReady();
            playBackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }


    private void hideSystemUi(){
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }



    @Override
    protected void onStart(){
        super.onStart();
        if(Util.SDK_INT>=24){
            initializePlayer();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        hideSystemUi();
        if(Util.SDK_INT < 24 || player == null){
            initializePlayer();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(Util.SDK_INT<24){
            releasePlayer();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(Util.SDK_INT>=24){
            releasePlayer();
        }
    }

    private void initFab(){

        int color = R.color.colorPrimary1;
        if(FileUtil.getThemeForNormalActivity(this) == R.style.AppTheme_mint_blue){
            color  = R.color.colorPrimary2;
        }

        fab.setMainFabOpenedBackgroundColor(getResources().getColor(color));
        fab.setMainFabClosedBackgroundColor(getResources().getColor(color));

        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_repost,R.drawable.ic_reply_black_24dp)
                .setFabBackgroundColor(getResources().getColor(color))
                .setLabel("Repost")
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.GRAY)
                .setLabelColor(Color.WHITE)
                .create());

        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_share,R.drawable.ic_menu_share)
                .setFabBackgroundColor(getResources().getColor(color))
                .setLabel("Share")
                .setFabImageTintColor(Color.WHITE)
                .setLabelBackgroundColor(Color.GRAY)
                .setLabelColor(Color.WHITE)
                .create());

        if(isSaved){
            fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_delete,R.drawable.ic_baseline_delete_24)
                    .setFabBackgroundColor(getResources().getColor(color))
                    .setLabel("Delete")
                    .setFabImageTintColor(Color.WHITE)
                    .setLabelBackgroundColor(Color.GRAY)
                    .setLabelColor(Color.WHITE)
                    .create());
        }else{
            fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_save,R.drawable.ic_save_black_24dp)
                    .setFabBackgroundColor(getResources().getColor(color))
                    .setLabel("Save")
                    .setFabImageTintColor(Color.WHITE)
                    .setLabelBackgroundColor(Color.GRAY)
                    .setLabelColor(Color.WHITE)
                    .create());
        }


        fab.setOnActionSelectedListener(actionItem -> {
            if(fab.isOpen()){
                fab.close();
            }
            int id = actionItem.getId();

            if(id == R.id.action_save){
                InterstitialAd mInterstitialAd =  new InterstitialAd(this);
                mInterstitialAd.setAdUnitId(FileUtil.ADMOB_INTERSTITIAL_AD_ID);
                mInterstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdLoaded(){
                        super.onAdLoaded();
                        if(player!=null){
                            player.setPlayWhenReady(false);
                        }
                        mInterstitialAd.show();
                    }

                    @Override
                    public void onAdClosed(){
                        super.onAdClosed();
                        if(player!=null){
                            player.setPlayWhenReady(true);
                        }
                    }
                });
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                FileUtil.saveMediaFile(this,videoFiles.get(player.getCurrentWindowIndex()),"Video Saved");
            }else if(id == R.id.action_share){
                FileUtil.shareMediaFile(this,videoFiles.get(player.getCurrentWindowIndex()),FileUtil.TYPE_VIDEO);
            }else if(id == R.id.action_repost){
                InterstitialAd mInterstitialAd =  new InterstitialAd(this);
                mInterstitialAd.setAdUnitId(FileUtil.ADMOB_INTERSTITIAL_AD_ID);
                mInterstitialAd.setAdListener(new AdListener(){

                    @Override
                    public void onAdClosed(){
                        super.onAdClosed();
                        FileUtil.rePostMediaFile(VideoPlayerActivity.this,videoFiles.get(player.getCurrentWindowIndex()),FileUtil.TYPE_VIDEO);
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError){
                        super.onAdFailedToLoad(loadAdError);
                        FileUtil.rePostMediaFile(VideoPlayerActivity.this,videoFiles.get(player.getCurrentWindowIndex()),FileUtil.TYPE_VIDEO);
                    }

                    @Override
                    public void onAdLeftApplication(){
                        super.onAdLeftApplication();
                    }

                    @Override
                    public void onAdOpened(){
                        super.onAdOpened();
                    }

                    @Override
                    public void onAdClicked(){
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdImpression(){
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdLoaded(){
                        super.onAdLoaded();
                        mInterstitialAd.show();
                    }
                });
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

            }else if(id == R.id.action_delete){
                player.setPlayWhenReady(false);
                new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this video?")
                        .setPositiveButton("Delete",((dialogInterface,i) -> {
                            int p = player.getCurrentWindowIndex();
                            boolean isDeleted = FileUtil.deleteMediaFile(this,videoFiles.get(p));
                            if(isDeleted){
                                if(videoFiles.size()==1){
                                    finish();
                                }else{
                                    videoFiles.remove(p);
                                    concatenatingMediaSource.removeMediaSource(p);
                                }

                                InterstitialAd mInterstitialAd =  new InterstitialAd(this);
                                mInterstitialAd.setAdUnitId(FileUtil.ADMOB_INTERSTITIAL_AD_ID);
                                mInterstitialAd.setAdListener(new AdListener(){
                                    @Override
                                    public void onAdLoaded(){
                                        super.onAdLoaded();
                                        mInterstitialAd.show();
                                    }
                                });
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                            }
                        }))
                        .setNegativeButton("Cancel",((dialogInterface,i) -> {

                        }))
                        .setOnDismissListener((dialogInterface -> player.setPlayWhenReady(true)))
                        .setCancelable(true)
                        .show();
            }else {
                return false;
            }
            return true;
        });

    }

}
