
package com.anjuke.library.uicomponent.chart.curve;

import java.util.List;

import android.graphics.Paint;
import android.graphics.Rect;

import com.anjuke.library.uicomponent.chart.curve.ChartData.Label;

class BesselCalculator {
    /** 纵坐标文本矩形 */
    public Rect verticalTextRect;
    /** 横坐标文本矩形 */
    public Rect horizontalTextRect;
    /** 横坐标标题文本矩形 */
    public Rect horizontalTitleRect;
    /** 图形的高度 */
    public int height;
    /** 图形的宽度 */
    public int width;
    /** 纵轴的宽度 */
    public int yAxisWidth;
    /** 纵轴的高度 */
    public int yAxisHeight;
    /** 横轴的高度 */
    public int xAxisHeight;
    /** 横轴的标题的高度 */
    public int xTitleHeight;
    /** 横轴的长度 */
    public float xAxisWidth;
    /** 灰色竖线顶点 */
    public Point[] gridPoints;
    /** 画布X轴的平移，用于实现曲线图的滚动效果 */
    private float translateX;

    /** 用于测量文本区域长宽的画笔 */
    private Paint paint;

    private ChartStyle style;
    private ChartData data;

    public BesselCalculator(ChartData data, ChartStyle style) {
        this.data = data;
        this.style = style;
        this.translateX = 0f;
        this.paint = new Paint();
        this.verticalTextRect = new Rect();
        this.horizontalTextRect = new Rect();
        this.horizontalTitleRect = new Rect();
    }

    /**
     * 计算图形绘制的参数信息
     * 
     * @param width 曲线图区域的宽度
     */
    public void compute(int width) {
        this.width = width;
        this.translateX = 0;
        // 计算横轴的时候需使用纵轴的高度计算纵坐标，故先计算纵轴，再计算横轴
        computeVertcalAxisInfo();
        computeHorizontalAxisInfo();
        computeTitlesInfo();
        computeSeriesCoordinate();
    }

    public void move(float distanceX, float velocityX) {
        translateX = translateX
                - distanceX * velocityX;// 计算画布平移距离
    }

    public float getTranslateX() {
        return translateX;
    }

    /***
     * 确保画布的移动不会超出范围
     * 
     * @return true,超出范围；false，未超出范围
     */
    public boolean ensureTranslation() {
        if (translateX >= 0) {
            translateX = 0;
            return true;
        } else if (translateX < 0) {
            if (yAxisWidth != 0 && translateX < -xAxisWidth / 2) {
                translateX = -xAxisWidth / 2;
                return true;
            }
        }
        return false;
    }

    /** 计算纵轴参数 */
    private void computeVertcalAxisInfo() {
        paint.setTextSize(style.getVerticalLabelTextSize());
        List<Label> yLabels = data.getYLabels();
        int yLabelCount = data.getYLabels().size();
        String maxText = getMaxText(yLabels);
        paint.getTextBounds(maxText, 0, maxText.length(), verticalTextRect);
        float x = verticalTextRect.width() * (0.5f + style.getVerticalLabelTextPaddingRate());
        for (int i = 0; i < yLabelCount; i++) {
            Label label = yLabels.get(i);
            label.coordinateX = x;
            label.coordinateY = verticalTextRect.height() * (i + 1)
                    + style.getVerticalLabelTextPadding() * (i + 0.5f);
        }
        yAxisWidth = (int) (verticalTextRect.width() * (1 + style.getVerticalLabelTextPaddingRate() * 2));
        yAxisHeight = verticalTextRect.height() * yLabelCount
                + style.getVerticalLabelTextPadding() * yLabelCount;
    }

