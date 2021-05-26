package com.ralleq.influx.rendertools;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.ralleq.influx.files.ColorHandler;
import com.ralleq.influx.math.ColorMath;

public class IndicatorRenderer {

    public static void renderSmallIndicator(Canvas c, Paint p, float value, float x, float y, float radius) {
        int color = p.getColor();
        p.setColor(ColorMath.colorBetween(ColorHandler.bgColor, color, 0.5f));
        c.drawArc(x-radius, y-radius, x+radius, y+radius, 135, 270, false, p);
        p.setColor(color);
        c.drawArc(x-radius, y-radius, x+radius, y+radius, 135, 270*value, false, p);
        NumberRenderer.renderBigNumber(c, p, (int) (value*99),
                x, y + radius, radius/4, radius/4);
    }
    public static void renderBigIndicator(Canvas c, Paint p, float value, float x, float y, float radius) {
        int color = p.getColor();
        p.setColor(ColorMath.colorBetween(ColorHandler.bgColor, color, 0.5f));
        c.drawArc(x-radius, y-radius, x+radius, y+radius, 135, 270, false, p);
        p.setColor(color);
        c.drawArc(x-radius, y-radius, x+radius, y+radius, 135, 270*value, false, p);
    }
    public static void renderTextIndicator(Canvas c, Paint p, float value, float x, float y, float radius) {
        NumberRenderer.renderBigNumber(c, p, (int) (value*99), x, y, radius, radius/2);
    }

}
