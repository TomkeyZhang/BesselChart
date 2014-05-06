
package com.anjuke.library.uicomponent.chart.bessel;

import android.graphics.Color;

/**
 * 曲线图整体的样式
 * 
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年4月17日
 */
public class ChartStyle {
    /** 网格线颜色 */
    private int gridColor;
    /** 坐标轴分隔线宽度 */
    private int axisLineWidth;
    
    /** 横坐标文本大小 */
    private float horizontalLabelTextSize;
    /** 横坐标文本颜色 */
    private int horizontalLabelTextColor;
    /** 横坐标标题文本大小 */
    private float horizontalTitleTextSize;
    /** 横坐标标题文本颜色 */
    private int horizontalTitleTextColor;
    /** 横坐标标题文本左间距 */
    private int horizontalTitlePaddingLeft;
    /** 横坐标标题文本右间距 */
    private int horizontalTitlePaddingRight;
    
    /** 纵坐标文本大小 */
    private float verticalLabelTextSize;
    /** 纵坐标文本上下间距 */
    private int verticalLabelTextPadding;
    /** 纵坐标文本左右间距相对文本的比例 */
    private float verticalLabelTextPaddingRate;
    /** 纵坐标文本颜色 */
    private int verticalLabelTextColor;

    public ChartStyle() {
        gridColor=Color.LTGRAY;
        horizontalTitleTextSize=34;
        horizontalTitleTextColor=Color.GRAY;
    	horizontalLabelTextSize=30;
    	horizontalLabelTextColor=Color.GRAY;
        verticalLabelTextSize = 34;
        verticalLabelTextPadding = 60;
        verticalLabelTextColor = Color.GRAY;
        verticalLabelTextPaddingRate=0.2f;
        axisLineWidth=2;
        horizontalTitlePaddingLeft = 20;
        horizontalTitlePaddingRight = 10;
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

    public float getVerticalLabelTextPaddingRate() {
        return verticalLabelTextPaddingRate;
    }

    public void setVerticalLabelTextPaddingRate(float verticalLabelTextPaddingRate) {
        this.verticalLabelTextPaddingRate = verticalLabelTextPaddingRate;
    }
    public int getAxisLineWidth() {
        return axisLineWidth;
    }
    public void setAxisLineWidth(int axisLineWidth) {
        this.axisLineWidth = axisLineWidth;
    }

    public int getHorizontalTitlePaddingLeft() {
        return horizontalTitlePaddingLeft;
    }

    public int getHorizontalTitlePaddingRight() {
        return horizontalTitlePaddingRight;
    }

    public void setHorizontalTitlePaddingLeft(int horizontalTitlePaddingLeft) {
        this.horizontalTitlePaddingLeft = horizontalTitlePaddingLeft;
    }

    public void setHorizontalTitlePaddingRight(int horizontalTitlePaddingRight) {
        this.horizontalTitlePaddingRight = horizontalTitlePaddingRight;
    }

}
