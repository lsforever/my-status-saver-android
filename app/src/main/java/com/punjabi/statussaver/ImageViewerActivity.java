package com.punjabi.statussaver;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.punjabi.statussaver.adapters.SliderAdapter;
import com.punjabi.statussaver.utils.FileUtil;

public class ImageViewerActivity extends AppCompatActivity{

    private SliderAdapter sliderAdapter;
    private ViewPager viewPager;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        boolean isSaved = getIntent().getBooleanExtra("isSaved",false);

        if(isSaved){
            sliderAdapter = new SliderAdapter(this,FileUtil.getSavedImageFilesList(this));
        }else {
            sliderAdapter = new SliderAdapter(this,FileUtil.getImageFilesList());
        }

        //Setting up ViewPager
        viewPager = findViewById(R.id.view_pager_images);
        viewPager.setAdapter(sliderAdapter);

        int index = getIntent().getIntExtra("index",0);
        viewPager.setCurrentItem(index);

        int color = R.color.colorPrimary1;
        if(FileUtil.getThemeForNormalActivity(this) == R.style.AppTheme_mint_blue){
            color  = R.color.colorPrimary2;
        }
        SpeedDialView fab = findViewById(R.id.speed_dial_slider);


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
                        mInterstitialAd.show();
                    }
                });
                mInterstitialAd.loadAd(new AdRequest.Builder().build());


                FileUtil.saveMediaFile(this,sliderAdapter.getItemAtPosition(viewPager.getCurrentItem()),"Image Saved");
            }else if(id == R.id.action_share){
                FileUtil.shareMediaFile(this,sliderAdapter.getItemAtPosition(viewPager.getCurrentItem()),FileUtil.TYPE_IMAGE);
            }else if(id == R.id.action_repost){
                InterstitialAd mInterstitialAd =  new InterstitialAd(this);
                mInterstitialAd.setAdUnitId(FileUtil.ADMOB_INTERSTITIAL_AD_ID);
                mInterstitialAd.setAdListener(new AdListener(){

                    @Override
                    public void onAdClosed(){
                        super.onAdClosed();
                        FileUtil.rePostMediaFile(ImageViewerActivity.this,sliderAdapter.getItemAtPosition(viewPager.getCurrentItem()),FileUtil.TYPE_IMAGE);

                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError){
                        super.onAdFailedToLoad(loadAdError);
                        FileUtil.rePostMediaFile(ImageViewerActivity.this,sliderAdapter.getItemAtPosition(viewPager.getCurrentItem()),FileUtil.TYPE_IMAGE);
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
                new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this image?")
                        .setPositiveButton("Delete",((dialogInterface,i) -> {
                            int p = viewPager.getCurrentItem();
                            boolean isDeleted =FileUtil.deleteMediaFile(this,sliderAdapter.getItemAtPosition(p));
                            if(isDeleted){
                                if(sliderAdapter.getCount()==1){
                                    finish();
                                }else{
                                    sliderAdapter.removeItem(p);
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
                        .setNegativeButton("Cancel",null)
                        .setCancelable(true)
                        .show();
            } else {
                return false;
            }
            return true;
        });

        LinearLayout layout = findViewById(R.id.linear_image_v);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(FileUtil.ADMOB_BANNER_AD_ID);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded(){
                super.onAdLoaded();
                if(flag){
                    layout.addView(adView);
                    flag = false;
                }
            }
        });
        adView.loadAd( new AdRequest.Builder().build());

    }

}