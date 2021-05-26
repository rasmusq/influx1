package com.ralleq.influx.objects.modules.modifiers;

import android.graphics.Canvas;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.objects.Value;
import com.ralleq.influx.objects.modules.tools.Envelope;
import com.ralleq.influx.screen.touch.TouchEvent;

public class EnvelopeModifier extends Modifier {

    @Override
    public String getName() {
        return "Envelope";
    }

    private Envelope envelope;

    public EnvelopeModifier(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);
        envelope = new Envelope();
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
        super.audio(inLeft, inRight, outLeft, outRight, currentFrame);
        outputValue = envelope.generateNextValue();
    }

    @Override
    public void midi(MidiEvent midiEvent) {
        if(midiEvent.getAction() == MidiEvent.ACTION_NOTE_ON) {
            envelope.restart();
        } else if(midiEvent.getAction() == MidiEvent.ACTION_NOTE_OFF) {
            envelope.release();
        }
    }

    @Override
    public void updateBounds(float left, float top, float right, float bottom) {
        super.updateBounds(left, top, right, bottom);

    }
}
