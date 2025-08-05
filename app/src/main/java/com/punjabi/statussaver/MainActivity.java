package com.punjabi.statussaver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.button.MaterialButton;
import com.punjabi.statussaver.views.RateDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.punjabi.statussaver.adapters.SectionsPagerAdapter;
import com.punjabi.statussaver.utils.FileUtil;
import com.punjabi.statussaver.utils.Permissions;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private NavigationView navigationView;

    private UnifiedNativeAd unifiedNativeAd;
    private AdLoader adLoader;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setTheme(FileUtil.getThemeForMainActivity(this));
        setContentView(R.layout.activity_main);


        //Setting up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting up Navigation Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Version setting is commented because client asked
        //TextView textView = navigationView.getHeaderView(0).findViewById(R.id.v_in_nav_t);
        //textView.setText("Version  "+ BuildConfig.VERSION_NAME);


        //Setting up Tabs
        sectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager(),false);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Permissions.checkPermission(this);



        AdView banner = findViewById(R.id.adView_B_Main);
        //MobileAds.initialize(this,FileUtil.ADMOB_APP_ID);
        //MobileAds.initialize(this,initializationStatus -> Log.i("Ads","Initialized"));

        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);

//        InterstitialAd mInterstitialAd =  new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(FileUtil.ADMOB_INTERSTITIAL_AD_ID);
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        final Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            if(mInterstitialAd.isLoaded()){
//                mInterstitialAd.show();
//            }else {
//                mInterstitialAd.setAdListener(new AdListener(){
//                    @Override
//                    public void onAdLoaded(){
//                        super.onAdLoaded();
//                        mInterstitialAd.show();
//                    }
//                });
//            }
//        },30000);



        //Exit ad
        //mInterstitialAdExit = new InterstitialAd(this);
        //mInterstitialAdExit.setAdUnitId(FileUtil.ADMOB_INTERSTITIAL_AD_ID_EXIT);
        //mInterstitialAdExit.loadAd(new AdRequest.Builder().build());

        AsyncTask.execute(() -> {
            SharedPreferences prefs = getSharedPreferences("app_data",MODE_PRIVATE);
            long last_time = prefs.getLong("last_time",-1);
            if(last_time == -2){
                return;
            }

            Date now = new Date();
            if(last_time == -1){
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("last_time",now.getTime());
                editor.apply();
            }else {
                long diff = now.getTime() - last_time;
                if(diff<=0){
                    return;
                }

                long dates = TimeUnit.MILLISECONDS.toDays(diff);

                if(dates >= 15){
                    runOnUiThread(() -> new RateDialog().showRateDialog(this));
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("last_time",now.getTime());
                    editor.apply();
                }
            }
        });

        adLoader = new AdLoader.Builder(this, FileUtil.ADMOB_NATIVE_AD_ID)
                .forUnifiedNativeAd(_unifiedNativeAd -> unifiedNativeAd = _unifiedNativeAd).build();
        loadNativeAd();

    }

    private void loadNativeAd(){
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            if(mInterstitialAdExit!=null && mInterstitialAdExit.isLoaded()){
//                mInterstitialAdExit.setAdListener(new AdListener(){
//
//                });
//                mInterstitialAdExit.show();
//            }else {
//                super.onBackPressed();
//            }

            showNative();
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.actionA_help){
            FileUtil.useApp(this);
            return true;
        }else  if(id == R.id.actionA_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else  if(id == R.id.actionA_share){
            FileUtil.shareApp(this);
            return true;
        }
//        else  if(id == R.id.actionA_more_apps){
//            FileUtil.savedStatuses(this);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(id == R.id.nav_home){
            if(!FileUtil.currentDir.equals(FileUtil.normalStatusDirectory)){
                if(FileUtil.checkAppIsInstalledOrNot(this,FileUtil.PACKAGE_WHATSAPP)){
                    FileUtil.currentDir = FileUtil.normalStatusDirectory;
                    resetAdapter();
                }else {
                    new AlertDialog.Builder(this)
                            .setTitle("App Not Found !")
                            .setMessage("We cannot find App on this device.\n\nPlease recheck or download App to show statuses")
                            .setPositiveButton("CANCEL",null)
                            .setCancelable(true)
                            .show();
                }
            }
        }else if(id == R.id.nav_business){
            if(!FileUtil.currentDir.equals(FileUtil.businessStatusDirectory)){
                if(FileUtil.checkAppIsInstalledOrNot(this,FileUtil.PACKAGE_WHATSAPP_BUSINESS)){
                    FileUtil.currentDir = FileUtil.businessStatusDirectory;
                    resetAdapter();
                }else {
                    new AlertDialog.Builder(this)
                            .setTitle("App Not Found !")
                            .setMessage("We cannot find 'Business App' on this device.\n\nPlease recheck or download App to show statuses")
                            .setPositiveButton("CANCEL",null)
                            .setCancelable(true)
                            .show();
                }
            }
        }else if(id == R.id.nav_saved_statuses){
            FileUtil.savedStatuses(this);
        }else if(id == R.id.nav_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_share){
            FileUtil.shareApp(this);
        } else if(id == R.id.nav_rate){
            //new RateDialog().showRateDialog(this);
            FileUtil.rateApp(this);
        } else if(id == R.id.nav_about){
           FileUtil.aboutApp(this);
        }

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume(){
        super.onResume();
        if(navigationView.getCheckedItem()!=null){
            if(FileUtil.currentDir.equals(FileUtil.normalStatusDirectory)){
                navigationView.setCheckedItem(R.id.nav_home);
            }else {
                navigationView.setCheckedItem(R.id.nav_business);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if(requestCode == Permissions.PERMISSION_REQUEST_CODE && Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            for(int result : grantResults){
                if(!(result == PackageManager.PERMISSION_GRANTED)){
                    new AlertDialog.Builder(this)
                            .setTitle("Notice")
                            .setMessage("This application requires some device permissions to run. Please enable the required permissions to continue.")
                            .setPositiveButton("OK",(dialog,which) -> Permissions.checkPermission(this))
                            .setNegativeButton("CLOSE",(dialog,which) -> finish())
                            .setNeutralButton("SETTINGS", (dialog,which) -> {})
                            .setCancelable(false)
                            .show();
                    return;
                }
            }
            resetAdapter();
        }

    }

    private void  resetAdapter(){
        viewPager.setAdapter(sectionsPagerAdapter);
    }




    private void showNative(){
        CardView cardView = (CardView) getLayoutInflater()
                .inflate(R.layout.nartive_ad_a, null);
        UnifiedNativeAdView adView = (UnifiedNativeAdView) cardView.findViewById(R.id.ad_view);

        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        if(unifiedNativeAd == null){
            cardView.setVisibility(View.GONE);
            AdLoader adLoader = new AdLoader.Builder(this, FileUtil.ADMOB_NATIVE_AD_ID)
                    .forUnifiedNativeAd(_unifiedNativeAd -> {
                        populateNativeAdView(_unifiedNativeAd,adView);
                        cardView.setVisibility(View.VISIBLE);
                    }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }else {
            populateNativeAdView(unifiedNativeAd,adView);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sure to exit?");
        builder.setView(cardView);
        builder.setPositiveButton("Exit",((dialogInterface,i) -> finish()) );
        builder.setNegativeButton("Stay", null);
        builder.setOnDismissListener((dialogInterface -> {
            if(!adLoader.isLoading()){
                loadNativeAd();
            }
        }));
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }
}
