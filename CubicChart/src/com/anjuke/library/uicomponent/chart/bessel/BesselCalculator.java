
package com.anjuke.library.uicomponent.chart.bessel;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Paint;
import android.graphics.Rect;

import com.anjuke.library.uicomponent.chart.bessel.ChartData.Label;

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
    /** 光滑因子 */
    private float smoothness;

    public BesselCalculator(ChartData data, ChartStyle style) {
        this.data = data;
        this.style = style;
        this.translateX = 0f;
        this.smoothness = 0.33f;
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
        computeVertcalAxisInfo();// 计算纵轴参数
        computeHorizontalAxisInfo();// 计算横轴参数
        computeTitlesInfo();// 计算标题参数
        computeSeriesCoordinate();// 计算纵轴参数
        computeBesselPoints();// 计算贝塞尔结点
        computeGridPoints();// 计算网格顶点
    }

    /**
     * 平移画布
     * 
     * @param distanceX
     * @param velocityX
     */
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
            label.x = x;
            label.y = verticalTextRect.height() * (i + 1)
                    + style.getVerticalLabelTextPadding() * (i + 0.5f);
            label.drawingY = label.y + verticalTextRect.height() / 2;
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
            label.x = labelWidth * (i + 0.5f);
            label.y = height - horizontalTextRect.height() * 0.5f;
        }
    }

    /** 计算标题的坐标信息 */
    private void computeTitlesInfo() {
        paint.setTextSize(style.getHorizontalTitleTextSize());
        String titleText = data.getSeriesList().get(0).getTitle().text;
        paint.getTextBounds(titleText, 0, titleText.length(), horizontalTitleRect);
        xTitleHeight = horizontalTitleRect.height() * 2;
        List<Title> titles = data.getTitles();
        int count = titles.size();
        float stepX = (width - style.getHorizontalTitlePaddingLeft() - style.getHorizontalTitlePaddingRight()) / count;
        for (Title title : titles) {
            if (title instanceof Marker) {
                title.radius = 15;
            } else {
                title.radius = 10;
            }
            title.circleTextPadding = 10;
            title.updateTextRect(paint, stepX);
            title.textX = style.getHorizontalTitlePaddingLeft() + (titles.indexOf(title) + 0.5f) * stepX;
            title.textY = xTitleHeight * 0.75f;
            title.circleX = title.textX - title.textRect.width() / 2 - title.circleTextPadding - title.radius;
            title.circleY = title.textY - horizontalTitleRect.height() * 0.5f + 5;
        }
    }

    /** 计算序列的坐标信息 */
    private void computeSeriesCoordinate() {
        List<Label> yLabels = data.getYLabels();
        float minCoordinateY = yLabels.get(0).y;
        float maxCoordinateY = yLabels.get(yLabels.size() - 1).y;
        int length = 0;
        for (Series series : data.getSeriesList()) {
            if (series.getPoints().size() > length)
                length = series.getPoints().size();
        }
        for (Series series : data.getSeriesList()) {
            List<Point> points = series.getPoints();
            float pointWidth = xAxisWidth / points.size();
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                // 计算数据点的坐标
                point.x = pointWidth * (i + 0.5f);
                float ratio = (point.valueY - data.getMinValueY()) / (float) (data.getMaxValueY() - data.getMinValueY());
                point.y = maxCoordinateY - (maxCoordinateY - minCoordinateY) * ratio;
                Marker marker = data.getMarker();
                if (marker != null && marker.getPoint().valueX == point.valueX) {
                    Point markerPoint = marker.getPoint();
                    markerPoint.x = point.x;
                    ratio = (markerPoint.valueY - data.getMinValueY()) / (float) (data.getMaxValueY() - data.getMinValueY());
                    markerPoint.y = maxCoordinateY - (maxCoordinateY - minCoordinateY) * ratio;
                }
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

    /** 计算网格顶点 */
    private void computeGridPoints() {
        gridPoints = new Point[data.getMaxPointsCount()];
        List<Series> seriesList = data.getSeriesList();
        for (Series series : seriesList) {
            for (Point point : series.getPoints()) {
                int index = series.getPoints().indexOf(point);
                if (gridPoints[index] == null || gridPoints[index].valueY < point.valueY) {
                    gridPoints[index] = point;
                }
            }
        }
    }

    /** 计算贝塞尔结点 */
    private void computeBesselPoints() {
        for (Series series : data.getSeriesList()) {
            List<Point> besselPoints = series.getBesselPoints();
            List<Point> points = new ArrayList<Point>();
            for (Point point : series.getPoints()) {
                if (point.valueY > 0)
                    points.add(point);
            }
            int count = points.size();
            if (count < 2)
                continue;
            besselPoints.clear();
            for (int i = 0; i < count; i++) {
                if (i == 0 || i == count - 1) {
                    computeHorizontalPoints(i, points, besselPoints);
                } else {
                    Point p0 = points.get(i - 1);
                    Point p1 = points.get(i);
                    Point p2 = points.get(i + 1);
                    if ((p1.y - p0.y) * (p1.y - p2.y) >= 0) {// 极值点
                        computeHorizontalPoints(i, points, besselPoints);
                    } else {
                        computeVerticalPoints(i, points, besselPoints);
                    }
                }
            }
        }
    }

    /** 计算单调情况的贝塞尔结点 */
    private void computeHorizontalPoints(int i, List<Point> points, List<Point> besselPoints) {
        if (i == 0) {
            Point p1 = points.get(0);
            Point p2 = points.get(1);
            besselPoints.add(p1);
            besselPoints.add(new Point(p1.x + (p2.x - p1.x) * smoothness, p1.y));
        } else if (i == points.size() - 1) {
            Point p0 = points.get(i - 1);
            Point p1 = points.get(i);
            besselPoints.add(new Point(p1.x - (p1.x - p0.x) * smoothness, p1.y));
            besselPoints.add(p1);
        } else {
            Point p0 = points.get(i - 1);
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            besselPoints.add(new Point(p1.x - (p1.x - p0.x) * smoothness, p1.y));
            besselPoints.add(p1);
            besselPoints.add(new Point(p1.x + (p2.x - p1.x) * smoothness, p1.y));
        }
    }

    /**
     * 计算非单调情况的贝塞尔结点
     * 
     * @param i
     * @param points
     * @param besselPoints
     */
    private void computeVerticalPoints(int i, List<Point> points, List<Point> besselPoints) {
        Point p0 = points.get(i - 1);
        Point p1 = points.get(i);
        Point p2 = points.get(i + 1);
        float k = (p2.y - p0.y) / (p2.x - p0.x);
        float b = p1.y - k * p1.x;
        Point p01 = new Point();
        p01.x = p1.x - (p1.x - (p0.y - b) / k) * smoothness;
        p01.y = k * p01.x + b;
        besselPoints.add(p01);
        besselPoints.add(p1);
        Point p11 = new Point();
        p11.x = p1.x + (p2.x - p1.x) * smoothness;
        p11.y = k * p11.x + b;
        besselPoints.add(p11);
    }

    public void setSmoothness(float smoothness) {
        this.smoothness = smoothness;
    }
}
