
package com.anjuke.library.uicomponent.chart.curve;

import android.graphics.Color;

/**
 * 曲线图整体的样式
 * 
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年4月17日
 */
public class ChartStyle {
    /** 背景颜色 */
    private int backgroundColor;
    /** 网格线颜色 */
    private int gridColor;
    
    /** 横坐标文本大小 */
    private float horizontalLabelTextSize;
    /** 横坐标文本颜色 */
    private int horizontalLabelTextColor;
    /** 横坐标标题文本大小 */
    private float horizontalTitleTextSize;
    /** 横坐标标题文本颜色 */
    private int horizontalTitleTextColor;
    
    /** 纵坐标文本大小 */
    private float verticalLabelTextSize;
    /** 纵坐标文本间距 */
    private int verticalLabelTextPadding;
    /** 纵坐标文本颜色 */
    private int verticalLabelTextColor;

    public ChartStyle() {
        backgroundColor=Color.WHITE;
        gridColor=Color.LTGRAY;
        horizontalTitleTextSize=40;
        horizontalTitleTextColor=Color.GRAY;
    	horizontalLabelTextSize=33;
    	horizontalLabelTextColor=Color.GRAY;
        verticalLabelTextSize = 38;
        verticalLabelTextPadding = 60;
        verticalLabelTextColor = Color.GRAY;
    }

    public float getVerticalLabelTextSize() {
        return verticalLabelTextSize;
    }

    public void setVerticalLabelTextSize(float verticalLabelTextSize) {
        this.verticalLabelTextSize = verticalLabelTextSize;
    }

    public int getVerticalLabelTextPadding() {
        return verticalLabelTextPadding;
    }

    public int getVerticalLabelTextColor() {
        return verticalLabelTextColor;
    }

    public void setVerticalLabelTextPadding(int verticalLabelTextPadding) {
        this.verticalLabelTextPadding = verticalLabelTextPadding;
    }

    public void setVerticalLabelTextColor(int verticalLabelTextColor) {
        this.verticalLabelTextColor = verticalLabelTextColor;
    }

	public float getHorizontalLabelTextSize() {
		return horizontalLabelTextSize;
	}

	public void setHorizontalLabelTextSize(float horizontalLabelTextSize) {
		this.horizontalLabelTextSize = horizontalLabelTextSize;
	}

	public int getHorizontalLabelTextColor() {
		return horizontalLabelTextColor;
	}

	public void setHorizontalLabelTextColor(int horizontalLabelTextColor) {
		this.horizontalLabelTextColor = horizontalLabelTextColor;
	}

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getGridColor() {
        return gridColor;
    }

    public void setGridColor(int gridColor) {
        this.gridColor = gridColor;
    }

	public float getHorizontalTitleTextSize() {
		return horizontalTitleTextSize;
	}

	public void setHorizontalTitleTextSize(float horizontalTitleTextSize) {
		this.horizontalTitleTextSize = horizontalTitleTextSize;
	}

	public int getHorizontalTitleTextColor() {
		return horizontalTitleTextColor;
	}

	public void setHorizontalTitleTextColor(int horizontalTitleTextColor) {
		this.horizontalTitleTextColor = horizontalTitleTextColor;
	}

}
