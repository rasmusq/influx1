package com.ralleq.influx.objects.controlknobs;

import com.ralleq.influx.midi.MidiEvent;

public class KnobEvent {

    private double speed;
    private MidiEvent controlModeEvent;
    private int knobIndex;
    private long time;

    public KnobEvent(double speed, MidiEvent controlModeEvent, int knobIndex) {
        this.speed = speed;
        this.controlModeEvent = controlModeEvent;
        this.knobIndex = knobIndex;
        time = System.nanoTime();
    }

    public double getSpeed() {
        return speed;
    }

    public MidiEvent getControlValueEvent() {
        return controlModeEvent;
    }

    public int getKnobIndex() {
        return knobIndex;
    }

    public long getTime() {
        return time;
    }

}
