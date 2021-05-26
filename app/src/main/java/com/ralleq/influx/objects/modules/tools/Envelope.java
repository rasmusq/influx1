package com.ralleq.influx.objects.modules.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import com.ralleq.influx.files.ColorHandler;
import com.ralleq.influx.math.Units;
import com.ralleq.influx.rendertools.IndicatorRenderer;

public class Envelope {



    private int maxAtkSeconds = 2, maxDcySeconds = 2, maxRlsSeconds = 2;
    private int maxAtkSamples, maxDcySamples, maxRlsSamples;
    private int atkSamples, dcySamples, rlsSamples;
    private double atk = 0.5, dcy = 0.5, stn = 0.5, rls = 0.5;
    private int pressCounter, releaseCounter;
    private boolean pressed, done;
    private double lastValue, releaseValue;

    private Path atkPath, dcyPath, rlsPath;
    private float renderPointX, renderPointY;
    float atkPathEndX, dcyPathEndX, rlsPathStartX, rlsPathWidth;

    public Envelope() {
        maxAtkSamples = maxAtkSeconds*58000;
        maxDcySamples = maxDcySeconds*48000;
        maxRlsSamples = maxRlsSeconds*48000;
        setAtk(atk);
        setDcy(dcy);
        setStn(stn);
        setRls(rls);

        atkPath = new Path();
        dcyPath = new Path();
        rlsPath = new Path();

        releaseCounter = rlsSamples;
    }

    public double generateNextValue() {
        double value = 0.0;
        if(!done) {
            if(pressed) {
                if(pressCounter < atkSamples) {
                    value = getAttackValue((double)pressCounter/(double)atkSamples);
                } else if (pressCounter < atkSamples+dcySamples){
                    value = getDecayValue((double)(pressCounter-atkSamples)/(double)dcySamples);
                } else {
                    value = stn;
                }
                pressCounter++;
            } else {
                value = getReleaseValue((double)releaseCounter/(double)rlsSamples, releaseValue);
                if(releaseCounter < rlsSamples)
                    releaseCounter++;
                else
                    done = true;
            }
        }
        lastValue = value;
        return value;
    }
    public double getAttackValue(double time) {
        return 1-Units.integerPower(1 - time, 4);
    }
    public double getDecayValue(double time) {
        return Math.max(stn, 1-((1-Units.integerPower(1-time, 4))*(1-stn)));
    }
    public double getReleaseValue(double time, double releaseValue) {
        return releaseValue-Math.max(1-(Units.integerPower(1-time, 4)), 0)*releaseValue;
    }

    public void draw(Canvas canvas, RectF bounds, Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        updatePath(bounds);
        paint.setColor(ColorHandler.knobColors[1]);
        canvas.drawPath(dcyPath, paint);
        paint.setColor(ColorHandler.knobColors[0]);
        canvas.drawPath(atkPath, paint);
        paint.setColor(ColorHandler.knobColors[2]);
        canvas.drawLine(dcyPathEndX, bounds.bottom - bounds.height()*(float)stn, rlsPathStartX, bounds.bottom - bounds.height()*(float)stn, paint);
        paint.setColor(ColorHandler.knobColors[3]);
        canvas.drawPath(rlsPath, paint);

        canvas.drawCircle(renderPointX, renderPointY, paint.getStrokeWidth(), paint);

        if(pressed) {
            if(pressCounter < atkSamples)
                paint.setColor(ColorHandler.knobColors[0]);
            else if(pressCounter < dcySamples+atkSamples)
                paint.setColor(ColorHandler.knobColors[1]);
            else
                paint.setColor(ColorHandler.knobColors[2]);
        } else {
            paint.setColor(ColorHandler.knobColors[3]);
        }
        float size = Units.centimetersToPixels(0.1f);
        IndicatorRenderer.renderTextIndicator(canvas, paint, (float)lastValue, bounds.right - size*2, bounds.top + size*4, size);
        paint.setStyle(Paint.Style.FILL);
    }

