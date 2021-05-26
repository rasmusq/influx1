package com.ralleq.influx.midi;

import android.util.Log;

import androidx.annotation.NonNull;

public class MidiEvent {

    public static MidiEvent createEvent(int action, int channel, byte[] parameters, int midiDeviceId, String midiDeviceName) {
        return new MidiEvent(action, channel, parameters, midiDeviceId, midiDeviceName);
    }
    public static MidiEvent createEvent(int action, int channel, byte firstByte, byte secondByte, int midiDeviceId, String midiDeviceName) {
        return new MidiEvent(action, channel, new byte[] {firstByte, secondByte}, midiDeviceId, midiDeviceName);
    }
    public static MidiEvent createNoteOffEvent(int channel, int noteIndex, double noteVelocity, int midiDeviceId, String midiDeviceName) {
        return new MidiEvent(ACTION_NOTE_OFF,
                channel, new byte[] {(byte)noteIndex, doubleToByte(noteVelocity)}, midiDeviceId, midiDeviceName);
    }
    public static MidiEvent createNoteOnEvent(int channel, int noteIndex, double noteVelocity, int midiDeviceId, String midiDeviceName) {
        return new MidiEvent(ACTION_NOTE_ON,
                channel, new byte[] {(byte)noteIndex, doubleToByte(noteVelocity)}, midiDeviceId, midiDeviceName);
    }
    public static MidiEvent createPolyAftertouchEvent(int channel, int noteIndex, double notePressure, int midiDeviceId, String midiDeviceName) {
        return new MidiEvent(ACTION_POLYPHONIC_AFTERTOUCH,
                channel, new byte[] {(byte)noteIndex, doubleToByte(notePressure)}, midiDeviceId, midiDeviceName);
    }
    public static MidiEvent createControlModeEvent(int channel, double modeValue, int modeIndex, int midiDeviceId, String midiDeviceName) {
        return new MidiEvent(ACTION_CONTROL_MODE_CHANGE,
                channel, new byte[] {doubleToByte(modeValue), (byte)modeIndex}, midiDeviceId, midiDeviceName);
    }
    public static MidiEvent createProgramChangeEvent(int channel, int programIndex, int midiDeviceId, String midiDeviceName) {
        return new MidiEvent(ACTION_PROGRAM_CHANGE,
                channel, new byte[] {(byte)programIndex, 0}, midiDeviceId, midiDeviceName);
    }
    public static MidiEvent createChannelAftertouchEvent(int channel, double pressure, int midiDeviceId, String midiDeviceName) {
        return new MidiEvent(ACTION_CHANNEL_AFTERTOUCH,
                channel, new byte[] {doubleToByte(pressure), 0}, midiDeviceId, midiDeviceName);
    }
    public static MidiEvent createPitchBendEvent(int channel, double value, int midiDeviceId, String midiDeviceName) {
        return new MidiEvent(ACTION_PITCH_BEND,
                channel, doubleToTwoBytes(value), midiDeviceId, midiDeviceName);
    }

    public static MidiEvent replicateNoteEventAtOtherIndex(MidiEvent noteEvent, int otherIndex) {
        return new MidiEvent(noteEvent.getAction(), noteEvent.getChannel(),
                new byte[] {(byte)otherIndex, noteEvent.getNoteVelocityInBytes()}, noteEvent.getMidiDeviceId(), noteEvent.getMidiDeviceName());
    }

    public static final String[] sharpNoteNames = new String[] {
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    };
    public static final String[] flatNoteNames = new String[] {
            "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"
    };

    public static final int
            ACTION_NOTE_OFF = 8,
            ACTION_NOTE_ON = 9,
            ACTION_POLYPHONIC_AFTERTOUCH = 10,
            ACTION_CONTROL_MODE_CHANGE = 11,
            ACTION_PROGRAM_CHANGE = 12,
            ACTION_CHANNEL_AFTERTOUCH = 13,
            ACTION_PITCH_BEND = 14,
            ACTION_OTHER = 15;

    public static final double pitchBendSnapMargin = 0.01;

    private int action, channel;
    private byte[] parameters;
    private int[] intParameters;
    private int midiDeviceId;
    private String midiDeviceName;
    private MidiDataKeeper midiDataKeeper;

    public MidiEvent(int action, int channel, byte[] parameters, int midiDeviceId, String midiDeviceName) {
        this.action = action;
        this.channel = channel;
        this.parameters = parameters;
        intParameters = new int[parameters.length];
        for(int i = 0; i < parameters.length; i++) {
            intParameters[i] = (int)parameters[i];
        }
        this.midiDeviceId = midiDeviceId;
        this.midiDeviceName = midiDeviceName;
    }

    //Interpreters

    public int getNoteIndex() {
        return parameters[0];
    }
    public double getNoteVelocity() {
        return byteToDouble(parameters[1]);
    }
    public byte getNoteVelocityInBytes() { return parameters[1]; }

