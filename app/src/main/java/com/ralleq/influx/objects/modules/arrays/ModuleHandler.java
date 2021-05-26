package com.ralleq.influx.objects.modules.arrays;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiEventFetcher;
import com.ralleq.influx.objects.modules.ModuleReturner;
import com.ralleq.influx.objects.modules.ModuleReturnerArray;

public class ModuleHandler extends ModuleReturnerArray {

    public static final int
            INDEX_MIDIINTERFACEHANDLER = 0,
            INDEX_INSTRUMENTARRAY = 1,
            INDEX_MODIFIERHANDLER = 2,
            INDEX_EFFECTHANDLER = 3,
            INDEX_RECORDER = 4,
            INDEX_MIXER = 5,
            INDEX_SAMPLES = 6,
            INDEX_OTHER = 7,
            INDEX_METRONOME = 8;

    public ModuleHandler(MidiEventFetcher midiEventFetcher, int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);
        moduleReturners = new ModuleReturner[9];
        moduleReturners[INDEX_MIDIINTERFACEHANDLER] = new MidiInterfaceHandler(midiEventFetcher, location, INDEX_MIDIINTERFACEHANDLER);
        moduleReturners[INDEX_INSTRUMENTARRAY] = new InstrumentArray(location, INDEX_INSTRUMENTARRAY);
        moduleReturners[INDEX_MODIFIERHANDLER] = new ModifierHandler(location, INDEX_MODIFIERHANDLER);
        moduleReturners[INDEX_EFFECTHANDLER] = new EffectHandler(location, INDEX_EFFECTHANDLER);

        moduleReturners[INDEX_RECORDER] = null;
        moduleReturners[INDEX_MIXER] = null;
        moduleReturners[INDEX_SAMPLES] = null;
        moduleReturners[INDEX_OTHER] = null;

        moduleReturners[INDEX_METRONOME] = null;

    }

    //Is sent to the midiProcessorArray to be processed and sent back
    public void baseMidi(MidiEvent midiEvent) {
        moduleReturners[0].midi(midiEvent);
    }

    @Override
    public void midi(MidiEvent midiEvent) {
        for(int i = 1; i < moduleReturners.length; i++) {
            if(moduleReturners[i] != null)
                moduleReturners[i].midi(midiEvent);
        }
    }

    public MidiInterfaceHandler getMidiInterfaceHandler() {
        return (MidiInterfaceHandler)moduleReturners[INDEX_MIDIINTERFACEHANDLER];
    }

}
