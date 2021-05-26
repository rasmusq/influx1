package com.ralleq.influx.objects.modules.arrays;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.objects.modules.ModuleReturner;
import com.ralleq.influx.objects.modules.ModuleReturnerArray;
import com.ralleq.influx.objects.modules.instruments.synth.SynthInstrument;

public class InstrumentArray extends ModuleReturnerArray {

    public InstrumentArray(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);

        moduleReturners = new ModuleReturner[1];
        moduleReturners[0] = new SynthInstrument(location, 0);

    }

    @Override
    public void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame) {
        getSelectedModuleReturner().getModule().audio(inLeft, inRight, outLeft, outRight, currentFrame);
    }
    @Override
    public void midi(MidiEvent midiEvent) {
        getSelectedModuleReturner().getModule().midi(midiEvent);
    }

}