    public int getControlModeIndex() {
        return parameters[0];
    }
    public double getControlModeValue() {
        return byteToDouble(parameters[1]);
    }

    public int getPolyAftertouchPressure() {
        return parameters[1];
    }

    public int getProgramIndex() {
        return parameters[0];
    }

    public int getChannelAftertouchPressure() {
        return parameters[0];
    }

    public byte getPitchBendLSB() {
        return parameters[0];
    }
    public byte getPitchBendMSB() {
        return parameters[1];
    }
    public double getPitchBendValue() {
        return twoBytesToDouble(getPitchBendLSB(), getPitchBendMSB());
    }

    //Converters
    public static double twoBytesToDouble(byte firstByte, byte secondByte) {
        double value = (secondByte / 127.0) + (firstByte / 16129.0);
        if(value > 1-pitchBendSnapMargin)
            value = 1;
        else if(value < 0+pitchBendSnapMargin)
            value = 0;
        else if(value > 0.5-pitchBendSnapMargin && value < 0.5+pitchBendSnapMargin) {
            value = 0.5;
        }
        return value;
    }
    public static byte[] doubleToTwoBytes(double value) {
        double smallValue = value%(1.0 / 127.0);
        double bigValue = value-smallValue;
        byte smallByteValue = (byte) (smallValue/(1.0 / 127.0) * 127.0);
        byte bigByteValue = (byte) (bigValue * 127.0);
        return new byte[] {smallByteValue, bigByteValue};
    }

    public static double byteToDouble(byte noteVelocity) {
        return noteVelocity / 127.0;
    }
    public static byte doubleToByte(double noteVelocity) {
        return (byte) (noteVelocity*127.0);
    }

    public static String getFlatNoteName(int noteIndex) {
        return flatNoteNames[(noteIndex-60+127)%flatNoteNames.length] + ((noteIndex-60)/flatNoteNames.length);
    }
    public static String getSharpNoteNames(int noteIndex) {
        return sharpNoteNames[(noteIndex-60+127)%sharpNoteNames.length] + ((noteIndex-60)/sharpNoteNames.length);
    }
    //Getters and setters

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public byte[] getParameters() {
        return parameters;
    }

    public int[] getIntParameters() {
        return intParameters;
    }

    public void setParameters(byte[] parameters) {
        this.parameters = parameters;
    }

    public int getMidiDeviceId() {
        return midiDeviceId;
    }

    public void setMidiDeviceId(int midiDeviceId) {
        this.midiDeviceId = midiDeviceId;
    }

    public String getMidiDeviceName() {
        return midiDeviceName;
    }

    public void setMidiDeviceName(String midiDeviceName) {
        this.midiDeviceName = midiDeviceName;
    }

    public boolean isNote() {
        return (action == ACTION_NOTE_ON) || (action == ACTION_NOTE_OFF);
    }

    public MidiDataKeeper getMidiDataKeeper() {
        return midiDataKeeper;
    }

    public void setMidiDataKeeper(MidiDataKeeper midiDataKeeper) {
        this.midiDataKeeper = midiDataKeeper;
    }

    //To String

    @NonNull
    @Override
    public String toString() {
        
        String string = "MidiEvent: UNKNOWN";
        if(action == ACTION_NOTE_OFF) {
            string = "MidiEvent: NOTE OFF [Note: " + getNoteIndex() + ", Velocity: " + getNoteVelocity() + "]";
        } else if(action == ACTION_NOTE_ON) {
            string = "MidiEvent: NOTE ON [Note: " + getNoteIndex() + ", Velocity: " + getNoteVelocity() + "]";
        } else if(action == ACTION_POLYPHONIC_AFTERTOUCH) {
            string = "MidiEvent: POLYPHONIC AFTERTOUCH [Note: " + getNoteIndex() + ", Pressure: " + getPolyAftertouchPressure() + "]";
        } else if(action == ACTION_CONTROL_MODE_CHANGE) {
            string = "MidiEvent: CONTROL MODE CHANGE [Index: " + getControlModeIndex() + ", Value: " + getControlModeValue() + "]";
        } else if(action == ACTION_PROGRAM_CHANGE) {
            string = "MidiEvent: PROGRAM CHANGE [Index: " + getProgramIndex() + "]";
        } else if(action == ACTION_CHANNEL_AFTERTOUCH) {
            string = "MidiEvent: CHANNEL AFTERTOUCH [Pressure: " + getChannelAftertouchPressure() + "]";
        } else if(action == ACTION_PITCH_BEND) {
            string = "MidiEvent: PITCH BEND [Value: " + getPitchBendValue() + "]";
        } else if(action == ACTION_OTHER) {
            string = "MidiEvent: OTHER";
        }

        string += " @ Channel: " + channel + ", From MidiDevice with ID: " + midiDeviceId + ", and name: " + midiDeviceName;

        return string;
    }
}
