package com.ralleq.influx.objects.modules.arrays;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.objects.modules.ModuleReturner;
import com.ralleq.influx.objects.modules.ModuleReturnerArray;
import com.ralleq.influx.objects.modules.effects.NothingEffect;
import com.ralleq.influx.objects.modules.effects.SpringEffect;

public class EffectArray extends ModuleReturnerArray {

    public EffectArray(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);

        moduleReturners = new ModuleReturner[2];
        moduleReturners[0] = new NothingEffect(location, 0);
        moduleReturners[1] = new SpringEffect(location, 1);

    }

    @Override
    public void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame) {
        getSelectedModuleReturner().getModule().audio(inLeft, inRight, outLeft, outRight, currentFrame);
    }
    @Override
    public void midi(MidiEvent midiEvent) {
        getSelectedModuleReturner().getModule().midi(midiEvent);
    }

}
