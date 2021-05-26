package com.ralleq.influx.midi.external;

import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.util.Log;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiEventFetcher;

import java.util.ArrayList;

public class ExternalMidiReceiver {

    private ArrayList<ExternalMidiDevice> registeredDevices;
    private MidiManager midiManager;

    private MidiDataFetcher midiDataFetcher;

    public ExternalMidiReceiver(Context context, final MidiEventFetcher midiEventFetcher) {
        midiDataFetcher = new MidiDataFetcher() {
            @Override
            public void fetchData(byte[] bytes, int offset, int count, long time, int midiDeviceId, String midiDeviceName) {
                ArrayList<MidiEvent> midiEvents = RawMidiInterpreter.interpretRawMidi(bytes, offset, count, time, midiDeviceId, midiDeviceName);
                for(int i = 0; i < midiEvents.size(); i++) {
                    midiEventFetcher.fetch(midiEvents.get(i));
                }
            }
        };

        registeredDevices = new ArrayList<>();

        midiManager = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
        if(midiManager != null) {
            midiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
                @Override
                public void onDeviceAdded(MidiDeviceInfo info) {
                    super.onDeviceAdded(info);
                    registerDevice(info);
                }
                @Override
                public void onDeviceRemoved(MidiDeviceInfo info) {
                    super.onDeviceRemoved(info);
                    unregisterDevice(info);
                }
            }, null);
            Log.i(getClass().getName(), "MidiManager was initialized");
            for(int i = 0; i < midiManager.getDevices().length; i++) {
                registerDevice(midiManager.getDevices()[i]);
            }
            Log.i(getClass().getName(), midiManager.getDevices().length + " already connected MidiDevices were registered");
        } else {
            Log.e(getClass().getName(), "MidiManager was not initialized. Therefore the app cannot receive external midi input");
        }
    }

    public void registerDevice(final MidiDeviceInfo midiDeviceInfo) {
        midiManager.openDevice(midiDeviceInfo,
                new MidiManager.OnDeviceOpenedListener() {
                    @Override
                    public void onDeviceOpened(MidiDevice midiDevice) {
                        if(midiDevice != null) {
                            registeredDevices.add(new ExternalMidiDevice(midiDeviceInfo, midiDevice, midiDataFetcher));
                            Log.i(getClass().getName(), "MidiDevice was opened and registered from MidiDeviceInfo: " + midiDeviceInfo.toString());
                        } else {
                            Log.e(getClass().getName(), "MidiDevice could not be opened from MidiDeviceInfo: " + midiDeviceInfo.toString());
                        }
                    }
                }, null);
    }
    public void unregisterDevice(final MidiDeviceInfo midiDeviceInfo) {
        for(int i = 0; i < registeredDevices.size(); i++) {
            if(registeredDevices.get(i).getInfo().getId() == midiDeviceInfo.getId()) {
                registeredDevices.remove(i);
                i--;
                Log.i(getClass().getName(), "MidiDeviceHolder was removed because a MidiDevice was disconnected from MidiDeviceInfo: " + midiDeviceInfo.toString());
            }
        }
    }
}
