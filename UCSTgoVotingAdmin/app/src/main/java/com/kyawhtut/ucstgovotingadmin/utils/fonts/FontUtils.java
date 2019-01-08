package com.kyawhtut.ucstgovotingadmin.utils.fonts;

import android.content.Context;

public class FontUtils {

    public static String getConvertedString(String string) {
        return (MMFontUtils.isSupportUnicode()) ? string : MMFontUtils.uni2zg(string);
    }

    public static String getConvertedString(Context ctx, int resource) {
        return getConvertedString(ctx.getResources().getString(resource));
    }
}
