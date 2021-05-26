package com.ralleq.influx.objects.modules.tools;

import android.util.Log;

import com.ralleq.influx.midi.MidiEvent;

public class ModulatedPolyphonicOscillator {

    private PolyphonicOscillator oscillator, modulator;
    private double modulation;

    public ModulatedPolyphonicOscillator(int oscillatorAmount) {
        oscillator = new PolyphonicOscillator(oscillatorAmount, true);
        modulator = new PolyphonicOscillator(oscillatorAmount, false);
    }

    public int generateNextSample() {
        modulator.generateNextSample(null, 0);
        int sample = oscillator.generateNextSample(modulator, modulation);
        return sample;
    }
    public void midi(MidiEvent midiEvent) {
        modulator.midi(midiEvent);
        oscillator.midi(midiEvent);
    }

    public PolyphonicOscillator getOscillator() {
        return oscillator;
    }

    public PolyphonicOscillator getModulator() {
        return modulator;
    }

    public void setModulation(double modulation) {
        this.modulation = modulation;
    }
}
