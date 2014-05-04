
package com.anjuke.library.uicomponent.chart.curve;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * 标记，比如房源
 * 
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年4月23日
 */
public class Marker extends Title{
    private Point point;
    private Bitmap bitmap;
    private Rect rect;
    private int width;
    private int height;

    public Marker(int color, int valueX, int valueY, Bitmap bitmap, String text, int width, int height) {
        super(text, color);
        this.point = new Point(valueX, valueY, true);
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
        rect = new Rect();
    }

    public Point getPoint() {
        return point;
    }

    public Bitmap getBitmap() {
        return bitmap;
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
    
    public Rect updateRect(float coordinateX,float coordinateY,int width,int height) {
        rect.left = (int) (coordinateX - width / 2);
        rect.right = (int) (coordinateX + width / 2);
        rect.top = (int) (coordinateY - height / 2);
        rect.bottom = (int) (coordinateY + height / 2);
        return rect;
    }
}
