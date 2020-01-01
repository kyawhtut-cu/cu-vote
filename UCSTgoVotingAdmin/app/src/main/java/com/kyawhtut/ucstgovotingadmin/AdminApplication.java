package com.kyawhtut.ucstgovotingadmin;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import timber.log.Timber;

public class AdminApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        CaocConfig.Builder.create()
                .showErrorDetails(BuildConfig.DEBUG)
                .apply();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
