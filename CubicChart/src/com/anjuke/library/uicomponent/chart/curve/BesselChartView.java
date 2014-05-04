package com.anjuke.library.uicomponent.chart.curve;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.anjuke.library.uicomponent.chart.curve.ChartData.Label;
/**
 * 贝塞尔曲线图
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年5月4日
 */
class BesselChartView extends View {
    /** 通用画笔 */
    private Paint paint;
    /** 曲线的路径，用于绘制曲线 */
    private Path curvePath;
    /** 曲线的路径路径测量器 */
    private PathMeasure curvePathMeasure;
    /** 曲线图绘制的计算信息 */
    private BesselCalculator calculator;
    /** 曲线图的样式 */
    private ChartStyle style;
    /** 曲线图的数据 */
    private ChartData data;

    /** 偏移因子 */
    private float firstMultiplier;
    private float secondMultiplier;
    /** 滚动的速度 */
    private float velocityX;
    
    private GestureDetector detector;

    public BesselChartView(Context context,ChartData data,ChartStyle style,BesselCalculator calculator) {
        super(context);
        this.calculator=calculator;
        this.data=data;
        this.style=style;
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.curvePath = new Path();
        this.curvePathMeasure = new PathMeasure(curvePath, false);
        this.velocityX = 1.2f;
        // 默认偏移第一个点33%的距离,
        this.firstMultiplier = 0.33f;
        // 默认偏移第二个点67%的距离.
        this.secondMultiplier = 1 - firstMultiplier;
        
        this.detector=new GestureDetector(getContext(), new SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(Math.abs(distanceX/distanceY)>1){
                    getParent().requestDisallowInterceptTouchEvent(true);
                    BesselChartView.this.calculator.move(distanceX,velocityX);
                    invalidate();
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return event.getAction() == MotionEvent.ACTION_DOWN;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("onDraw "+data.getSeriesList().size());
        if (data.getSeriesList().size() == 0)
            return;
        canvas.drawColor(style.getBackgroundColor());
        calculator.ensureTranslation();
        canvas.translate(calculator.getTranslateX(), 0);
        drawCurveAndPoints(canvas);
        drawGrid(canvas);
        drawMarker(canvas);
        drawHorLabels(canvas);
    }
    /**绘制曲线图中的房源*/
    private void drawMarker(Canvas canvas) {
        Marker marker = data.getMarker();
        if (marker != null) {
            paint.setAlpha(255);
            canvas.drawBitmap(marker.getBitmap(), null, marker.updateRect(marker.getPoint().coordinateX, marker.getPoint().coordinateY,marker.getWidth(),marker.getHeight()), paint);
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
        canvas.drawLine(0, coordinateY, calculator.xAxisWidth, coordinateY, paint);
        coordinateY = yLabels.get(yLabels.size() - 1).coordinateY;
        canvas.drawLine(0, coordinateY, calculator.xAxisWidth, coordinateY, paint);
        for (Point point : calculator.gridPoints) {
            if (point.willDrawing&&point.valueY>0)
                canvas.drawLine(point.coordinateX, point.fixedCoordinateY, point.coordinateX, calculator.yAxisHeight, paint);
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
             Log.d("valueY="+points.get(nextIndex).valueY+" coordinateY="+points.get(nextIndex).coordinateY);
             canvas.drawCircle(points.get(nextIndex).coordinateX, points.get(nextIndex).coordinateY, 5, paint);
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
                    if (calculator.gridPoints[index] == null || calculator.gridPoints[index].valueY < point.valueY) {
                        calculator.gridPoints[index] = point;
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


    /** 绘制横轴 */
    private void drawHorLabels(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        paint.setColor(style.getHorizontalLabelTextColor());
        paint.setTextSize(style.getHorizontalLabelTextSize());
        paint.setTextAlign(Align.CENTER);
        float endCoordinateX = calculator.xAxisWidth;
        float coordinateY = getHeight() - calculator.xAxisHeight - calculator.xTitleHeight;
        canvas.drawLine(0, coordinateY, endCoordinateX, coordinateY, paint);
        for (Label label : data.getXLabels()) {
            // 绘制橫坐标文本
            canvas.drawText(label.text, label.coordinateX, label.coordinateY,
                    paint);
        }
    }


    public void updateHeight() {
        Log.d("updateHeight="+calculator.height);
        LayoutParams lp = getLayoutParams();
        lp.height = calculator.height;
        setLayoutParams(lp);
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
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

}
