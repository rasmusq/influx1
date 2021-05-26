package com.ralleq.influx.screen;

import android.content.Context;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.ralleq.influx.screen.touch.TouchEvent;
import com.ralleq.influx.screen.touch.TouchHandler;

public abstract class ScreenHandler {

    private DisplayMetrics displayMetrics;
    private float dpi;

    private CanvasView screen;

    private TouchHandler touchHandler;

    public abstract void draw(Canvas canvas);
    public abstract void fetchTouchEvent(TouchEvent touchEvent);
    public abstract void fetchSurfaceDimensions(int width, int height);
    public ScreenHandler(Context context) {

        displayMetrics = context.getResources().getDisplayMetrics();
        dpi = (displayMetrics.xdpi + displayMetrics.ydpi)/2.0f;

        touchHandler = new TouchHandler() {
            @Override
            public void fetchTouchEvent(TouchEvent touchEvent) {
                ScreenHandler.this.fetchTouchEvent(touchEvent);
            }
        };

        screen = new CanvasView(context) {
            @Override
            public void drawCustom(Canvas canvas) {
                ScreenHandler.this.draw(canvas);
            }
            @Override
            public void fetchMotionEvent(MotionEvent motionEvent) {
                touchHandler.receiveMotionEvent(motionEvent);
            }
            @Override
            public void fetchSurfaceDimensions(int width, int height) {
                ScreenHandler.this.fetchSurfaceDimensions(width, height);
            }
        };

    }

    public void hideUI() {
        screen.hideUI();
    }

    public int getScreenWidth() {
        return screen.getWidth();
    }
    public int getScreenHeight() {
        return screen.getHeight();
    }
    public float getDpi() {
        return dpi;
    }
    public float pixelsToInches(float pixels) {
        return pixels / dpi;
    }
    public float inchesToPixels(float inches) {
        return inches * dpi;
    }

    public CanvasView getScreen() {
        return screen;
    }


    public TouchHandler getTouchHandler() {
        return touchHandler;
    }

}
