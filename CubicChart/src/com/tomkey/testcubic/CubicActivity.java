
package com.tomkey.testcubic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class CubicActivity extends Activity {
    private String TAB = "zqt_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         setContentView(new CubicView(this));
        setContentView(R.layout.activity_touch);
//        setContentView(new Group2(this));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
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
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
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
