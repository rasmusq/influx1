package com.ralleq.influx.midi.internal;

import android.util.Log;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiEventFetcher;

public abstract class InternalMidiDevice {

    private MidiEventFetcher midiEventFetcher;
    private int id = -1;

    protected abstract String getName();

    public void sendMidiEvent(MidiEvent midiEvent) {
        if(midiEventFetcher != null) {
            midiEventFetcher.fetch(midiEvent);
        } else {
            Log.e(getClass().getName(), "Could not send fetch MidiEvent, because the MidiEventFetcher is NULL");
        }
    }

    public void setMidiEventFetcher(MidiEventFetcher midiEventFetcher) {
        this.midiEventFetcher = midiEventFetcher;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