    /** 计算横轴参数 */
    private void computeHorizontalAxisInfo() {
        xAxisWidth = 2 * (width - yAxisWidth);
        paint.setTextSize(style.getHorizontalLabelTextSize());
        List<Label> xLabels = data.getXLabels();
        String measureText = "张";
        paint.getTextBounds(measureText, 0, measureText.length(),
                horizontalTextRect);
        xAxisHeight = horizontalTextRect.height() * 2;
        height = (int) (yAxisHeight + xAxisHeight);// 图形的高度计算完毕
        float labelWidth = xAxisWidth / xLabels.size();
        for (int i = 0; i < xLabels.size(); i++) {
            Label label = xLabels.get(i);
            label.coordinateX = labelWidth * (i + 0.5f);
            label.coordinateY = height - horizontalTextRect.height() * 0.5f;
        }
        paint.setTextSize(style.getHorizontalTitleTextSize());
        String titleText = data.getSeriesList().get(0).getTitle().text;
        paint.getTextBounds(titleText, 0, titleText.length(), horizontalTitleRect);
        xTitleHeight = horizontalTitleRect.height() * 2;
        height = height + xTitleHeight;
    }

    /** 计算标题的坐标信息 */
    private void computeTitlesInfo() {
        List<Title> titles = data.getTitles();
        int count = titles.size();
        float stepX = width / count;
        float x = width;
        for (Title title : titles) {
            if (title instanceof Marker) {
                title.radius = 15;
            } else {
                title.radius = 10;
            }
            title.circleTextPadding = 20;
            title.updateTextRect(paint, stepX);
            title.textCoordinateX = 30 + x - (titles.indexOf(title) + (data.getMarker() != null ? 1.5f : 0.5f)) * stepX;
            title.textCoordinateY = height - 10;
            title.circleCoordinateX = title.textCoordinateX - title.textRect.width() / 2 - title.circleTextPadding + 5;
            title.circleCoordinateY = title.textCoordinateY - horizontalTitleRect.height() * 0.5f + 5;
        }
    }

    /** 计算序列的坐标信息 */
    private void computeSeriesCoordinate() {
        List<Label> yLabels = data.getYLabels();
        float minCoordinateY = yLabels.get(0).coordinateY;
        float maxCoordinateY = yLabels.get(yLabels.size() - 1).coordinateY;
        int length = 0;
        for (Series series : data.getSeriesList()) {
            if (series.getPoints().size() > length)
                length = series.getPoints().size();
        }
        gridPoints = new Point[length];
        for (Series series : data.getSeriesList()) {
            List<Point> points = series.getPoints();
            float pointWidth = xAxisWidth / points.size();
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                point.fixedCoordinateY = 0;
                // 计算数据点的坐标
                point.coordinateX = pointWidth * (i + 0.5f);
                float ratio = (point.valueY - data.getMinValueY()) / (float) (data.getMaxValueY() - data.getMinValueY());

                point.coordinateY = maxCoordinateY - (maxCoordinateY - minCoordinateY) * ratio;
                Marker marker = data.getMarker();
                if (marker != null && marker.getPoint().valueX == point.valueX) {
                    Point markerPoint = marker.getPoint();
                    markerPoint.coordinateX = point.coordinateX;
                    ratio = (markerPoint.valueY - data.getMinValueY()) / (float) (data.getMaxValueY() - data.getMinValueY());
                    markerPoint.coordinateY = maxCoordinateY - (maxCoordinateY - minCoordinateY) * ratio;
                }
                // Log.d("zqt", "ratio=" + ratio + " point.coordinateY=" + point.coordinateY);
                // 计算竖直线的顶点
                if (gridPoints[i] == null || gridPoints[i].valueY < point.valueY) {
                    gridPoints[i] = point;
                }
//                Log.d("point.coordinateX:" + point.coordinateX + " point.coordinateY:" +
//                        point.coordinateY);
            }
        }
    }

    /**
     * 获取label中最长的文本
     * 
     * @param labels
     * @return
     */
    private String getMaxText(List<Label> labels) {
        String maxText = "";
        for (Label label : labels) {
            if (label.text.length() > maxText.length())
                maxText = label.text;
        }
        return maxText;
    }

}
