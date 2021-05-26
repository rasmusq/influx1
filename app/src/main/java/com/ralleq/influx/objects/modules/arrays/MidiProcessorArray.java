package com.ralleq.influx.objects.modules.arrays;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiEventFetcher;
import com.ralleq.influx.objects.modules.ModuleReturner;
import com.ralleq.influx.objects.modules.ModuleReturnerArray;
import com.ralleq.influx.objects.modules.midiprocessors.MidiBasicProcessor;
import com.ralleq.influx.objects.modules.midiprocessors.MidiChordProcessor;

public class MidiProcessorArray extends ModuleReturnerArray {

    public MidiProcessorArray(MidiEventFetcher midiEventFetcher, int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);

        moduleReturners = new ModuleReturner[2];
        moduleReturners[0] = new MidiBasicProcessor(midiEventFetcher, location, 0);
        moduleReturners[1] = new MidiChordProcessor(midiEventFetcher, location, 0);

    }

    @Override
    public void midi(MidiEvent midiEvent) {
        getSelectedModuleReturner().getModule().midi(midiEvent);
    }
}
