package com.int0blivion.socketclient;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.common.base.Preconditions;

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

    public static boolean hitTest(@NonNull View view, @NonNull MotionEvent motionEvent) {
        Preconditions.checkNotNull(view, "view");
        Preconditions.checkNotNull(motionEvent, "motionEvent");
        final int[] viewPosition = new int[2];

        view.getLocationInWindow(viewPosition);

        return (motionEvent.getRawX() >= viewPosition[0]
                && motionEvent.getRawX() <= (viewPosition[0] + view.getWidth())
                && motionEvent.getRawY() >= viewPosition[1]
                && motionEvent.getRawY() <= (viewPosition[1] + view.getHeight()));
    }
}
