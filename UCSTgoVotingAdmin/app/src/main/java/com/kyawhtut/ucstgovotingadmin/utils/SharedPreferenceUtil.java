package com.kyawhtut.ucstgovotingadmin.utils;

import android.content.SharedPreferences;

public class SharedPreferenceUtil {

    public static SharedPreferenceUtil INSTANCE;

    private SharedPreferences sharedPreferences;

    static SharedPreferenceUtil create(SharedPreferences sh) {
        if (INSTANCE == null) {
            INSTANCE = new SharedPreferenceUtil(sh);
        }
        return INSTANCE;
    }

    private SharedPreferenceUtil(SharedPreferences sh) {
        this.sharedPreferences = sh;
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void clearValue() {
        sharedPreferences.edit().clear().apply();
    }
}
