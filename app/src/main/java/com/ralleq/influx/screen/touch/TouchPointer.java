package com.ralleq.influx.screen.touch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.ralleq.influx.math.Units;
import com.ralleq.influx.math.Vector;

public class TouchPointer {

    private Vector position, velocity, lastDownPosition, lastUpPosition, previousPosition;
    private double travelX, travelY;
    private boolean down, lastDownWasDoubleTap, lastUpWasTap;
    private long lastDownTime, lastUpTime;  //In nanoseconds
    private long doubleTapTime = 300*1000000, tapTime = 150*1000000;

    private Paint paint;
    public void draw(Canvas canvas) {
        if (down)
            paint.setStyle(Paint.Style.FILL);
        else
            paint.setStyle(Paint.Style.STROKE);
        float radius = Units.centimetersToPixels(0.5f);
        canvas.drawCircle((float)getPosition().getX(),
                (float)getPosition().getY(), radius, paint);
    }

    public TouchPointer() {
        position = new Vector();
        velocity = new Vector();
        lastDownPosition = new Vector();
        lastUpPosition = new Vector();
        previousPosition = new Vector();

        paint = new Paint();
        paint.setColor(Color.RED);
    }

    public void addTravelX(double toAdd) {
        travelX += toAdd;
    }
    public double getTravelX() {
        return travelX;
    }
    public void addTravelY(double toAdd) {
        travelY += toAdd;
    }
    public double getTravelY() {
        return travelY;
    }
    public double getTravelTotal() {
        return Math.sqrt(travelX*travelX + travelY*travelY);
    }
    public Vector getPosition() {
        return position;
    }
    public Vector getVelocity() {
        return velocity;
    }
    public Vector getLastDownPosition() {
        return lastDownPosition;
    }
    public Vector getLastUpPosition() {
        return lastUpPosition;
    }
    public Vector getPreviousPosition() {
        return previousPosition;
    }
    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        long nowTime = System.nanoTime();
        if(!this.down && down) {    //Was up, is down
            lastDownWasDoubleTap = nowTime - lastDownTime <= doubleTapTime; //Double-tap
            lastDownTime = nowTime;
            lastDownPosition.set(position.getX(), position.getY());
            travelX = 0;
            travelY = 0;
        } else if(this.down && !down) { //Was down, is up
            lastUpTime = nowTime;
            lastUpWasTap = (lastUpTime-lastDownTime) < tapTime;
            lastUpPosition.set(position.getX(), position.getY());
        }
        this.down = down;
    }

    public boolean lastDownWasDoubleTap() {
        return lastDownWasDoubleTap;
    }
    public boolean lastUpWasTap() {
        return lastUpWasTap;
    }

    public long getTimeSinceLastDown() {
        return System.nanoTime() - lastDownTime;
    }
    public long getTimeSinceLastUp() {
        return System.nanoTime() - lastUpTime;
    }

    public long getLastDownTime() {
        return lastDownTime;
    }
    public long getLastUpTime() {
        return lastUpTime;
    }
}
