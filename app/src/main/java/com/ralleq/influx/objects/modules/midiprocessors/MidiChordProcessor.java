package com.ralleq.influx.objects.modules.midiprocessors;

import android.graphics.Canvas;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiEventFetcher;
import com.ralleq.influx.screen.touch.TouchEvent;

public class MidiChordProcessor extends MidiProcessor {

    private final int maxChordNotes = 8;
    private int[] notes;

    public MidiChordProcessor(MidiEventFetcher midiEventFetcher, int[] parentLocation, int newLocation) {
        super(midiEventFetcher, parentLocation, newLocation);

        notes = new int[maxChordNotes];
        notes = new int[] {3, 5, 7, 9, -1, -1, -1, -1};
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void touch(TouchEvent touchEvent) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void midi(MidiEvent midiEvent) {
        midiEventFetcher.fetch(midiEvent);
        if(midiEvent.isNote()) {
            for(int i = 0; i < notes.length && notes[i] != -1; i++) {
                midiEventFetcher.fetch(MidiEvent.replicateNoteEventAtOtherIndex(
                        midiEvent, midiEvent.getNoteIndex() + notes[i]));
            }
        }
    }



}
