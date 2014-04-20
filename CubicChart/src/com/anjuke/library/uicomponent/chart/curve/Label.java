package com.anjuke.library.uicomponent.chart.curve;
/**
 * 标签
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年4月18日
 */
public class Label {
    /**文本对应的坐标X*/
    public float coordinateX;
    /**文本对应的坐标Y*/
    public float coordinateY;
    /**文本对应的实际数值*/
    public int value;
    /**文本*/
    public String text;
    public Label(int value, String text) {
        this.value = value;
        this.text = text;
    }
    
}
