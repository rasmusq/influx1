package com.ralleq.influx.objects.modules.instruments.synth;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.objects.Value;
import com.ralleq.influx.objects.modules.Module;
import com.ralleq.influx.objects.modules.tools.ModulatedPolyphonicOscillator;
import com.ralleq.influx.screen.touch.TouchEvent;


public class SynthInstrument extends Module {

    @Override
    public String getName() {
        return "Synthesizer";
    }

    private double pitchBendScalar = 12, gain;
    private ModulatedPolyphonicOscillator oscillator;

    public SynthInstrument(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);
        oscillator = new ModulatedPolyphonicOscillator(6);
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

    /*private Path path;
    public void drawWave(int resolution, Canvas canvas, RectF bounds, Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.moveTo(bounds.left, bounds.centerY()
                - generateNextRenderingSample(0)
                /(float)Short.MAX_VALUE*bounds.height());
        for(int i = 1; i < resolution; i++) {
            path.lineTo(bounds.left + bounds.width()*i/resolution, bounds.centerY()
                    - generateNextRenderingSample(0)
                    /(float)Short.MAX_VALUE*bounds.height());
        }
        canvas.drawPath(path, paint);
        resetRenderPosition();
        paint.setStyle(Paint.Style.FILL);
    }*/

    @Override
    public void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame) {
        super.audio(inLeft, inRight, outLeft, outRight, currentFrame);
        int monoSample = 0;
        monoSample = (int) (oscillator.generateNextSample()*gain);
        outLeft[currentFrame] = monoSample;
        outRight[currentFrame] = monoSample;
    }

    @Override
    public void midi(MidiEvent midiEvent) {
        super.midi(midiEvent);
        oscillator.midi(midiEvent);
    }

    /*@Override
    public void updateBounds(float left, float top, float right, float bottom) {
        super.updateBounds(left, top, right, bottom);
        valueFields[0].updateBounds(getMarginedBoundsWithFractions(0, 0, 2/3f, 1/3f,
                1, 1, 0.5f, 1/3f));
        valueFields[1].updateBounds(getMarginedBoundsWithFractions(0, 1/3f, 2/3f, 2/3f,
                1, 2/3f, 0.5f, 2/3f));
        valueFields[2].updateBounds(getMarginedBoundsWithFractions(0, 2/3f, 1, 1,
                1, 0.5f, 1, 1));
        valueFields[3].updateBounds(getMarginedBoundsWithFractions(2/3f, 0, 1, 2/3f,
                0.5f, 1, 1, 0.5f));
    }*/

}
