package com.ralleq.influx.math;

import android.graphics.Color;
import android.util.Log;

public class ColorMath {

    public static int colorBetween(int startColor, int endColor, float transition) {
        if(transition < 1) {
            return Color.argb(
                    Math.min(1, (Color.alpha(startColor)*(-transition+1) + Color.alpha(endColor)*(transition)) / 255),
                    Math.min(1, (Color.red(startColor)*(-transition+1) + Color.red(endColor)*(transition)) / 255),
                    Math.min(1, (Color.green(startColor)*(-transition+1) + Color.green(endColor)*(transition)) / 255),
                    Math.min(1, (Color.blue(startColor)*(-transition+1) + Color.blue(endColor)*(transition)) / 255));
        } else {
            return endColor;
        }
    }
    public static int addAlpha(float alpha, int color) {
        return Color.argb((int) (255*alpha), Color.red(color), Color.green(color), Color.blue(color));

    }

}
