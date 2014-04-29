package com.tomkey.testcubic;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class Group2 extends LinearLayout {

    public Group2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Group2(Context context) {
        super(context);
    }
    private String TAB = "zqt_Group2";
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch(ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.d(TAB, "dispatchTouchEvent action:ACTION_DOWN");
            break;
        case MotionEvent.ACTION_UP:
            Log.d(TAB, "dispatchTouchEvent action:ACTION_UP");
            break;
        }
        return super.dispatchTouchEvent(ev);
    }
 
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //true表示拦截后交给自己的onTouchEvent处理，false表示传递给子View
        switch(ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.d(TAB, "onInterceptTouchEvent action:ACTION_DOWN");
            break;
        case MotionEvent.ACTION_UP:
            Log.d(TAB, "onInterceptTouchEvent action:ACTION_UP");
            break;
        }
        return super.onInterceptTouchEvent(ev);
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Log.d(TAB, "---onTouchEvent action:ACTION_DOWN");
            break;
        case MotionEvent.ACTION_UP:
            Log.d(TAB, "---onTouchEvent action:ACTION_UP");
            break;
        }
        return super.onTouchEvent(event);
    }
}
