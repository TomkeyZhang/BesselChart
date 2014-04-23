package com.tomkey.testcubic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.anjuke.library.uicomponent.chart.curve.ChartData;
import com.anjuke.library.uicomponent.chart.curve.ChartData.LabelTransform;
import com.anjuke.library.uicomponent.chart.curve.CurveChart;
import com.anjuke.library.uicomponent.chart.curve.Marker;
import com.anjuke.library.uicomponent.chart.curve.Point;
import com.anjuke.library.uicomponent.chart.curve.Series;

public class MainActivity extends Activity implements OnCheckedChangeListener {
    CurveChart chart;
    ToggleButton button;
    Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart=(CurveChart)findViewById(R.id.chart);
        button=(ToggleButton)findViewById(R.id.toggle_btn);
        button.setOnCheckedChangeListener(this);
       
//        chart.getStyle().setGridColor(Color.parseColor("#66CCCCCC"));
//        chart.setData(getChartData(true));
//        chart.setVelocityX(1.2f);
//        chart.setSmoothness(0.5f);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                chart.setData(getChartData(true));
            }
        }, 1000);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private ChartData getChartData(boolean willDrawing){
        List<Series> seriess=new ArrayList<Series>();
        seriess.add(getRandomSeries("浦东",Color.LTGRAY, willDrawing));
        seriess.add(getRandomSeries("陆家嘴",Color.GRAY, willDrawing));
        seriess.add(getRandomSeries("奥林匹克花园",Color.RED, willDrawing));
//        seriess.add(getRandomSeries("蓝高小区",Color.RED, false));
//        seriess.add(getRandomSeries("塘桥",Color.GREEN, false));
//        seriess.add(getRandomSeries("浦东",Color.MAGENTA, false));
        ChartData data=new ChartData();
        int position=0;
        if(willDrawing){
            position=12;
            data.setLabelTransform(new LabelTransform() {
                
                @Override
                public String verticalTransform(int valueY) {
                    return String.format("%.1fW", valueY/10000f);
                }
                
                @Override
                public String horizontalTransform(int valueX) {
                    return String.format("%s月", valueX);
                }
            });
        }else{
            position=36;
            data.setLabelTransform(new My36Transfer());
        }
        data.setMarker(new Marker(Color.GREEN, position, 23000, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), "该房源", 30, 30));
        data.setSeriesList(seriess);
        return data;
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        chart.setData(getChartData(!isChecked));
    }
    private class My36Transfer implements LabelTransform{
        private Calendar calendar=Calendar.getInstance();
        private SimpleDateFormat format=new SimpleDateFormat("yyyy.MM",Locale.CHINA);
        @Override
        public String verticalTransform(int valueY) {
            Log.d("zqt", "step valueY="+valueY);
            return String.format("%.1fW", valueY/10000f);
        }
        
        @Override
        public String horizontalTransform(int valueX) {
            calendar.set(Calendar.YEAR, 2011);
            calendar.set(Calendar.MONTH, valueX);
            return format.format(calendar.getTime());
        }
    }
}
