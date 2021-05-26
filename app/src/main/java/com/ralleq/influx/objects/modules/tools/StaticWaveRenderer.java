package com.ralleq.influx.objects.modules.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import java.util.Arrays;

public class StaticWaveRenderer {

    protected static final int renderBufferResolution = 256;
    private int renderBufferCounter;
    private double renderWavePos;
    private int[] renderBufferFill, renderBufferFinal;

    private Path path;

    public StaticWaveRenderer() {
        renderBufferFill = new int[renderBufferResolution];
        renderBufferFinal = new int[renderBufferResolution];

        path = new Path();
    }

    public void logSampleToBuffer(int sample, double waveSpeed) {
        renderWavePos += waveSpeed;
        while(renderWavePos >= Math.PI*8/renderBufferResolution) {
            renderBufferFill[renderBufferCounter] = sample;
            renderWavePos -= Math.PI*8/renderBufferResolution;
            renderBufferCounter++;
            if(renderBufferCounter >= renderBufferFill.length) {
                renderBufferCounter = 0;
                for(int j = 0; j < renderBufferFinal.length; j++) {
                    renderBufferFinal[j] = renderBufferFill[j];
                }
            }
        }
    }
    public void draw(RectF bounds, Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.moveTo(bounds.left, bounds.centerY() + bounds.height()*(renderBufferFill[0]/(float)Short.MAX_VALUE)/2);
        for(int i = 1; i < renderBufferFill.length; i++) {
            path.lineTo(bounds.left + bounds.width()/ renderBufferFill.length*i, bounds.centerY() + bounds.height()*(renderBufferFill[i]/(float)Short.MAX_VALUE)/2);
        }
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
    }

}
