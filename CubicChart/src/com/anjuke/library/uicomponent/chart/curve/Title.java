package com.anjuke.library.uicomponent.chart.curve;

public class Title {
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
	public Title(String text, int color) {
		this.text = text;
		this.color = color;
	}
	
    
}
