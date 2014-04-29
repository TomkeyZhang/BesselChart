package com.tomkey.testcubic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.View;

public class CubicView extends View {
    Path path=new Path();
    Paint paint=new Paint();
    Point[] points=new Point[]{new Point(0, 100),new Point(100, 150),new Point(200, 400),new Point(300, 50),new Point(400, 200),new Point(500, 600),new Point(600, 300),new Point(700, 500),new Point(800, 480),new Point(900, 680)};
    float ratio=0.33f;
    List<Point> list=new ArrayList<CubicView.Point>();
    public CubicView(Context context) {
        super(context);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setStyle(Style.STROKE);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(points.length==0)
            return;
        list.clear();
        for(int i=0;i<points.length;i++){
            if(i==0||i==points.length-1){
                setHorizontalPoints(i);
            }else{
                Point p0=points[i-1];
                Point p1=points[i];
                Point p2=points[i+1];
                if((p1.y-p0.y)*(p1.y-p2.y)>0){//极值点
                    setHorizontalPoints(i);
                }else{
                    setVerticalPoints(i);
                }
            }
        }
        path.reset();
        for(int i=0;i<list.size();i=i+3){
            if(i==0){
                path.moveTo(list.get(i).x, list.get(i).y);  
            }else{
               path.cubicTo(list.get(i-2).x, list.get(i-2).y, list.get(i-1).x, list.get(i-1).y, list.get(i).x, list.get(i).y); 
            }
        }
        canvas.drawPath(path, paint);
    }
    private void cubicPath(int i){
        Point p0=points[i];
        Point p3=points[i+1];
        Point p1=new Point(p0.x+(p3.x-p0.x)*ratio, p0.y);
        Point p2=new Point(p3.x-(p3.x-p0.x)*ratio, p3.y);
        path.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
    }
    private void setHorizontalPoints(int i){
        if(i==0){
            Point p1=points[0];
            Point p2=points[1];
            list.add(p1);
            list.add(new Point(p1.x+(p2.x-p1.x)*ratio, p1.y));
        }else if(i==points.length-1){
            Point p0=points[i-1];
            Point p1=points[i];
            list.add(new Point(p1.x-(p1.x-p0.x)*ratio, p1.y));
            list.add(p1);
        }else{
            Point p0=points[i-1];
            Point p1=points[i];
            Point p2=points[i+1];
            list.add(new Point(p1.x-(p1.x-p0.x)*ratio, p1.y));
            list.add(p1);
            list.add(new Point(p1.x+(p2.x-p1.x)*ratio, p1.y));
        }
    }
    private void setVerticalPoints(int i){
        Point p0=points[i-1];
        Point p1=points[i];
        Point p2=points[i+1];
        float k=(p2.x-p1.x)/(p1.x-p0.x);//为了支持x轴间距不均匀的情况
        list.add(new Point(p1.x-(p1.x-p0.x)*ratio, p1.y-(p1.y-p0.y)*ratio));
        list.add(p1);
        list.add(new Point(p1.x+(p1.x-p0.x)*ratio*k, p1.y+(p1.y-p0.y)*ratio*k));
    }
    private void compute(){
        
    }
    private void quadPath(int i){
        Point p0=points[i];
        Point p3=points[i+1];
        Point p1=new Point(p0.x+(p3.x-p0.x)*ratio, p0.y);
        Point p2=new Point(p3.x-(p3.x-p0.x)*ratio, p3.y);
        path.cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
    }
    class Point{
        float x;
        float y;
        public Point() {
        }
        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
    }
}
