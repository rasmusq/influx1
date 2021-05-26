package com.ralleq.influx.rendertools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.ralleq.influx.math.Units;

public class BorderRenderer {

    public static void renderBorder(Canvas canvas, Paint paint, RectF bounds) {
        float cornerSize = Units.globalCornerSize;
        canvas.drawLine(bounds.left, bounds.top, bounds.left + cornerSize, bounds.top, paint);
        canvas.drawLine(bounds.left, bounds.top + cornerSize, bounds.left, bounds.top, paint);

        canvas.drawLine(bounds.right, bounds.top, bounds.right - cornerSize, bounds.top, paint);
        canvas.drawLine(bounds.right, bounds.top + cornerSize, bounds.right, bounds.top, paint);

        canvas.drawLine(bounds.left, bounds.bottom, bounds.left + cornerSize, bounds.bottom, paint);
        canvas.drawLine(bounds.left, bounds.bottom - cornerSize, bounds.left, bounds.bottom, paint);

        canvas.drawLine(bounds.right, bounds.bottom, bounds.right - cornerSize, bounds.bottom, paint);
        canvas.drawLine(bounds.right, bounds.bottom - cornerSize, bounds.right, bounds.bottom, paint);
    }
    public static void renderBorder(Canvas canvas, Paint paint, RectF bounds, float cornerSize, float marginSize) {
        float left = bounds.left + marginSize;
        float top = bounds.top + marginSize;
        float right = bounds.right - marginSize;
        float bottom = bounds.bottom - marginSize;
        canvas.drawLine(left, top, left + cornerSize, top, paint);
        canvas.drawLine(left, top + cornerSize, left, top, paint);

        canvas.drawLine(right, top, right - cornerSize, top, paint);
        canvas.drawLine(right, top + cornerSize, right, top, paint);

        canvas.drawLine(left, bottom, left + cornerSize, bottom, paint);
        canvas.drawLine(left, bottom - cornerSize, left, bottom, paint);

        canvas.drawLine(right, bottom, right - cornerSize, bottom, paint);
        canvas.drawLine(right, bottom - cornerSize, right, bottom, paint);
    }


}
