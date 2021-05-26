package com.ralleq.influx.objects.modules.effects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.ralleq.influx.objects.modules.Module;
import com.ralleq.influx.screen.touch.TouchEvent;

public class NothingEffect extends Module {

    @Override
    public String getName() {
        return "Nothing";
    }

    public NothingEffect(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawText("NOTHING", bounds.centerX(), bounds.centerY(), paint);
    }

    @Override
    public void touch(TouchEvent touchEvent) {

    }

    @Override
    public void tick() {

    }

}
