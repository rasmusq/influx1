package com.ralleq.influx.objects.modules.midiprocessors;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiEventFetcher;
import com.ralleq.influx.objects.modules.Module;

public abstract class MidiProcessor extends Module {

    protected MidiEventFetcher midiEventFetcher;

    public MidiProcessor(MidiEventFetcher midiEventFetcher, int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);
        this.midiEventFetcher = midiEventFetcher;
    }

    public abstract void midi(MidiEvent midiEvent);

}
