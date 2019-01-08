package com.kyawhtut.ucstgovoting;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import timber.log.Timber;

public class VotingApplication extends Application {

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
