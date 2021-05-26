package com.ralleq.influx.objects.modules;

import android.graphics.Canvas;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.objects.controlknobs.KnobEvent;
import com.ralleq.influx.screen.touch.TouchEvent;

public abstract class ModuleReturner {

    protected int[] location;
    protected int index;
    public ModuleReturner(int[] parentLocation, int newLocation) {
        index = newLocation;
        if(parentLocation != null) {
            location = new int[parentLocation.length+1];
            for(int i = 0; i < parentLocation.length; i++) {
                location[i] = parentLocation[i];
            }
            location[parentLocation.length] = newLocation;
        } else {
            location = new int[] {newLocation};
        }
    }

    public int[] getLocation() {
        return location;
    }

    public abstract Module getModule();
    public abstract String getName();

    public abstract void knob(KnobEvent knobEvent);
    public abstract void draw(Canvas canvas);
    public abstract void touch(TouchEvent touchEvent);
    public abstract void tick();
    public abstract void midi(MidiEvent midiEvent);
    public abstract void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame);
    public abstract void updateBounds(float left, float top, float right, float bottom);

    public abstract void save(String savePath);
    public abstract void load(String savePath);

}
