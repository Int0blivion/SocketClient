package com.int0blivion.socketclient;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Matt on 1/23/2016.
 */
public class Util {
    public static String formatErrorText(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentTime = sdf.format(new Date());

        return "(" + currentTime + ") " + message;
    }

    public static void hideSoftKeyboard(Activity activity, View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static boolean hitTest(View v, MotionEvent e) {
        final int[] viewPosition = new int[2];

        v.getLocationInWindow(viewPosition);

        return e.getRawX() >= viewPosition[0] && e.getRawX() <= viewPosition[0] + v.getWidth() && e.getRawY() >= viewPosition[1] && e.getRawY() <= viewPosition[1] + v.getHeight();
    }
}