    public void updatePath(RectF bounds) {
        atkPath.reset();
        atkPath.moveTo(bounds.left, bounds.bottom);
        for(float i = 0; i < 1.1; i += 0.1) {
            atkPath.lineTo((float) (bounds.left + (bounds.width()/4)*(atkSamples/(float)maxAtkSamples)*i),
                    bounds.bottom - (float)getAttackValue(i)*bounds.height());
        }
        atkPathEndX = bounds.left + (bounds.width()/4)*(atkSamples/(float)maxAtkSamples);
        dcyPath.reset();
        dcyPath.moveTo((float)atkPathEndX, bounds.top);
        for(float i = 0; i < 1.1; i += 0.1) {
            dcyPath.lineTo(atkPathEndX + (bounds.width()/4)*(dcySamples/(float)maxDcySamples)*i,
                    bounds.bottom - (float)getDecayValue(i)*bounds.height());
        }
        dcyPathEndX = atkPathEndX + (bounds.width()/4)*(dcySamples/(float)maxDcySamples);
        rlsPathStartX = bounds.right - bounds.width()/3*(rlsSamples/(float)maxRlsSamples);
        rlsPathWidth = bounds.right - rlsPathStartX;
        rlsPath.reset();
        rlsPath.moveTo(rlsPathStartX, bounds.bottom - (float)getReleaseValue(0, stn)*bounds.height());
        for(float i = 0; i < 1.1; i += 0.1) {
            rlsPath.lineTo(rlsPathStartX + rlsPathWidth*i, bounds.bottom - (float)getReleaseValue(i, stn)*bounds.height());
        }

        if(pressed) {
            if(pressCounter < atkSamples) {
                float atkPercentage = ((float)pressCounter/(float)atkSamples);
                renderPointX = bounds.left + (bounds.width()/4)*(atkSamples/(float)maxAtkSamples)*atkPercentage;
                renderPointY = bounds.bottom - (float)getAttackValue(atkPercentage)*bounds.height();
            } else if(pressCounter < dcySamples+atkSamples) {
                float dcyPercentage = ((float)(pressCounter-atkSamples)/(float)dcySamples);
                renderPointX = atkPathEndX + (bounds.width()/4)*(dcySamples/(float)maxDcySamples)*dcyPercentage;
                renderPointY = bounds.bottom - (float)getDecayValue(dcyPercentage)*bounds.height();
            } else {
                float stnPercentage = (float)Math.pow(((double) (pressCounter-atkSamples-dcySamples)/(double) (atkSamples+dcySamples)), 0.5);
                stnPercentage *= stnPercentage;
                if(stnPercentage > 1)
                    stnPercentage = 1;
                renderPointX = (dcyPathEndX + (rlsPathStartX-dcyPathEndX)*stnPercentage);
                renderPointY = bounds.bottom - (float)stn*bounds.height();
            }
        } else {
            float rlsPercentage = ((float)releaseCounter/(float)rlsSamples);
            renderPointX = rlsPathStartX + rlsPathWidth*rlsPercentage;
            renderPointY = bounds.bottom - (float)getReleaseValue(rlsPercentage, stn)*bounds.height();
        }
    }

    public void restart() {
        pressed = true;
        done = false;
        pressCounter = 0;
    }
    public void release() {
        pressed = false;
        releaseValue = lastValue;
        releaseCounter = 0;
    }

    public void setAtk(double atk) {
        this.atk = atk;
        atkSamples = (int) (maxAtkSamples*atk)+1;
    }

    public void setDcy(double dcy) {
        this.dcy = dcy;
        dcySamples = (int) (maxDcySamples*dcy)+1;
    }

    public void setStn(double stn) {
        this.stn = stn;
    }

    public void setRls(double rls) {
        this.rls = rls;
        rlsSamples = (int) (maxRlsSamples*rls)+1;
    }

    public double getLastValue() {
        return lastValue;
    }
}
