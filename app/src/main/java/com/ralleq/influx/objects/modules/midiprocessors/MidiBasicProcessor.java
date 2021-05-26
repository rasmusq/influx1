package com.ralleq.influx.objects.modules.midiprocessors;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiEventFetcher;
import com.ralleq.influx.objects.Value;
import com.ralleq.influx.screen.touch.TouchEvent;

public class MidiBasicProcessor extends MidiProcessor {

    public MidiBasicProcessor(MidiEventFetcher midiEventFetcher, int[] parentLocation, int newLocation) {
        super(midiEventFetcher, parentLocation, newLocation);
    }

    @Override
    public void updateBounds(float left, float top, float right, float bottom) {
        super.updateBounds(left, top, right, bottom);
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
    public void midi(MidiEvent midiEvent) {
        midiEventFetcher.fetch(midiEvent);
    }
}
