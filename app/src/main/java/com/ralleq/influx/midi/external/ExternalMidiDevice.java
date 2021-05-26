package com.ralleq.influx.midi.external;

import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;

import java.io.IOException;

public class ExternalMidiDevice {

    private MidiDeviceInfo info;
    private MidiDevice device;
    private int midiDeviceId;
    private String midiDeviceName;

    private MidiInputPort[] inputPorts;
    private MidiOutputPort[] outputPorts;
    private MidiReceiver midiReceiver;

    public ExternalMidiDevice(MidiDeviceInfo info, MidiDevice device, final MidiDataFetcher dataFetcher) {
        this.info = info;
        this.device = device;
        midiDeviceId = info.getId();
        midiDeviceName = info.getProperties().getString("name", "MidiDeviceInfo 'name' key was not found");

        midiReceiver = new MidiReceiver() {
            @Override
            public void onSend(byte[] bytes, int offset, int count, long time) throws IOException {
                dataFetcher.fetchData(bytes, offset, count, time, midiDeviceId, midiDeviceName);
            }
        };
        inputPorts = new MidiInputPort[info.getInputPortCount()];
        outputPorts = new MidiOutputPort[info.getOutputPortCount()];
        for(int i = 0; i < inputPorts.length; i++) {
            inputPorts[i] = device.openInputPort(i);
        }
        for(int i = 0; i < outputPorts.length; i++) {
            outputPorts[i] = device.openOutputPort(i);
            outputPorts[i].connect(midiReceiver);
        }
    }

    public MidiDeviceInfo getInfo() {
        return info;
    }

    public MidiDevice getDevice() {
        return device;
    }
}
