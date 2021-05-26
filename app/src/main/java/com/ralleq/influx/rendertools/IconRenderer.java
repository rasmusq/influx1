package com.ralleq.influx.rendertools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.ralleq.influx.MainActivity;

public class IconRenderer {

    public static void drawNumberIcon(Canvas canvas, RectF bounds, Paint paint, int number) {
        NumberRenderer.renderNumber(canvas, paint, bounds.centerX(), bounds.bottom, bounds.width()/2, number);
    }

    public static void drawBlankIcon(Canvas canvas, RectF bounds, Paint paint) {
        //Do nothing
    }

    public static void drawExitIcon(Canvas canvas, RectF bounds, Paint paint) {
        canvas.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, paint);
        canvas.drawLine(bounds.left, bounds.bottom, bounds.right, bounds.top, paint);
    }

    public static void drawMidiIcon(Canvas canvas, RectF bounds, Paint paint) {
        //See this image for reference math: https://images.app.goo.gl/PMcd9qq27y1bbPbo9
        float circleRadius = Math.min(bounds.width(), bounds.height())/8.0f*1.66f;
        int notesPressed = MainActivity.midiHandler.getBaseMidiDataKeeper().getMidiNotesOn().size();

        if(notesPressed > 0) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawCircle(bounds.left + circleRadius, bounds.bottom - circleRadius, circleRadius, paint);

        if(notesPressed > 1) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), circleRadius, paint);

        if(notesPressed > 2) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawCircle(bounds.right - circleRadius, bounds.top + circleRadius, circleRadius, paint);

        paint.setStyle(Paint.Style.STROKE);

    }

    public static void drawInstrumentIcon(Canvas canvas, RectF bounds, Paint paint) {
        float thirdOfWidth = bounds.width()/3;
        canvas.drawArc(bounds.left,
                bounds.bottom-thirdOfWidth,
                bounds.left+thirdOfWidth,
                bounds.bottom, 0,180, false, paint);
        canvas.drawArc(bounds.right-thirdOfWidth,
                bounds.bottom-thirdOfWidth,
                bounds.right,
                bounds.bottom, 0,180, false, paint);
        canvas.drawArc(bounds.left+thirdOfWidth,
                bounds.top,
                bounds.left+thirdOfWidth*2,
                bounds.top+thirdOfWidth, 180,180, false, paint);
        canvas.drawLine(bounds.left, bounds.centerY()-thirdOfWidth/2, bounds.left, bounds.bottom-thirdOfWidth/2, paint);
        canvas.drawLine(bounds.left+thirdOfWidth, bounds.top+thirdOfWidth/2, bounds.left+thirdOfWidth, bounds.bottom-thirdOfWidth/2, paint);
        canvas.drawLine(bounds.right-thirdOfWidth, bounds.top+thirdOfWidth/2, bounds.right-thirdOfWidth, bounds.bottom-thirdOfWidth/2, paint);
        canvas.drawLine(bounds.right, bounds.centerY()-thirdOfWidth/2, bounds.right, bounds.bottom-thirdOfWidth/2, paint);
    }

    public static void drawModifierIcon(Canvas canvas, RectF bounds, Paint paint) {
        /*canvas.drawArc(bounds.centerX() - radius, bounds.centerY() - radius,
                bounds.centerX() + radius, bounds.centerY() + radius,
                -90, 180, false, paint);*/
        float centerStrokeWidth = paint.getStrokeWidth()*1.5f;
        paint.setStrokeWidth(centerStrokeWidth);
        canvas.drawPoint(bounds.centerX(), bounds.centerY(), paint);
        paint.setStrokeWidth(paint.getStrokeWidth()/1.5f);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(),  bounds.width()/4, paint);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width()/2, paint);
    }

    public static void drawEffectIcon(Canvas canvas, RectF bounds, Paint paint) {
        canvas.drawLine(bounds.centerX(), bounds.top, bounds.right, bounds.centerY(), paint);
        canvas.drawLine(bounds.left, bounds.top, bounds.right, bounds.bottom, paint);
        canvas.drawLine(bounds.left, bounds.centerY(), bounds.centerX(), bounds.bottom, paint);

        canvas.drawLine(bounds.centerX(), bounds.top, bounds.left, bounds.centerY(), paint);
        canvas.drawLine(bounds.right, bounds.top, bounds.left, bounds.bottom, paint);
        canvas.drawLine(bounds.right, bounds.centerY(), bounds.centerX(), bounds.bottom, paint);
    }

    public static void drawRecorderIcon(Canvas canvas, RectF bounds, Paint paint) {
        float fifthOfWidth = bounds.width()/5;
        canvas.drawCircle(bounds.left + fifthOfWidth, bounds.centerY(), fifthOfWidth, paint);
        canvas.drawCircle(bounds.right - fifthOfWidth, bounds.centerY(), fifthOfWidth, paint);
        canvas.drawLine(bounds.left + fifthOfWidth,
                bounds.centerY() + fifthOfWidth,
                bounds.right - fifthOfWidth,
                bounds.centerY() + fifthOfWidth, paint);
    }

    public static void drawMixerIcon(Canvas canvas, RectF bounds, Paint paint) {
        double[] volumes = new double[] {1, 1, 1, 1};   //TODO Change to actual values
        float x = bounds.left;
        float lineSpacing = bounds.width()/(volumes.length-1);
        for(int i = 0; i < volumes.length; i++) {
            float yOffset = (float) (bounds.height()/2*volumes[i]);
            canvas.drawLine(x, bounds.centerY() - yOffset, x, bounds.centerY() + yOffset, paint);
            x += lineSpacing;
        }
    }

    public static void drawExportIcon(Canvas canvas, RectF bounds, Paint paint) {
        float chipSize = 0.25f;
        canvas.drawLine(bounds.left, bounds.top, bounds.left, bounds.bottom, paint);
        canvas.drawLine(bounds.left, bounds.bottom, bounds.right, bounds.bottom, paint);
        canvas.drawLine(bounds.right, bounds.bottom,
                bounds.right, bounds.top + bounds.height()*chipSize, paint);
        canvas.drawLine(bounds.right, bounds.top + bounds.height()*chipSize,
                bounds.right - bounds.width()*chipSize, bounds.top, paint);
        canvas.drawLine(bounds.right - bounds.width()*chipSize, bounds.top,
                bounds.left, bounds.top, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(bounds.centerX(), bounds.centerY() + bounds.height()*0.1f,
                paint.getStrokeWidth()*1.25f, paint);
    }

}
