
package com.anjuke.library.uicomponent.chart.curve;

import java.util.List;

import android.graphics.Rect;

public class Series {
    /** 序列曲线的标题 */
    private Title title;
    /** 序列曲线的颜色 */
    private int color;
    /** 序列点集合 */
    private List<Point> points;

    /**
     * @param color 曲线的颜色
     * @param points 点集合
     */
    public Series(String title,int color, List<Point> points) {
        this.title=new Title(title, color);
    	this.color = color;
        this.points = points;
    }
    public Title getTitle() {
		return title;
	}
    public int getColor() {
        return color;
    }

    public List<Point> getPoints() {
        return points;
    }
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
        /**文本区域*/
        public Rect textRect=new Rect();
        public Title(String text, int color) {
            this.text = text;
            this.color = color;
        }
        
        
    }

}
