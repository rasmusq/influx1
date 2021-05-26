package com.ralleq.influx.screen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public abstract class CanvasView extends SurfaceView implements SurfaceHolder.Callback {

    private Canvas canvas;
    private SurfaceHolder holder;

    private Thread drawThread;
    private boolean keepLoopAlive = true;

    public CanvasView(Context context) {
        super(context);

        holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);

        drawThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(keepLoopAlive) {
                    handleCanvasAndDraw();
                }
            }
        });
        drawThread.setName("Draw Thread");
        drawThread.start();
    }

    public abstract void drawCustom(Canvas canvas);
    public void handleCanvasAndDraw() {

        canvas = null;
        try {
            if(holder.getSurface().isValid()) {
                canvas = holder.lockCanvas();
            }
            if(canvas != null) {
                synchronized (holder) {
                    drawCustom(canvas);	//Draws everything on screen
                }
            }
        } finally {
            if(canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }
    public abstract void fetchSurfaceDimensions(int width, int height);
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        fetchSurfaceDimensions(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public abstract void fetchMotionEvent(MotionEvent motionEvent);
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        fetchMotionEvent(motionEvent);
        return true;
    }

    public void hideUI() {
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
}
