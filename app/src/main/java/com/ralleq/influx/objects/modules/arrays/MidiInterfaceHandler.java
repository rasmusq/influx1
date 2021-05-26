package com.ralleq.influx.objects.modules.arrays;

import com.ralleq.influx.midi.MidiEventFetcher;
import com.ralleq.influx.objects.modules.ModuleReturner;
import com.ralleq.influx.objects.modules.ModuleReturnerArray;
import com.ralleq.influx.objects.modules.effects.NothingEffect;
import com.ralleq.influx.objects.modules.midiinterpreter.MidiInterpreter;

public class MidiInterfaceHandler extends ModuleReturnerArray {

    public MidiInterfaceHandler(MidiEventFetcher midiEventFetcher, int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);

        moduleReturners = new ModuleReturner[3];
        moduleReturners[0] = new MidiProcessorArray(midiEventFetcher, location, 0);
        moduleReturners[1] = new MidiInterpreter(location, 1);

    }

    public MidiInterpreter getMidiInterpreter() {
        return (MidiInterpreter)moduleReturners[1];
    }

}