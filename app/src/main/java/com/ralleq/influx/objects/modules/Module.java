package com.ralleq.influx.objects.modules;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.ralleq.influx.math.Units;
import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.objects.Value;
import com.ralleq.influx.objects.controlknobs.KnobEvent;
import com.ralleq.influx.objects.buttons.Button;
import com.ralleq.influx.screen.touch.TouchEvent;

public abstract class Module extends ModuleReturner {

    protected RectF bounds;
    private static final int VALUE_AMOUNT = 8, BUTTON_AMOUNT = 4;
    protected Value[] values;
    protected Button[] buttons;

    @Override
    public Module getModule() {
        return this;
    }
    @Override
    public String getName() {
        return "No Name Module";
    }

    public Module(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);
        bounds = new RectF();
        values = new Value[VALUE_AMOUNT];
        buttons = new Button[BUTTON_AMOUNT];
    }

    @Override
    public void knob(KnobEvent knobEvent) {

    }

    @Override
    public abstract void draw(Canvas canvas);

    @Override
    public abstract void touch(TouchEvent touchEvent);

    @Override
    public abstract void tick();

    @Override
    public void midi(MidiEvent midiEvent) {
        //Not necessary
    }
    @Override
    public void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame) {
        //Not necessary
    }

    @Override
    public void updateBounds(float left, float top, float right, float bottom) {
        bounds.set(left, top, right, bottom);
    }

    @Override
    public void save(String savePath) {
        if(values != null)
            for(int i = 0; i < values.length; i++) {
                if(values[i] != null) {
                    values[i].save(savePath + "valueIndex"+i+"_");
                }
            }
    }

    @Override
    public void load(String savePath) {
        if(values != null)
            for(int i = 0; i < values.length; i++) {
                if(values[i] != null) {
                    values[i].load(savePath + "valueIndex"+i+"_");
                }
            }
    }

    public RectF getMarginedBoundsWithFractions(float left, float top, float right, float bottom, float leftMargin, float topMargin, float rightMargin, float bottomMargin) {
        float marginSize = Units.globalMarginSize;
        float mLeft, mTop, mRight, mBottom;
        RectF mBounds = new RectF();
        mLeft = bounds.left + bounds.width()*left + marginSize*leftMargin;
        mTop = bounds.top + bounds.height()*top + marginSize*topMargin;
        mRight = bounds.left + bounds.width()*right - marginSize*rightMargin;
        mBottom = bounds.top + bounds.height()*bottom - marginSize*bottomMargin;

        mBounds.set(mLeft, mTop, mRight, mBottom);
        return mBounds;
    }

    public RectF getBounds() {
        return bounds;
    }
}
