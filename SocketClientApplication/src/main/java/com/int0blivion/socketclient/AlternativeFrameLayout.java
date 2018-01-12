package com.int0blivion.socketclient;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Matt on 1/27/2016.
 */
public class AlternativeFrameLayout extends FrameLayout
{
    /**
     * AlternativeFrameLayout Constructor
     * @param context
     */
    public AlternativeFrameLayout(Context context)
    {
        super(context);
    }

    /**
     * AlternativeFrameLayout Constructor
     * @param context
     * @param attrs
     */
    public AlternativeFrameLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * AlternativeFrameLayout Constructor
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public AlternativeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Pass the intercepted touch event back to the activity to process.
     * Dont let the navigation drawer hog all of the touch events'
     *
     * @param e
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e)
    {
        ((Activity) getContext()).onTouchEvent(e);

        return super.onInterceptTouchEvent(e);
    }
}
