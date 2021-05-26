package com.ralleq.influx.objects.buttons;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.ralleq.influx.MainActivity;
import com.ralleq.influx.files.ColorHandler;
import com.ralleq.influx.math.ColorMath;
import com.ralleq.influx.math.Units;
import com.ralleq.influx.screen.touch.TouchEvent;

public abstract class Button {

    protected Paint paint;
    protected RectF bounds, iconBounds;
    private int
            darkColor = ColorHandler.bgColor,
            lightColor = ColorHandler.btnIconColor;
    protected float actualLight, targetLight, lightSpeed = 0.2f;
    private static float iconMargin = 0.33f;

    public static long holdTime = 1000000*350;
    private boolean down, holding;
    private int lastDownPointerIndex = -1;
    private long lastDownTime, lastUpTime;

    public Button() {
        bounds = new RectF();
        iconBounds = new RectF();

        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
    }
    public abstract void onPress(ButtonEvent buttonEvent);
    public abstract void drawIcon(Canvas canvas);

    public void tick() {
        //Animates button lighting up when touching and holding
        actualLight += (targetLight - actualLight)*lightSpeed;

        //If the button has been down for longer than holdTime, then send hold action
        if(System.nanoTime() - lastDownTime > holdTime && down) {
            if(!holding) {
                onPress(new ButtonEvent(ButtonEvent.ACTION_HOLD));
                holding = true;
                targetLight = 1;
                MainActivity.vibrationHandler.vibrate(10);
            }
        }
    }

    public void draw(Canvas canvas) {
        //Background rendered for all buttons collectively in the SideBar class
        //Renders the touch response
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ColorMath.colorBetween(darkColor, lightColor, actualLight));
        if(actualLight > 0) {
            canvas.drawRect(bounds, paint);
        }
        //Rotates canvas and renders the icon

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ColorMath.colorBetween(lightColor, darkColor, actualLight));
        drawIcon(canvas);
    }

    public void touch(TouchEvent touchEvent) {
        //If pointer touches the button, lock onto that pointer and set button as down
        if(touchEvent.getActivePointer().getPosition().isInsideRect(bounds)
                && touchEvent.isDownEvent()) {
            lastDownPointerIndex = touchEvent.getActivePointerIndex();
            down = true;
            lastDownTime = System.nanoTime();
            targetLight = 0.35f;
            MainActivity.vibrationHandler.vibrate(10, 128);
        }
        //If the locked on pointer goes up, set button as up
        if(touchEvent.getActivePointerIndex() == lastDownPointerIndex
                && touchEvent.isUpEvent()) {
            //If the button has not been held for long enough time and the pointer is still inside
            //the button, run the press action
            if(touchEvent.getActivePointer().getLastDownPosition().isInsideRect(bounds)
                    && touchEvent.getActivePointer().getPosition().isInsideRect(bounds) && !holding) {
                onPress(new ButtonEvent(ButtonEvent.ACTION_PRESS));
            }
            down = false;
            lastUpTime = System.nanoTime();
            holding = false;
            targetLight = 0;
        }
    }

    public void updateBounds(float left, float top, float right, float bottom) {
        bounds.set(left, top, right, bottom);
        iconBounds.set(
                bounds.left + bounds.width()*iconMargin,
                bounds.top + bounds.width()*iconMargin,
                bounds.right - bounds.width()*iconMargin,
                bounds.bottom - bounds.width()*iconMargin);
        paint.setStrokeWidth(Units.globalStrokeWidth);
    }

    public void setDarkColor(int darkColor) {
        this.darkColor = darkColor;
    }
    public void setLightColor(int lightColor) {
        this.lightColor = lightColor;
    }

    public RectF getBounds() {
        return bounds;
    }
}