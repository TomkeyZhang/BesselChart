package com.anjuke.library.uicomponent.chart.curve;

import android.graphics.Paint;
import android.graphics.Rect;
/**
 * 内部使用
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年4月23日
 */
class Title {
    /**文本对应的坐标X*/
    public float textCoordinateX;
    /**文本对应的坐标Y*/
    public float textCoordinateY;
    /**文本*/
    public String text;
    /**圆点对应的坐标X*/
    public float circleCoordinateX;
    /**圆点对应的坐标Y*/
    public float circleCoordinateY;
    /**圆点的颜色*/
    public int color;
    /**圆点的半径*/
    public int radius;
    /**图形标注与文本的间距*/
    public int circleTextPadding;
    /**文本区域*/
    public Rect textRect=new Rect();
    public Title(String text, int color) {
        this.text = text;
        this.color = color;
    }
    public void updateTextRect(Paint paint,float maxWidth){
        int textWidth=textCircleWidth(paint);
        if(textWidth<=maxWidth)
            return;
        while(textWidth>maxWidth){
            text=text.substring(0, text.length()-1);
            textWidth=textCircleWidth(paint);
        }
        text=text.substring(0, text.length()-1)+"...";
    }
    private int textCircleWidth(Paint paint){
        paint.getTextBounds(text, 0, text.length(), textRect);
        return textRect.width()+(radius+circleTextPadding)*2;
    }
}
