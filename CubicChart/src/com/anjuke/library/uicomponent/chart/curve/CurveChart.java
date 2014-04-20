package com.anjuke.library.uicomponent.chart.curve;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.anjuke.library.uicomponent.chart.curve.ChartData.LabelTransform;

public class CurveChart extends View {
	/** 通用画笔 */
	private Paint paint;
	/** 曲线的路径，用于绘制曲线 */
	private Path curvePath;
	/** 曲线图绘制的参数信息 */
	private DrawingInfo info;
	/** 曲线图的样式 */
	private ChartStyle style;
	/** 曲线图的数据 */
	private ChartData data;

	/** 滚动的速度 */
	private float velocityX;
	/** 是否全部重新绘制 */
	private boolean repaint;
	
	/**偏移因子*/
	private float firstMultiplier;
	private float secondMultiplier;

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
		info = new DrawingInfo();
		velocityX = 1.0f;
		// 默认偏移第一个点33%的距离,
	    firstMultiplier = 0.33f;
	    // 默认偏移第二个点67%的距离.
	    secondMultiplier = 1 - firstMultiplier;
		repaint = true;

		style = new ChartStyle();
		data = new ChartData();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE
				&& info.lastTouchEventX >= 0) {
			info.translateX = info.translateX
					+ (event.getX() - info.lastTouchEventX) * velocityX;// 计算画布平移距离
			repaint(false);
		}
		info.lastTouchEventX = event.getX();
		return true;
	}
	
	private void repaint(boolean repaint) {
		this.repaint = repaint;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		translateCanvas(canvas);
		info.computeInfo();
		setHeight(info.height);
//		testDraw(canvas);
		drawCurve(canvas);
		drawHorLabels(canvas);
		drawVerLabels(canvas);
		repaint = false;
	}
	/**绘制曲线图*/
	private void drawCurve(Canvas canvas) {
		for(Series series:data.getSeriesList()){
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(5);
			paint.setColor(series.getColor());
			curvePath.reset();
			List<Point> points=series.getPoints();
			curvePath.moveTo(points.get(0).coordinateX, points.get(0).coordinateY);
			int length=points.size();
			for(int i=0;i<length;i++){
				int nextIndex=i+1<length?i+1:i;
				int nextNextIndex=i+2<length?i+2:nextIndex;
				Point p1=calc(points.get(i), points.get(nextIndex), secondMultiplier);
				Point p3=calc(points.get(nextIndex), points.get(nextNextIndex), firstMultiplier);
				curvePath.cubicTo(p1.coordinateX, p1.coordinateY, points.get(nextIndex).coordinateX, points.get(nextIndex).coordinateY, p3.coordinateX, p3.coordinateY);
			}
			canvas.drawPath(curvePath, paint);
		}
	}
	private Point calc(Point p1,Point p2, float multiplier) {
		Point result=new Point();
	    float diffX = p2.coordinateX - p1.coordinateX; // p2.x - p1.x;
	    float diffY = p2.coordinateY - p1.coordinateY; // p2.y - p1.y;
	    result.coordinateX=p1.coordinateX + (diffX * multiplier);
	    result.coordinateY=p1.coordinateY + (diffY * multiplier);
	    return result;
	  }
	/** 变换画布 */
	private void translateCanvas(Canvas canvas) {
		Log.d("zqt", "info.translateX=" + info.translateX);
		if (info.translateX >= 0) {
			info.translateX = 0;
			canvas.translate(0, 0);
		} else if (info.translateX < 0) {
			if (info.yAxisWidth != 0 && info.translateX < -info.xAxisWidth / 2) {
				info.translateX = -info.xAxisWidth / 2;
			}
			canvas.translate(info.translateX, 0);
		}
		Log.d("zqt", "info.translateX=" + info.translateX + " info.xAxisWidth="
				+ info.xAxisWidth);
	}

	private void drawHorLabels(Canvas canvas) {
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);
		paint.setColor(style.getHorizontalLabelTextColor());
		paint.setTextSize(style.getHorizontalLabelTextSize());
		paint.setTextAlign(Align.CENTER);
		float endCoordinateX = info.getTranslateCoordinateX(getWidth()
				- info.yAxisWidth);
		float coordinateY = getHeight() - info.xAxisHeight;
		canvas.drawLine(0, coordinateY, endCoordinateX, coordinateY, paint);
		for (Label label : data.getXLabels()) {
			// 绘制橫坐标文本
			canvas.drawText(label.text, label.coordinateX, label.coordinateY,
					paint);
		}
	}

	/**
	 * 绘制纵坐标
	 */
	protected void drawVerLabels(Canvas canvas) {
		float coordinateX = info.getTranslateCoordinateX(getWidth()
				- info.yAxisWidth);
		float startCoordinateY = data.getYLabels().get(0).coordinateY;
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(coordinateX, 0,
				info.getTranslateCoordinateX(getWidth()), getHeight(), paint);// 绘制一个白色矩形在纵坐标处盖住曲线
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);
		paint.setColor(style.getVerticalLabelTextColor());
		paint.setTextSize(style.getVerticalLabelTextSize());
		paint.setTextAlign(Align.CENTER);
		canvas.drawLine(coordinateX, startCoordinateY, coordinateX, getHeight()
				- info.xAxisHeight, paint);// 绘制纵坐标左边线条
		for (Label label : data.getYLabels()) {
			// 绘制纵坐标文本
			// Y轴坐标要下面偏移半个文本的高度，这样可以使文本的中心跟坐标的中心重合,再往下偏移几个像素以便对齐（原因：主要是由于verticalTextRect矩形比实际的text在上方多了几个像素）
			canvas.drawText(label.text, label.coordinateX, label.coordinateY
					+ info.verticalTextRect.height() / 2 - 5, paint);
			// canvas.drawCircle(label.coordinateX, label.coordinateY,5, paint);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	private void setHeight(int height) {
		LayoutParams lp = getLayoutParams();
		lp.height = height;
		setLayoutParams(lp);
	}

	public void setVelocityX(float velocityX) {
		this.velocityX = velocityX;
	}

	private void testDraw(Canvas canvas) {
		paint.setColor(Color.CYAN);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		curvePath.reset();
		curvePath.cubicTo(10, 20, 20, 30, 30, 40);
		curvePath.moveTo(20, 30);
		curvePath.cubicTo(30, 40, 80, 90, 100, 300);
		curvePath.moveTo(80, 90);
		curvePath.cubicTo(100, 300, 330, 200, 340, 90);
		canvas.drawPath(curvePath, paint);
		curvePath.reset();
		paint.setColor(Color.RED);
		curvePath.moveTo(330, 200);
		curvePath.cubicTo(340, 90, 400, 200, 450, 270);
		curvePath.moveTo(400, 200);
		curvePath.cubicTo(450, 270, 500, 210, 530, 150);
		curvePath.moveTo(500, 210);
		curvePath.cubicTo(530, 150, 600, 290, 650, 300);
		curvePath.cubicTo(630, 150, 700, 290, 1650, 300);
		canvas.drawPath(curvePath, paint);
	}

	public void setData(ChartData data) {
		this.data = data;
	}

	public void setStyle(ChartStyle style) {
		this.style = style;
	}
	/**
	 * 设置曲率，0.0f<smoothness<=0.5f
	 * smoothness=0时，是一般的折线图
	 * smoothness=0.5时,完全光滑的曲线图
	 * smoothness>0.5会出现交叉
	 * @param smoothness
	 */
	public void setSmoothness(float smoothness){
		firstMultiplier = smoothness;
	    secondMultiplier = 1 - firstMultiplier;
	}
	private class DrawingInfo {
		/** 纵坐标文本矩形 */
		private Rect verticalTextRect;
		/** 横坐标文本矩形 */
		private Rect horizontalTextRect;
		/** 画布X轴的平移，用于实现曲线图的滚动效果 */
		private float translateX;
		/** 上次触摸屏幕的X轴坐标，用于实现曲线图的滚动效果 */
		private float lastTouchEventX;
		/** 图形的高度 */
		private int height;
		/** 图形的宽度 */
		private int width;
		/** 纵轴的宽度 */
		private float yAxisWidth;
		/** 横轴的高度 */
		private float xAxisHeight;
		/** 横轴的长度 */
		private float xAxisWidth;

		public DrawingInfo() {
			translateX = 0;
			lastTouchEventX = -1;
			verticalTextRect = new Rect();
			horizontalTextRect = new Rect();
		}

		/** 计算图形绘制的参数信息 */
		private void computeInfo() {
			// 计算横轴的时候需使用纵轴的高度计算纵坐标，故先计算纵轴，再计算横轴
			computeVertcalAxisInfo();
			computeHorizontalAxisInfo();

			computeSeriesCoordinate();
		}

		/** 计算纵轴参数 */
		private void computeVertcalAxisInfo() {
			paint.setTextSize(style.getVerticalLabelTextSize());
			List<Label> yLabels = data.getYLabels();
			int yLabelCount = data.getYLabels().size();
			String maxText = getMaxText(yLabels);
			paint.getTextBounds(maxText, 0, maxText.length(), verticalTextRect);
			float x = getTranslateCoordinateX(getWidth()
					- info.verticalTextRect.width() * 0.8f);
			for (int i = 0; i < yLabelCount; i++) {
				Label label = yLabels.get(i);
				label.coordinateX = x;
				label.coordinateY = verticalTextRect.height() * (i + 1)
						+ style.getVerticalLabelTextPadding() * (i + 0.5f);
			}
			yAxisWidth = info.verticalTextRect.width() * 1.6f;
			// 此时的height还未包含横轴的高度
			height = verticalTextRect.height() * yLabelCount
					+ style.getVerticalLabelTextPadding() * yLabelCount;
		}

		/** 计算横轴参数 */
		private void computeHorizontalAxisInfo() {
			xAxisWidth = 2 * (getWidth() - yAxisWidth);
			paint.setTextSize(style.getHorizontalLabelTextSize());
			List<Label> xLabels = data.getXLabels();
			String maxText = getMaxText(xLabels);// 取得最长的文本
			paint.getTextBounds(maxText, 0, maxText.length(),
					horizontalTextRect);
			xAxisHeight = horizontalTextRect.height() * 2;
			height = height + (int) xAxisHeight;// 图形的高度计算完毕
			float minXAxisWidth = xLabels.size() * horizontalTextRect.width()
					* 1.5f;// 对横轴长度大小做一个限制，以免横轴文字过于拥挤
			if (xAxisWidth < minXAxisWidth) {
				xAxisWidth = minXAxisWidth;
			}
			float labelWidth = xAxisWidth / xLabels.size();
			for (int i = 0; i < xLabels.size(); i++) {
				Label label = xLabels.get(i);
				label.coordinateX = labelWidth * (i + 0.5f);
				label.coordinateY = height - horizontalTextRect.height() * 0.5f;
			}
		}

		/** 计算序列的坐标信息 */
		private void computeSeriesCoordinate() {
			List<Label> yLabels=data.getYLabels();
			float minCoordinateY=yLabels.get(0).coordinateY;
			float maxCoordinateY=yLabels.get(yLabels.size()-1).coordinateY;
			for(Series series:data.getSeriesList()){
				List<Point> points=series.getPoints();
				float pointWidth = xAxisWidth / points.size();
				for(int i=0;i<points.size();i++){
					Point point=points.get(i);
					point.coordinateX=pointWidth * (i + 0.5f);
					float ratio=(point.valueY-data.getMinValueY())/(float)(data.getMaxValueY()-data.getMinValueY());
					point.coordinateY=maxCoordinateY-(maxCoordinateY-minCoordinateY)*ratio;
					Log.d("zqt", "point.coordinateX:"+point.coordinateX+" point.coordinateY:"+point.coordinateY);
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
