
package com.anjuke.library.uicomponent.chart.bessel;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.tomkey.testcubic.R;



/***
 * 完整的贝塞尔曲线图
 * 
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年5月4日
 */
public class BesselChart extends LinearLayout {
    /** 贝塞尔曲线图 */
    private BesselChartView besselChartView;
    /** 纵轴 */
    private VerticalAxisView verticalAxis;
    /** 横向说明 */
    private HorizontalLegendView horizontalLegend;
    /** 动画对象 */
    private AnimateRunnable animateRunnable;
    /** 带纵轴的贝塞尔曲线图 */
    private LinearLayout besselChartLayout;
    /** 横轴的位置 */
    private int position = VerticalAxisView.POSITION_RIGHT;
    /** 曲线图绘制的计算信息 */
    private BesselCalculator calculator;
    /** 曲线图的样式 */
    private ChartStyle style;
    /** 曲线图的数据 */
    private ChartData data;

    public BesselChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChartStyle);
        position = a.getInt(R.styleable.ChartStyle_verticalAxisPosition, VerticalAxisView.POSITION_RIGHT);
        a.recycle();
        init();
    }

    public BesselChart(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        style = new ChartStyle();
        data = new ChartData();
        calculator = new BesselCalculator(data, style);
        animateRunnable = new AnimateRunnable();
        besselChartLayout = new LinearLayout(getContext());
        besselChartView = new BesselChartView(getContext(), data, style, calculator);
        verticalAxis = new VerticalAxisView(getContext(), data.getYLabels(), style, calculator);
        horizontalLegend = new HorizontalLegendView(getContext(), data.getTitles(), style, calculator);
        besselChartLayout.setOrientation(LinearLayout.HORIZONTAL);
        verticalAxis.setPosition(position);
        besselChartLayout.addView(verticalAxis, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        if (position == VerticalAxisView.POSITION_LEFT) {
            besselChartLayout.addView(besselChartView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
        } else {
            besselChartLayout.addView(besselChartView, 0, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
        }
        addView(besselChartLayout, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(horizontalLegend, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void setPosition(int position) {
        this.position = position;
        verticalAxis.setPosition(position);
        invalidate();
    }

    /**
     * 获取数据对象
     * 
     * @return
     */
    public ChartData getData() {
        return data;
    }

    /**
     * 获取样式对象
     * 
     * @return
     */
    public ChartStyle getStyle() {
        return style;
    }

    /** 刷新数据 */
    public void refresh() {
        refresh(false);
    }

    /**
     * 设置光滑因子
     * 
     * @param smoothness
     */
    public void setSmoothness(float smoothness) {
        calculator.setSmoothness(smoothness);
    }

    public void setDrawBesselPoint(boolean drawBesselPoint) {
        besselChartView.setDrawBesselPoint(drawBesselPoint);
    }
    /***
     * 带动画刷新数据
     * 
     * @param animate
     */
    public void refresh(final boolean animate) {
        post(new Runnable() {
            @Override
            public void run() {
                calculator.compute(getWidth());// 重新计算图形信息
                besselChartView.updateSize();// 更新图形的
                verticalAxis.updateSize();// 更新纵轴的宽高
                horizontalLegend.updateHeight();// 更新标题的高度
                // horizontalLegend.
//                setLayoutParams(getLayoutParams());
                invalidate();
                besselChartView.animateScrollToEnd(5000);
                // if (animate && !animateRunnable.run) {
                // // 同一个时间只能有一个动画在跑
                // animateRunnable.run = true;
                // new Thread(animateRunnable).start();
                // }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animateRunnable.run = false;// 取消动画
    }

    /**
     * 自动滚动动画
     */
    private class AnimateRunnable implements Runnable {
        private boolean run = false;

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
                calculator.move(j);
                run = !calculator.ensureTranslation();
                besselChartView.postInvalidate();
            }
        };
    }

    public void setChartListener(ChartListener chartListener) {
        besselChartView.setChartListener(chartListener);
    }
    public interface ChartListener {
        void onMove();
    }
}
