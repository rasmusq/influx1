package com.ralleq.influx.midi;

import android.content.Context;
import android.util.Log;

import com.ralleq.influx.midi.external.ExternalMidiReceiver;
import com.ralleq.influx.midi.internal.InternalMidiReceiver;

public abstract class MidiHandler {

    private ExternalMidiReceiver externalMidiReceiver;
    private InternalMidiReceiver internalMidiReceiver;

    private MidiDataKeeper baseMidiDataKeeper;
    private MidiEventFetcher baseMidiEventFetcher;

    public abstract void fetchMidiEvent(MidiEvent midiEvent);
    public MidiHandler(Context context) {

        //Processing the raw MidiEvents and registering these events
        baseMidiDataKeeper = new MidiDataKeeper();
        baseMidiEventFetcher = new MidiEventFetcher() {
            @Override
            public void fetch(MidiEvent midiEvent) {
                baseMidiDataKeeper.receiveMidiEvent(midiEvent);
                fetchMidiEvent(midiEvent);
            }
        };

        /*
        These classes receive MidiEvents from external and internal sources
        and send these to the baseMidiEventFetcher
         */
        externalMidiReceiver = new ExternalMidiReceiver(context, baseMidiEventFetcher);
        internalMidiReceiver = new InternalMidiReceiver(baseMidiEventFetcher);

    }

    public MidiDataKeeper getBaseMidiDataKeeper() {
        return baseMidiDataKeeper;
    }

    public MidiEventFetcher getBaseMidiEventFetcher() {
        return baseMidiEventFetcher;
    }
}
