package com.anjuke.library.uicomponent.chart.curve;

import java.util.ArrayList;
import java.util.List;

/**
 * 曲线图的数据以及相关配置信息
 * 
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年4月17日
 */
public class ChartData {
	private List<Series> seriesList;
	private List<Label> xLabels;
	private List<Label> yLabels;
	private int maxValueY;
	private int minValueY;
	private LabelTransform labelTransform;
	/** 纵坐标显示文本的数量 */
	private int yLabelCount;

	/** 使用哪一个series的横坐标来显示横坐标文本 */
	private int xLabelUsageSeries;
	
	public ChartData() {
		xLabels = new ArrayList<Label>();
		yLabels = new ArrayList<Label>();
		seriesList = new ArrayList<Series>();
		labelTransform=new LabelTransform() {
			@Override
			public String verticalTransform(int valueY) {
				return String.valueOf(valueY);
			}

			@Override
			public String horizontalTransform(int valueX) {
				return String.valueOf(valueX);
			}
		};
		yLabelCount=4;//默认纵轴显示4个文本
		xLabelUsageSeries=0;//默认横轴使用第一个序列来显示文本
	}

	/** 设置数据序列 */
	public void setSeriesList(List<Series> seriesList) {
		this.seriesList.clear();
		if (seriesList != null && seriesList.size() > 0) {
			this.seriesList.addAll(seriesList);
			if (this.seriesList.size() <= xLabelUsageSeries)
				throw new IllegalArgumentException("xLabelUsageSeries should greater than seriesList.size()");
			resetXLabels();
			resetYLabels();
		}
	}

	/** 重新生成X坐标轴文本 */
	private void resetXLabels() {
		xLabels.clear();
		for (Point point : seriesList.get(xLabelUsageSeries).getPoints()) {
			if (point.willDrawing)
				xLabels.add(new Label(point.valueX, labelTransform
						.horizontalTransform(point.valueX)));
		}
	}

	/** 重新生成Y坐标轴文本 */
	private void resetYLabels() {
		maxValueY = 0;
		minValueY = Integer.MAX_VALUE;
		for (Series series : seriesList) {
			for (Point point : series.getPoints()) {
				if (point.valueY > maxValueY)
					maxValueY = point.valueY;
				if (point.valueY < minValueY)
					minValueY = point.valueY;
			}
		}
		int step = (maxValueY - minValueY) / (yLabelCount - 1);
		yLabels.clear();
		for (int value = minValueY; value <= maxValueY; value = value + step) {
			yLabels.add(0,
					new Label(value, labelTransform.verticalTransform(value)));
		}
	}

	public void setLabelTransform(LabelTransform labelTransform) {
		this.labelTransform = labelTransform;
	}

	public List<Series> getSeriesList() {
		return seriesList;
	}

	public LabelTransform getLabelTransform() {
		return labelTransform;
	}

	public List<Label> getXLabels() {
		return xLabels;
	}

	public List<Label> getYLabels() {
		return yLabels;
	}
	
	public int getMaxValueY() {
		return maxValueY;
	}
	public int getMinValueY() {
		return minValueY;
	}
	public int getyLabelCount() {
		return yLabelCount;
	}

	public void setyLabelCount(int yLabelCount) {
		this.yLabelCount = yLabelCount;
	}

	public int getxLabelUsageSeries() {
		return xLabelUsageSeries;
	}

	public void setxLabelUsageSeries(int xLabelUsageSeries) {
		this.xLabelUsageSeries = xLabelUsageSeries;
	}

	public interface LabelTransform {
		/** 纵坐标显示的文本 */
		String verticalTransform(int valueY);

		/** 横坐标显示的文本 */
		String horizontalTransform(int valueX);

	}
}
