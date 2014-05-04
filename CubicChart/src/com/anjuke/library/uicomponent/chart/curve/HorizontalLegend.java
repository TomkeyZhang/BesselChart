
package com.anjuke.library.uicomponent.chart.curve;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;

/**
 * 横向标题
 * 
 * @author tomkeyzhang（qitongzhang@anjuke.com）
 * @date :2014年5月4日
 */
public class HorizontalLegend extends View {
    private Paint paint;
    private ChartStyle style;
    private List<Title> titles;

    public HorizontalLegend(Context context, List<Title> titles, ChartStyle style) {
        super(context);
        this.titles = titles;
        this.style = style;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(titles.size()==0)
            return;
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(style.getHorizontalTitleTextSize());
        for (Title title : titles) {
            paint.setColor(title.color);
            paint.setTextAlign(Align.CENTER);
            paint.setTextSize(style.getHorizontalTitleTextSize());
            if (title instanceof Marker) {
                Marker marker = (Marker) title;
                canvas.drawBitmap(marker.getBitmap(), null,
                        marker.updateRect(title.circleCoordinateX, title.circleCoordinateY, title.radius * 2, title.radius * 2), paint);
            } else {
                canvas.drawCircle(title.circleCoordinateX, title.circleCoordinateY, title.radius, paint);
            }
            paint.setAlpha(255);
            canvas.drawText(title.text, title.textCoordinateX, title.textCoordinateY, paint);
        }
    }

}
