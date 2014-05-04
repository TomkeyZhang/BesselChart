
package com.anjuke.library.uicomponent.chart.curve;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.anjuke.library.uicomponent.chart.curve.ChartData.Label;

/**
 * 曲线图
 * 
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年4月23日
 */
public class CurveChart extends View {
    /** 通用画笔 */
    private Paint paint;
    /** 曲线的路径，用于绘制曲线 */
    private Path curvePath;
    /** 曲线的路径路径测量器 */
    private PathMeasure curvePathMeasure;
    /** 曲线图绘制的参数信息 */
    private DrawingInfo info;
    /** 曲线图的样式 */
    private ChartStyle style;
    /** 曲线图的数据 */
    private ChartData data;

    /** 滚动的速度 */
    private float velocityX;

    /** 偏移因子 */
    private float firstMultiplier;
    private float secondMultiplier;
    /** 动画线程 */
    private AnimateThread animateThread;
    private GestureDetector detector;
    private boolean repaint;

    public CurveChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CurveChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurveChart(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        curvePath = new Path();
        curvePathMeasure = new PathMeasure(curvePath, false);
        info = new DrawingInfo();
        velocityX = 1.2f;
        // 默认偏移第一个点33%的距离,
        firstMultiplier = 0.33f;
        // 默认偏移第二个点67%的距离.
        secondMultiplier = 1 - firstMultiplier;
        repaint=true;
        style = new ChartStyle();
        data = new ChartData();
        detector=new GestureDetector(getContext(), new GestureDetector.OnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(Math.abs(distanceX/distanceY)>1){
                    getParent().requestDisallowInterceptTouchEvent(true);
                    info.translateX = info.translateX
                            - distanceX * velocityX;// 计算画布平移距离
//                    repaint=true;
                    invalidate();
                }
//                Log.d("zqt", "onScroll "+distanceX);
                return false;
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                Log.d("zqt", "onFling "+velocityX);
                return false;
            }
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
//                Log.d("zqt", "onSingleTapUp "+e);
                return false;
            }
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
            @Override
            public void onShowPress(MotionEvent e) {
            }
            @Override
            public void onLongPress(MotionEvent e) {
            }
        });
    }
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.d("zqt", "dispatchTouchEvent");
//        return detector.onTouchEvent(event);
//    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
//        return detector.onTouchEvent(event);
//        if (event.getAction() == MotionEvent.ACTION_MOVE
//                && info.lastTouchEventX >= 0) {
//            
//        }
//        info.lastTouchEventX = event.getX();
        return event.getAction() == MotionEvent.ACTION_DOWN;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return super.dispatchTouchEvent(event);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
