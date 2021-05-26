package com.ralleq.influx.objects;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.ralleq.influx.MainActivity;
import com.ralleq.influx.files.ColorHandler;
import com.ralleq.influx.midi.MidiDataKeeper;
import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiEventFetcher;
import com.ralleq.influx.objects.mididevices.Piano;
import com.ralleq.influx.objects.modules.arrays.ModuleHandler;
import com.ralleq.influx.objects.modules.midiinterpreter.MidiInterpreter;
import com.ralleq.influx.screen.touch.TouchEvent;

public class MainHandler {

    private RectF bounds;
    private float pianoTopFraction = 0.6f;

    public static Piano piano;
    public static ModuleHandler moduleHandler;

    private MidiEventFetcher processedMidiEventFetcher;
    private MidiDataKeeper processedMidiDataKeeper;

    public MainHandler(MidiEventFetcher baseMidiEventFetcher) {
        bounds = new RectF();

        processedMidiDataKeeper = new MidiDataKeeper();
        processedMidiEventFetcher = new MidiEventFetcher() {
            @Override
            public void fetch(MidiEvent midiEvent) {
                processedMidiDataKeeper.receiveMidiEvent(midiEvent);
                processedMidi(midiEvent);
            }
        };

        piano = new Piano();
        piano.setMidiEventFetcher(baseMidiEventFetcher);
        moduleHandler = new ModuleHandler(processedMidiEventFetcher, null, 0);
        load();
    }

    public void tick() {

    }
    public void draw(Canvas canvas) {
        canvas.drawColor(ColorHandler.bgColor);
        piano.draw(canvas);

    }
    public void touch(TouchEvent touchEvent) {
        piano.touch(touchEvent);
    }
    public void updateBounds(float width, float height) {
        bounds.set(0, 0, width, height);
        piano.updateBounds(bounds.left, bounds.top + pianoTopFraction*bounds.height(), bounds.right, bounds.bottom);
    }
    public void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame) {

    }

    public void baseMidi(MidiEvent midiEvent) {
        moduleHandler.baseMidi(midiEvent);
    }

    public void processedMidi(MidiEvent midiEvent) {
        piano.processedMidi(midiEvent);
        moduleHandler.midi(midiEvent);
    }

    public void save() {
        moduleHandler.save("currentProject_");
    }
    public void load() {
        moduleHandler.load("currentProject_");
    }

    public Piano getPiano() {
        return piano;
    }
    public ModuleHandler getModuleHandler() {
        return moduleHandler;
    }

}
