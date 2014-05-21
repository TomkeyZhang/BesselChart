
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

import com.anjuke.library.uicomponent.chart.bessel.BesselChart;
import com.anjuke.library.uicomponent.chart.bessel.BesselChart.ChartListener;
import com.anjuke.library.uicomponent.chart.bessel.ChartData.LabelTransform;
import com.anjuke.library.uicomponent.chart.bessel.Marker;
import com.anjuke.library.uicomponent.chart.bessel.Point;
import com.anjuke.library.uicomponent.chart.bessel.Series;

public class MainActivity extends Activity implements OnCheckedChangeListener, ChartListener {
    BesselChart chart;
    ToggleButton button;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart = (BesselChart) findViewById(R.id.chart);
        button = (ToggleButton) findViewById(R.id.toggle_btn);
        button.setOnCheckedChangeListener(this);
        // startActivity(new Intent(this, CubicActivity.class));
        // chart.getStyle().setGridColor(Color.parseColor("#66CCCCCC"));
        // chart.setData(getChartData(true));
        // chart.setVelocityX(1.2f);
        chart.setSmoothness(0.4f);
        chart.setChartListener(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                chart.setData(getChartData(true));
                getSeriesList(true);
                // chart.setDrawBesselPoint(true);
                chart.setSmoothness(0.33f);
            }
        }, 1000);
    }

    private Series getRandomSeries(String title, int color, boolean willDrawing) {
        List<Point> points = new ArrayList<Point>();
        Random random = new Random();
        if (willDrawing) {
            int[] data = new int[] {
                    25000, 24000, 22000, 23000, 27000, 25000, 29000, 28000, 26000, 25000, 22000, 23000
            };
            for (int i = 0; i < 12; i++) {
                if (i != 3)
                    points.add(new Point(i + 1, 20000 + 1000 * random.nextInt(10), true));
                else {
                    boolean tag = random.nextInt(10) > 5;
                    points.add(new Point(i + 1, tag ? 21000 : 0, tag));
                }
                // points.add(new Point(i + 1, data[i], true));
            }
        } else {
            for (int i = 0; i < 36; i++) {
                if (i % 3 == 2 && i < 20)
                    points.add(new Point(i + 1, 0, false));
                else
                    points.add(new Point(i + 1, 20000 + 1000 * random.nextInt(10), i % 3 == 1));
            }
        }
        for (Point point : points) {
            com.anjuke.library.uicomponent.chart.bessel.Log.d("getRandomSeries valueY=" + point.valueY);
        }
        return new Series(title, color, points);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void getSeriesList(boolean willDrawing) {
        List<Series> seriess = new ArrayList<Series>();
        seriess.add(getRandomSeries("浦东", Color.LTGRAY, willDrawing));
        seriess.add(getRandomSeries("陆家嘴", Color.GRAY, willDrawing));
        seriess.add(getRandomSeries("奥林匹克花园", Color.RED, willDrawing));
        // seriess.add(getRandomSeries("蓝高小区",Color.RED, false));
        // seriess.add(getRandomSeries("塘桥",Color.GREEN, false));
        // seriess.add(getRandomSeries("浦东",Color.MAGENTA, false));
        int position = 0;
        if (willDrawing) {
            position = 12;
            chart.getData().setLabelTransform(new LabelTransform() {

                @Override
                public String verticalTransform(int valueY) {
                    return String.format("%.1fW", valueY / 10000f);
                }

                @Override
                public String horizontalTransform(int valueX) {
                    return String.format("%s月", valueX);
                }

                @Override
                public boolean labelDrawing(int valueX) {
                    return true;
                }
            });
        } else {
            position = 36;
            chart.getData().setLabelTransform(new My36Transfer());
        }
        chart.getData().setSeriesList(seriess);
        chart.getData().setMarker(
                new Marker(Color.GREEN, position, 23000, BitmapFactory.decodeResource(getResources(), R.drawable.ajk_fj_benfangyuan), "该房源", 30, 30));
        chart.refresh(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        getSeriesList(!isChecked);
    }

    private class My36Transfer implements LabelTransform {
        private Calendar calendar = Calendar.getInstance();
        private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM", Locale.CHINA);

        @Override
        public String verticalTransform(int valueY) {
            Log.d("zqt", "step valueY=" + valueY);
            return String.format("%.1fW", valueY / 10000f);
        }

        @Override
        public String horizontalTransform(int valueX) {
            calendar.set(Calendar.YEAR, 2011);
            calendar.set(Calendar.MONTH, valueX);
            return format.format(calendar.getTime());
        }

        @Override
        public boolean labelDrawing(int valueX) {
            return valueX % 3 == 0;
        }
    }

    @Override
    public void onMove() {
        Log.d("zqt", "onMove");
    }
}
