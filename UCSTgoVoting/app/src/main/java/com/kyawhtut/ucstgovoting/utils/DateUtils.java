package com.kyawhtut.ucstgovoting.utils;

import android.text.TextUtils;
 
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
 
/**
 * Created by myolwin00 on 8/15/17.
 */
 
public class DateUtils {
 
    public static final String DF_TO_API = "yyyy-MM-dd HH:mm:ss";
    public static final String DF_FROM_API = "yyyy-MM-dd HH.mm.ss";
    public static final String DF_DISPLAY = "d MMM yyyy";
 
 
    private static String convert(String from, String to, String dateString) {
        DateFormat fromFormat = new SimpleDateFormat(from, Locale.getDefault());
        fromFormat.setLenient(false);
        DateFormat toFormat = new SimpleDateFormat(to, Locale.getDefault());
        toFormat.setLenient(false);
        Date date = null;
        try {
            date = fromFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
 
        return toFormat.format(date);
    }
 
    public static String getDisplayDateString(String date) {
        return TextUtils.isEmpty(date) ? "" : convert(DF_FROM_API, DF_DISPLAY, date);
    }
 
    public static String convertResToReq(String date) {
        return !TextUtils.isEmpty(date) ? convert(DF_FROM_API, DF_TO_API, date) : "";
    }
 
    public static String getCurrentTimeString() {
        return convertMilSecTo(DF_TO_API);
    }
 
    public static String convertMilSecTo(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat (pattern, Locale.getDefault());
        return formatter.format(System.currentTimeMillis());
    }
 
 
 
    private static long toMilliseconds(String dateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = formatter.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }
 
    public static boolean isDateWithinOneMonth(String date) {
        long dateMillisecond = toMilliseconds(date);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        long previousMonthMillisecond = c.getTimeInMillis();
        return dateMillisecond >= previousMonthMillisecond;
    }
 
    public static boolean isDateWithinOneWeek(String dateString) {
        long dateMillisecond = toMilliseconds(dateString);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7);
        long previousMonthMillisecond = c.getTimeInMillis();
        return dateMillisecond >= previousMonthMillisecond;
    }
 
}