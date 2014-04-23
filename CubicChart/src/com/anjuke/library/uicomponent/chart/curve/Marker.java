package com.anjuke.library.uicomponent.chart.curve;

import android.graphics.Bitmap;
import android.graphics.Rect;
/**
 * 标记，比如房源
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年4月23日
 */
public class Marker {
    private int color;
    private Point point;
    private Bitmap bitmap;
    private String text;
    private Rect rect;
    private int width;
    private int height;
    private Point titlePoint;
    
    
    public Marker(int color, int valueX,int valueY, Bitmap bitmap, String text, int width, int height) {
        this.color = color;
        this.point = new Point(valueX, valueY, true);
        this.bitmap = bitmap;
        this.text = text;
        this.width = width;
        this.height = height;
        rect=new Rect();
    }
    public int getColor() {
        return color;
    }
    public Point getPoint() {
        return point;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    public String getText() {
        return text;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public Rect getRect() {
        return rect;
    }
    public Rect updateRect(Point point){
        rect.left=(int)(point.coordinateX-width/2);
        rect.right=(int)(point.coordinateX+width/2);
        rect.top=(int)(point.coordinateY-height/2);
        rect.bottom=(int)(point.coordinateY+height/2);
        return rect;
    }
}
