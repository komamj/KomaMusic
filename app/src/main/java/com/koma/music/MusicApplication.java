package com.koma.music;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.bumptech.glide.Glide;
import com.koma.music.util.LogUtils;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by koma on 3/20/17.
 */

public class MusicApplication extends Application {
    private static final String TAG = MusicApplication.class.getSimpleName();

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.i(TAG, "onCreate");

        enableStrictMode();

        sContext = getApplicationContext();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public synchronized static Context getContext() {
        return sContext;
    }

    @Override
    public void onLowMemory() {
        LogUtils.i(TAG, "onLowMemory");

        //clear cache
        Glide.get(sContext).clearMemory();
        super.onLowMemory();
    }

    private void enableStrictMode() {
        final StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
                .detectAll().penaltyLog();
        final StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
                .detectAll().penaltyLog();

        threadPolicyBuilder.penaltyFlashScreen();
        StrictMode.setThreadPolicy(threadPolicyBuilder.build());
        StrictMode.setVmPolicy(vmPolicyBuilder.build());
    }
}
