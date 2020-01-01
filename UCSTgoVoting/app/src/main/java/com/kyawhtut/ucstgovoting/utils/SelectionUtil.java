package com.kyawhtut.ucstgovoting.utils;

import android.content.SharedPreferences;

public class SelectionUtil {

    public static SelectionUtil INSTANCE;

    private SharedPreferences sharedPreferences;

    public static final String KING_ID = "king id";
    public static final String QUEEN_ID = "queen id";
    public static final String ATTRACTIVE_BOY_ID = "attractive boy id";
    public static final String ATTRACTIVE_GIRL_ID = "attractive girl id";
    public static final String INNOCENCE_ID = "innocence id";
    public static final String INNOCENCE_BOY_ID = "innocence boy id";
    public static final String INNOCENCE_GIRL_ID = "innocence girl id";


    public static SelectionUtil create(SharedPreferences sh) {
        if (INSTANCE == null) {
            INSTANCE = new SelectionUtil(sh);
        }
        return INSTANCE;
    }

    private SelectionUtil(SharedPreferences sh) {
        this.sharedPreferences = sh;
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void setString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void clearValue() {
        sharedPreferences.edit().clear().apply();
    }
}
