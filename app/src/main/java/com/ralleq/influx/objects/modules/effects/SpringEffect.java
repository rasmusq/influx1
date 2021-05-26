package com.ralleq.influx.objects.modules.effects;

import android.graphics.Canvas;

import com.ralleq.influx.objects.Value;
import com.ralleq.influx.objects.modules.Module;
import com.ralleq.influx.objects.modules.tools.Spring;
import com.ralleq.influx.screen.touch.TouchEvent;

public class SpringEffect extends Module {

    @Override
    public String getName() {
        return "Spring Reverb";
    }

    private Spring spring;
    private double mix, gain;

    public SpringEffect(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);
        spring = new Spring();
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void touch(TouchEvent touchEvent) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame) {
        int monoSample = spring.generateNextSample((outLeft[currentFrame]+outRight[currentFrame])/2);
        outLeft[currentFrame] = (int) ((monoSample*mix + outLeft[currentFrame]*(1-mix))*gain);
        outRight[currentFrame] = (int) ((monoSample*mix + outRight[currentFrame]*(1-mix))*gain);
    }

    @Override
    public void updateBounds(float left, float top, float right, float bottom) {
        super.updateBounds(left, top, right, bottom);
    }
}
