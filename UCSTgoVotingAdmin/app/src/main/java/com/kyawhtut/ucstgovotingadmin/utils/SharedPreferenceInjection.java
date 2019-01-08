package com.kyawhtut.ucstgovotingadmin.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceInjection {

    public static SharedPreferenceUtil provideSelection(Context ctx) {
        return SharedPreferenceUtil.create(provideSharedPreferences(ctx));
    }

    private static SharedPreferences provideSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
}
