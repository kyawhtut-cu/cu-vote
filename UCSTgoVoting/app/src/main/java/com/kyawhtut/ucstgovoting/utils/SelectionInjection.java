package com.kyawhtut.ucstgovoting.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SelectionInjection {

    public static SelectionUtil provideSelection(Context ctx) {
        return SelectionUtil.create(provideSharedPreferences(ctx));
    }

    private static SharedPreferences provideSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }
}
