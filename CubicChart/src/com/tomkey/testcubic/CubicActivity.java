
package com.tomkey.testcubic;

import android.app.Activity;
import android.os.Bundle;

public class CubicActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(new CubicView(this));
//        setContentView(R.layout.activity_touch);
//        setContentView(new Group2(this));
    }
    
}
