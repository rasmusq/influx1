package com.ralleq.influx.midi;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MidiDataKeeper {

    public static final int keyAmount = 128, controlModeAmount = 128;

    private ArrayList<MidiEvent> midiNotesOn;
    private boolean[] notesOn;
    private long[] lastNoteOnTime;
    private double[] velocities;
    private double pitchBend = 0.5;
    private double[] controlModeValues;
    private double[] controlModeSpeeds;

    public MidiDataKeeper() {
        midiNotesOn = new ArrayList<>();
        notesOn = new boolean[keyAmount];
        lastNoteOnTime = new long[keyAmount];
        velocities = new double[keyAmount];
        
        controlModeValues = new double[controlModeAmount];
        controlModeSpeeds = new double[controlModeAmount];
    }

    public void receiveMidiEvent(MidiEvent midiEvent) {
        midiEvent.setMidiDataKeeper(this);

        if(midiEvent.getAction() == MidiEvent.ACTION_NOTE_ON) {
            if(midiEvent.getNoteVelocity() == 0) {
                //A note velocity of 0 should be interpreted as an off signal
                setNoteOff(midiEvent);
            } else {
                setNoteOn(midiEvent);
            }
            velocities[midiEvent.getNoteIndex()] = midiEvent.getNoteVelocity();
        } else if(midiEvent.getAction() == MidiEvent.ACTION_NOTE_OFF) {
            setNoteOff(midiEvent);
            velocities[midiEvent.getNoteIndex()] = midiEvent.getNoteVelocity();
        } else if(midiEvent.getAction() == MidiEvent.ACTION_PITCH_BEND) {
            pitchBend = midiEvent.getPitchBendValue();
        } else if(midiEvent.getAction() == MidiEvent.ACTION_CONTROL_MODE_CHANGE) {
            double newControlModeValue = midiEvent.getControlModeValue();
            controlModeSpeeds[midiEvent.getControlModeIndex()] =
                    newControlModeValue - controlModeValues[midiEvent.getControlModeIndex()];
            controlModeValues[midiEvent.getControlModeIndex()] = newControlModeValue;
        }
    }
    public void setNoteOn(MidiEvent midiEvent) {
        notesOn[midiEvent.getNoteIndex()] = true;
        lastNoteOnTime[midiEvent.getNoteIndex()] = System.nanoTime();
        midiNotesOn.add(midiEvent);
    }
    public void setNoteOff(MidiEvent midiEvent) {
        notesOn[midiEvent.getNoteIndex()] = false;
        removeAllMidiOnEventsWithIndex(midiEvent.getNoteIndex());
    }
    public void removeAllMidiOnEventsWithIndex(int index) {
        for(int i = 0; i < midiNotesOn.size(); i++) {
            if(midiNotesOn.get(i).getNoteIndex() == index) {
                midiNotesOn.remove(i);
                i--;
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ON Notes: [");
        int onNoteCount = 0;
        for(int i = 0; i < notesOn.length; i++) {
            if(notesOn[i]) {
                if(onNoteCount != 0) {
                    stringBuilder.append(", ");
                }
                String toAppend = MidiEvent.getFlatNoteName(i) +
                        ": Velocity(" + MidiEvent.doubleToByte(velocities[i]) +
                        "), Time(" + lastNoteOnTime[i] + ")";
                stringBuilder.append(toAppend);
                onNoteCount++;
            }
        }
        stringBuilder.append("], Pitch Bend: [");
        stringBuilder.append(pitchBend);
        stringBuilder.append("], Control Mode Values: [");
        int controlModeCount = 0;
        for(int i = 0; i < controlModeValues.length; i++) {
            if(controlModeValues[i] != 0.0) {
                if(controlModeCount != 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(controlModeValues[i]);
                controlModeCount++;
            }
        }
        stringBuilder.append("];");
        return stringBuilder.toString();
    }

    public ArrayList<MidiEvent> getMidiNotesOn() {
        return midiNotesOn;
    }

    public boolean[] getNotesOn() {
        return notesOn;
    }

    public long[] getLastNoteOnTime() {
        return lastNoteOnTime;
    }

    public double[] getVelocities() {
        return velocities;
    }

    public double getPitchBend() {
        return pitchBend;
    }

    public double[] getControlModeValues() {
        return controlModeValues;
    }

    public double[] getControlModeSpeeds() {
        return controlModeSpeeds;
    }
}
