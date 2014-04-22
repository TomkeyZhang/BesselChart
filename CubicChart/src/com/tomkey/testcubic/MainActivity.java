package com.tomkey.testcubic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import com.anjuke.library.uicomponent.chart.curve.ChartData;
import com.anjuke.library.uicomponent.chart.curve.ChartData.LabelTransform;
import com.anjuke.library.uicomponent.chart.curve.CurveChart;
import com.anjuke.library.uicomponent.chart.curve.Point;
import com.anjuke.library.uicomponent.chart.curve.Series;

public class MainActivity extends Activity {
    CurveChart chart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart=(CurveChart)findViewById(R.id.chart);
        List<Series> seriess=new ArrayList<Series>();
        seriess.add(getRandomSeries("蓝高小区",Color.RED, false));
        seriess.add(getRandomSeries("塘桥",Color.GREEN, false));
//        seriess.add(getRandomSeries("浦东",Color.MAGENTA, false));
        ChartData data=new ChartData();
        data.setLabelTransform(new LabelTransform() {
            
            @Override
            public String verticalTransform(int valueY) {
                Log.d("zqt", "step valueY="+valueY);
                return String.format("%.1f万", valueY/10000f);
            }
            
            @Override
            public String horizontalTransform(int valueX) {
                return String.format("%s月", valueX);
            }
        });
        data.setSeriesList(seriess);
//        chart.getStyle().setGridColor(Color.parseColor("#66CCCCCC"));
        chart.setData(data);
//        chart.setVelocityX(1.2f);
//        chart.setSmoothness(0.5f);
    }
    private Series getRandomSeries(String title,int color,boolean willDrawing){
        
        List<Point> points=new ArrayList<Point>();
        Random random=new Random();
        if(willDrawing){
            for(int i=0;i<12;i++){
                points.add(new Point(i+1, 20000+1000*random.nextInt(10),true));
            } 
        }else{
            for(int i=0;i<36;i++){
                points.add(new Point(i+1, 20000+1000*random.nextInt(10),i%3==1));
            } 
        }
        return new Series(title,color, points);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