//        Log.d("zqt", "CurveChart onDraw"+repaint);
        if (data.getSeriesList().size() == 0)
            return;
        canvas.drawColor(style.getBackgroundColor());
        translateCanvas(canvas);
     // 计算横轴的时候需使用纵轴的高度计算纵坐标，故先计算纵轴，再计算横轴
        info.computeVertcalAxisInfo();
        info.computeHorizontalAxisInfo();
        if(repaint){
            info.computeSeriesCoordinate();
            setHeight(info.height);
        }
        drawCurveAndPoints(canvas);
        drawGrid(canvas);
        drawMarker(canvas);
        drawHorLabels(canvas);
        drawVerLabels(canvas);
        drawSeriesTitle(canvas);
        startAnimateIfNeed();
        repaint=false;
    }
    /**绘制曲线图中的房源*/
    private void drawMarker(Canvas canvas) {
        Marker marker = data.getMarker();
        if (marker != null) {
            paint.setAlpha(255);
            canvas.drawBitmap(marker.getBitmap(), null, marker.updateRect(marker.getPoint().coordinateX, marker.getPoint().coordinateY,marker.getWidth(),marker.getHeight()), paint);
        }
    }
    /**绘制序列标题*/
    private void drawSeriesTitle(Canvas canvas) {
        List<Series> seriess = data.getSeriesList();
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(style.getHorizontalTitleTextSize());
        for (int i = 0; i < seriess.size(); i++) {
            paint.setColor(seriess.get(i).getColor());
            Title title = seriess.get(i).getTitle();
            canvas.drawCircle(title.circleCoordinateX, title.circleCoordinateY + 5, title.radius, paint);
            paint.setAlpha(255);
            canvas.drawText(title.text, title.textCoordinateX, title.textCoordinateY, paint);
        }
        Marker marker = data.getMarker();
        if (marker != null) {
            paint.setAlpha(255);
            Title title = marker;
            canvas.drawBitmap(marker.getBitmap(), null, marker.updateRect(title.circleCoordinateX, title.circleCoordinateY + 5,title.radius*2,title.radius*2), paint);
            paint.setTextAlign(Align.CENTER);
            paint.setTextSize(style.getHorizontalTitleTextSize());
            paint.setColor(title.color);
            canvas.drawText(title.text, title.textCoordinateX, title.textCoordinateY, paint);
        }
    }

    /** 在需要的时候启动动画 */
    private void startAnimateIfNeed() {
        if (animateThread == null) {
            animateThread = new AnimateThread();
            animateThread.start();
        }
    }

    /** 绘制网格线 */
    private void drawGrid(Canvas canvas) {
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(style.getGridColor());
        paint.setAlpha(80);
        List<Label> yLabels = data.getYLabels();
        float coordinateY = yLabels.get(0).coordinateY;
        canvas.drawLine(0, coordinateY, info.xAxisWidth, coordinateY, paint);
        coordinateY = yLabels.get(yLabels.size() - 1).coordinateY;
        canvas.drawLine(0, coordinateY, info.xAxisWidth, coordinateY, paint);
        // for(Label label:yLabels){
        // canvas.drawLine(0, label.coordinateY, info.xAxisWidth, label.coordinateY, paint);
        // }
        for (Point point : info.gridPoints) {
            if (point.willDrawing&&point.valueY>0)
                canvas.drawLine(point.coordinateX, point.fixedCoordinateY, point.coordinateX, info.yAxisHeight, paint);
        }
    }

    /** 绘制曲线和结点 */
    private void drawCurveAndPoints(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        for (Series series : data.getSeriesList()) {
            paint.setColor(series.getColor());
            List<Point> points = series.getPoints();
            for(Point point:points){
                point.fixedCoordinateY=0;
            }
            drawCurvePath(canvas, points);
            drawPoints(canvas, points);
        }
    }

    /** 绘制曲线 */
    private void drawCurvePath(Canvas canvas, List<Point> points) {
        curvePath.reset();
        List<Point> drawPoints=new ArrayList<Point>();
        for(Point point:points){
            if(point.valueY>0)
                drawPoints.add(point);
        }
        if(drawPoints.size()==0)
            return;
        curvePath.moveTo(drawPoints.get(0).coordinateX, drawPoints.get(0).coordinateY);
        int length = drawPoints.size();
        for (int i = 0; i < length; i++) {
            int nextIndex = i + 1 < length ? i + 1 : i;
            int nextNextIndex = i + 2 < length ? i + 2 : nextIndex;
            Point p1 = calc(drawPoints.get(i), drawPoints.get(nextIndex), secondMultiplier);
            Point p3 = calc(drawPoints.get(nextIndex), drawPoints.get(nextNextIndex), firstMultiplier);
            curvePath.cubicTo(p1.coordinateX, p1.coordinateY, drawPoints.get(nextIndex).coordinateX, drawPoints.get(nextIndex).coordinateY, p3.coordinateX,
                    p3.coordinateY);
            // Log.d("zqt", "valueY="+points.get(nextIndex).valueY+" coordinateY="+points.get(nextIndex).coordinateY);
            // canvas.drawCircle(points.get(nextIndex).coordinateX, points.get(nextIndex).coordinateY, 5, paint);
        }
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(curvePath, paint);
    }

    /** 先修正结点，再绘制结点 */
    private void drawPoints(Canvas canvas, List<Point> points) {
        paint.setStyle(Paint.Style.FILL);
        curvePathMeasure.setPath(curvePath, false);
        int length = (int) curvePathMeasure.getLength();
        // Log.d("zqt", "fixCurvePath length=" + length);
        float[] coords = new float[2];
        for (float i = 0; i <= length; i++) {
            curvePathMeasure.getPosTan(i, coords, null);
            for (Point point : points) {
                float diff = Math.abs(point.coordinateX - coords[0]);
                if (diff < 1 && point.fixedCoordinateY == 0) {
                    point.fixedCoordinateY = coords[1];
                    if (point.willDrawing&&point.valueY>0) {
                        canvas.drawCircle(point.coordinateX, point.fixedCoordinateY, 5, paint);
                        paint.setAlpha(80);
                        canvas.drawCircle(point.coordinateX, point.fixedCoordinateY, 10, paint);
                        paint.setAlpha(255);
                    }
                    // Log.e("zqt", "point.fixedCoordinateY=" + point.fixedCoordinateY + "-point.coordinateY=" +
                    // point.coordinateY);
                    // Log.e("zqt", "point=" + point.valueY + "-" + Math.abs(point.coordinateY - coords[1]));
                    int index = points.indexOf(point);
                    // 计算竖直线的顶点
                    if (info.gridPoints[index] == null || info.gridPoints[index].valueY < point.valueY) {
                        info.gridPoints[index] = point;
                    }
                    break;
                }

            }
        }
    }

    /** 计算插值点 */
    private Point calc(Point p1, Point p2, float multiplier) {
        Point result = new Point();
        float diffX = p2.coordinateX - p1.coordinateX; // p2.x - p1.x;
        float diffY = p2.coordinateY - p1.coordinateY; // p2.y - p1.y;
        result.coordinateX = p1.coordinateX + (diffX * multiplier);
        result.coordinateY = p1.coordinateY + (diffY * multiplier);
        return result;
    }

    /** 变换画布 */
    private void translateCanvas(Canvas canvas) {
        // Log.d("zqt", "info.translateX=" + info.translateX);
        if (info.translateX >= 0) {
            info.translateX = 0;
            canvas.translate(0, 0);
        } else if (info.translateX < 0) {
            if (info.yAxisWidth != 0 && info.translateX < -info.xAxisWidth / 2) {
                info.translateX = -info.xAxisWidth / 2;
            }
            canvas.translate(info.translateX, 0);
        }
        // Log.d("zqt", "info.translateX=" + info.translateX + " info.xAxisWidth="
        // + info.xAxisWidth);

    }

    /** 绘制横轴 */
    private void drawHorLabels(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        paint.setColor(style.getHorizontalLabelTextColor());
        paint.setTextSize(style.getHorizontalLabelTextSize());
        paint.setTextAlign(Align.CENTER);
        float endCoordinateX = info.xAxisWidth;
        float coordinateY = getHeight() - info.xAxisHeight - info.xTitleHeight;
        canvas.drawLine(0, coordinateY, endCoordinateX, coordinateY, paint);
        for (Label label : data.getXLabels()) {
            // 绘制橫坐标文本
            canvas.drawText(label.text, label.coordinateX, label.coordinateY,
                    paint);
        }
    }

    /** 绘制纵轴 */
    protected void drawVerLabels(Canvas canvas) {
        float coordinateX = data.getYLabels().get(0).coordinateX - info.verticalTextRect.width() * (0.5f + style.getVerticalLabelTextPaddingRate());
        float startCoordinateY = data.getYLabels().get(0).coordinateY;
        paint.setColor(style.getBackgroundColor());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(coordinateX, 0,
                info.getTranslateCoordinateX(getWidth()), getHeight(), paint);// 绘制一个白色矩形在纵坐标处盖住曲线
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        paint.setColor(style.getVerticalLabelTextColor());
        paint.setTextSize(style.getVerticalLabelTextSize());
        paint.setTextAlign(Align.CENTER);
        canvas.drawLine(coordinateX, startCoordinateY, coordinateX, getHeight()
                - info.xAxisHeight - info.xTitleHeight, paint);// 绘制纵坐标左边线条
        for (Label label : data.getYLabels()) {
            // 绘制纵坐标文本
            // Y轴坐标要下面偏移半个文本的高度，这样可以使文本的中心跟坐标的中心重合,再往下偏移几个像素以便对齐（原因：主要是由于verticalTextRect矩形比实际的text在上方多了几个像素）
            canvas.drawText(label.text, label.coordinateX, label.coordinateY
                    + info.verticalTextRect.height() / 2 - 5, paint);
            // canvas.drawCircle(label.coordinateX, label.coordinateY,5, paint);
        }
    }

    private void setHeight(int height) {
        LayoutParams lp = getLayoutParams();
        lp.height = height;
        setLayoutParams(lp);
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public void setData(ChartData data) {
        this.data = data;
        info.translateX = 0;
        animateThread = null;
        repaint=true;
        invalidate();
    }

    public ChartStyle getStyle() {
        return style;
    }

    /**
     * 设置曲率，0.0f<smoothness<=0.5f smoothness=0时，是一般的折线图 smoothness=0.5时,完全光滑的曲线图 smoothness>0.5会出现交叉
     * 
     * @param smoothness
     */
    public void setSmoothness(float smoothness) {
        firstMultiplier = smoothness;
        secondMultiplier = 1 - firstMultiplier;
    }
    /**自动滚动动画*/
    private class AnimateThread extends Thread {
        private boolean run = true;

        public void run() {
            int i = 80;
            float k = 0.99f;
            float j = 1f;
            while (run) {
                try {
                    if (i > 1) {
                        i = (int) (i * Math.pow(k, 3));
                        k = k - .01f;
                    }
                    Thread.sleep(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                info.translateX = info.translateX - j;
                if (info.translateX < -info.xAxisWidth / 2) {
                    run = false;
                }
                postInvalidate();
            }
        };
    }

    private class DrawingInfo {
        /** 纵坐标文本矩形 */
        private Rect verticalTextRect;
        /** 横坐标文本矩形 */
        private Rect horizontalTextRect;
        /** 横坐标标题文本矩形 */
        private Rect horizontalTitleRect;
        /** 画布X轴的平移，用于实现曲线图的滚动效果 */
        private float translateX;
        /** 图形的高度 */
        private int height;
        /** 纵轴的宽度 */
        private float yAxisWidth;
        /** 纵轴的高度 */
        private float yAxisHeight;
        /** 横轴的高度 */
        private float xAxisHeight;
        /** 横轴的标题的高度 */
        private int xTitleHeight;
        /** 横轴的长度 */
        private float xAxisWidth;

        private Point[] gridPoints;

        public DrawingInfo() {
            translateX = 0;
            verticalTextRect = new Rect();
            horizontalTextRect = new Rect();
            horizontalTitleRect = new Rect();
        }

        // /** 计算图形绘制的参数信息 */
        // private void computeInfo() {
        // // 计算横轴的时候需使用纵轴的高度计算纵坐标，故先计算纵轴，再计算横轴
        // computeVertcalAxisInfo();
        // computeHorizontalAxisInfo();
        //
        // computeSeriesCoordinate();
        // }

        /** 计算纵轴参数 */
        private void computeVertcalAxisInfo() {
            paint.setTextSize(style.getVerticalLabelTextSize());
            List<Label> yLabels = data.getYLabels();
            int yLabelCount = data.getYLabels().size();
            String maxText = getMaxText(yLabels);
            paint.getTextBounds(maxText, 0, maxText.length(), verticalTextRect);
            float x = getTranslateCoordinateX(getWidth()
                    - info.verticalTextRect.width() * (0.5f + style.getVerticalLabelTextPaddingRate()));
            for (int i = 0; i < yLabelCount; i++) {
                Label label = yLabels.get(i);
                label.coordinateX = x;
                label.coordinateY = verticalTextRect.height() * (i + 1)
                        + style.getVerticalLabelTextPadding() * (i + 0.5f);
                // Log.d("zqt", "label.coordinateY=" + label.coordinateY);
            }
            yAxisWidth = info.verticalTextRect.width() * (1 + style.getVerticalLabelTextPaddingRate() * 2);
            yAxisHeight = verticalTextRect.height() * yLabelCount
                    + style.getVerticalLabelTextPadding() * yLabelCount;
        }

        /** 计算横轴参数 */
        private void computeHorizontalAxisInfo() {
            xAxisWidth = 2 * (getWidth() - yAxisWidth);
            paint.setTextSize(style.getHorizontalLabelTextSize());
            List<Label> xLabels = data.getXLabels();
            // String maxText = getMaxText(xLabels);// 取得最长的文本
            String measureText = "张";
            paint.getTextBounds(measureText, 0, measureText.length(),
                    horizontalTextRect);
            xAxisHeight = horizontalTextRect.height() * 2;
            height = (int) (yAxisHeight + xAxisHeight);// 图形的高度计算完毕
            // float minXAxisWidth = xLabels.size() * horizontalTextRect.width()
            // * 1.5f;// 对横轴长度大小做一个限制，以免横轴文字过于拥挤
            // if (xAxisWidth < minXAxisWidth) {
            // xAxisWidth = minXAxisWidth;
            // }
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
            List<Series> seriess = data.getSeriesList();
            Marker marker = data.getMarker();
            int count = marker != null ? seriess.size() + 1 : seriess.size();
            float stepX = getWidth() / count;
            // Log.d("zqt", "stepX=" + stepX);
            float x = getTranslateCoordinateX(getWidth()
                    );
            for (int i = -1; i < seriess.size(); i++) {
                Title title = null;
                if(i>=0){
                    title = seriess.get(i).getTitle();
                    title.radius=10;
                }else if(marker!=null){
                    title=marker;
                    title.radius=15;
                }else{
                    continue;
                }
                title.circleTextPadding=20;
                title.updateTextRect(paint,stepX);
                title.textCoordinateX = 30 + x - (i + (data.getMarker()!=null?1.5f:0.5f)) * stepX;
                title.textCoordinateY = height-10;
                title.circleCoordinateX = title.textCoordinateX - title.textRect.width() / 2 - title.circleTextPadding;
                title.circleCoordinateY = title.textCoordinateY - info.horizontalTitleRect.height() * 0.5f;
            }
        }

        /** 计算序列的坐标信息 */
        private void computeSeriesCoordinate() {
            List<Label> yLabels = data.getYLabels();
            float minCoordinateY = yLabels.get(0).coordinateY;
            float maxCoordinateY = yLabels.get(yLabels.size() - 1).coordinateY;
            int length=0;
            for(Series series:data.getSeriesList()){
                if(series.getPoints().size()>length)
                    length=series.getPoints().size();
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
                    // Log.d("zqt", "point.coordinateX:" + point.coordinateX + " point.coordinateY:" +
                    // point.coordinateY);
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

        /**
         * 横坐标变换
         * 
         * @param coordinateX
         * @return
         */
        private float getTranslateCoordinateX(float coordinateX) {
            return coordinateX - translateX;
        }
    }
}
