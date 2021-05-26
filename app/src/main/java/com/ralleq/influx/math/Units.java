package com.ralleq.influx.math;

public class Units {

    public static final float inchesPerCentimeter = 0.393701f;
    public static float screenDpi, screenDpcm,
            globalStrokeWidth, globalSmallTextSize, globalBigTextSize, globalCornerSize, globalMarginSize;

    public static float pixelsToInches(float pixels) {
        return pixels / screenDpi;
    }
    public static float inchesToPixels(float inches) {
        return inches * screenDpi;
    }
    public static float centimetersToPixels(float centimeters) {
        return centimeters * screenDpcm;
    }

    public static void setScreenDpi(float screenDpi, int screenWidth, int screenHeight) {
        Units.screenDpi = screenDpi;
        Units.screenDpcm = screenDpi*inchesPerCentimeter;
        globalStrokeWidth = centimetersToPixels(0.04f);
        globalSmallTextSize = centimetersToPixels(0.25f);
        globalBigTextSize = centimetersToPixels(1f);
        globalCornerSize = centimetersToPixels(0.25f);
        globalMarginSize = globalCornerSize;
    }

    public static float numberBetween(float firstNumber, float lastNumber, float fraction) {
        return firstNumber + (lastNumber-firstNumber)*fraction;
    }
    public static boolean isCloseToZero(float number, float threshold) {
        if(number > threshold || number < -threshold) {
            return false;
        }
        return true;
    }
    public static double limit(double value, double bottomLimit, double topLimit) {
        return Math.min(Math.max(value, bottomLimit), topLimit);
    }
    public static double integerPower(double value, int power) {
        double returnValue = value;
        for(int i = 0; i < power-1; i++) {
            returnValue *= value;
        }
        return returnValue;
    }

}
