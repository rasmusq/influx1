package com.ralleq.influx.objects.modules.midiinterpreter;

import android.graphics.Canvas;

import com.ralleq.influx.objects.modules.Module;
import com.ralleq.influx.screen.touch.TouchEvent;

public class MidiInterpreter extends Module {

    public MidiInterpreter(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);

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

    public double getFrequencyFromNote(double note) {
        return 440.0/64.0 * Math.pow(2, (note+3)/12.0);
    }

}
