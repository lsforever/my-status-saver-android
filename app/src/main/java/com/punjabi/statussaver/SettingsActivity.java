package com.punjabi.statussaver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.punjabi.statussaver.utils.FileUtil;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static boolean isThemeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(FileUtil.getThemeForNormalActivity(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings,new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



    }

    public static class SettingsFragment extends PreferenceFragmentCompat{
        @Override
        public void onCreatePreferences(Bundle savedInstanceState,String rootKey){
            setPreferencesFromResource(R.xml.root_preferences,rootKey);

            Preference myPref = findPreference("storage");
            Preference version = findPreference("version");
            String path = PreferenceManager.getDefaultSharedPreferences(myPref.getContext()).getString("storage","null");
            if(path != null && !path.equals("null")){
                myPref.setSummary(path);
            }
            version.setSummary(BuildConfig.VERSION_NAME);
            myPref.setOnPreferenceClickListener(preference -> {
                new ChooserDialog(myPref.getContext())
                        .withStartFile(Environment.getExternalStorageDirectory().getAbsolutePath())
                        .cancelOnTouchOutside(true)
                        .displayPath(true)
                        .withFilter(true ,false)
                        .withIcon(R.drawable.ic_folder_black_24dp)
                        .withStringResources("Choose a folder","CHOOSE","CANCEL")
                        .withChosenListener((s,file) -> {
                            SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(myPref.getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("storage",s);
                            editor.apply();
                            myPref.setSummary(s);
                        })
                        .build()
                        .show();
                return true;
            });

        }
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key){
        if(key.equals("theme")){
            isThemeChanged = true;
            recreate();
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(isThemeChanged){
            isThemeChanged = false;
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else {
            super.onBackPressed();
        }
    }

}