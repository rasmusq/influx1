package com.ralleq.influx.midi.internal;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiEventFetcher;

import java.util.ArrayList;

public class InternalMidiReceiver {

    private ArrayList<InternalMidiDevice> registeredDevices;

    private MidiEventFetcher deviceMidiEventFetcher;
    private int deviceCounter = 0;

    public InternalMidiReceiver(final MidiEventFetcher midiEventFetcher) {
        deviceMidiEventFetcher = new MidiEventFetcher() {
            @Override
            public void fetch(MidiEvent midiEvent) {
                midiEventFetcher.fetch(midiEvent);
            }
        };

        registeredDevices = new ArrayList<>();

    }

    public void registerDevice(InternalMidiDevice internalMidiDevice) {
        if(internalMidiDevice != null) {
            internalMidiDevice.setMidiEventFetcher(deviceMidiEventFetcher);
            internalMidiDevice.setId(deviceCounter);
            deviceCounter++;
            registeredDevices.add(internalMidiDevice);
        }
    }
    public void unregisterDevice(InternalMidiDevice internalMidiDevice) {
        registeredDevices.remove(internalMidiDevice);
    }

}
