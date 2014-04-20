package com.anjuke.library.uicomponent.chart.curve;
/**
 * 结点
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年4月17日
 */
public class Point {
    /**是否在图形中绘制出此结点*/
    public boolean willDrawing;
    /** 在canvas中的X坐标 */
    public float coordinateX;
    /** 在canvas中的Y坐标 */
    public float coordinateY;
    /** 实际的X数值 */
    public int valueX;
    /** 实际的Y数值 */
    public int valueY;
    public Point() {
	}
    public Point(int valueX, int valueY,boolean willDrawing) {
        this.valueX = valueX;
        this.valueY = valueY;
        this.willDrawing=willDrawing;
    }
    
}
