package com.google.developer.colorvalue;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.google.developer.colorvalue.service.NotificationJobService;

public class SettingsActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private static final int JOB_ID = 88;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final String notifyKey = getString(R.string.pref_key_notification);
        if (key.equals(notifyKey)) {
            boolean on = sharedPreferences.getBoolean(notifyKey, false);
            JobScheduler jobScheduler =
                    (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            ComponentName componentName = new ComponentName(this, NotificationJobService.class);
            JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                    .setPeriodic(300000)
                    .setPersisted(true)
                    .build();
            if(on){
                jobScheduler.schedule(jobInfo);
            }else{
                jobScheduler.cancelAll();
            }

        }
    }

    /**
     * loading setting resource
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.preference);
        }

    }
}