package com.ralleq.influx.midi.external;

import com.ralleq.influx.midi.MidiEvent;

import java.util.ArrayList;

public class RawMidiInterpreter {

    public static ArrayList<MidiEvent> interpretRawMidi(byte[] bytes, int offset, int count, long time, int midiDeviceId, String midiDeviceName) {
        int byteCounter = 1;    //The first byte is a padding 1
        ArrayList<MidiEvent> midiEvents = new ArrayList<>();
        while(byteCounter < count+1) {
            int action = (bytes[byteCounter]&0b11110000)>>4;
            int channel = bytes[byteCounter]&0b00001111;
            byte[] parameters = new byte[2];

            if(action == MidiEvent.ACTION_NOTE_OFF) {
                byteCounter++;
                parameters[0] = bytes[byteCounter];
                byteCounter++;
                parameters[1] = bytes[byteCounter];
            } else if(action == MidiEvent.ACTION_NOTE_ON) {
                byteCounter++;
                parameters[0] = bytes[byteCounter];
                byteCounter++;
                parameters[1] = bytes[byteCounter];
            } else if(action == MidiEvent.ACTION_POLYPHONIC_AFTERTOUCH) {
                byteCounter++;
                parameters[0] = bytes[byteCounter];
                byteCounter++;
                parameters[1] = bytes[byteCounter];
            } else if(action == MidiEvent.ACTION_CONTROL_MODE_CHANGE) {
                byteCounter++;
                parameters[0] = bytes[byteCounter];
                byteCounter++;
                parameters[1] = bytes[byteCounter];
            } else if(action == MidiEvent.ACTION_PROGRAM_CHANGE) {
                byteCounter++;
                parameters[0] = bytes[byteCounter];
            } else if(action == MidiEvent.ACTION_CHANNEL_AFTERTOUCH) {
                byteCounter++;
                parameters[0] = bytes[byteCounter];
            } else if(action == MidiEvent.ACTION_PITCH_BEND) {
                byteCounter++;
                parameters[0] = bytes[byteCounter];
                byteCounter++;
                parameters[1] = bytes[byteCounter];
            } else if(action == MidiEvent.ACTION_OTHER) {
                //Nothing useful
            }

            midiEvents.add(new MidiEvent(action, channel, parameters, midiDeviceId, midiDeviceName));

            byteCounter++;
        }
        return midiEvents;
    }

}
