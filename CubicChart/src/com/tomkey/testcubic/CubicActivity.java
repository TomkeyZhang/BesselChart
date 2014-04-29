package com.tomkey.testcubic;

import android.app.Activity;
import android.os.Bundle;

public class CubicActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CubicView(this));
    }
}
