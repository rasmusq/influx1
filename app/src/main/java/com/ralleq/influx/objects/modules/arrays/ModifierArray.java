package com.ralleq.influx.objects.modules.arrays;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.objects.modules.ModuleReturner;
import com.ralleq.influx.objects.modules.ModuleReturnerArray;
import com.ralleq.influx.objects.modules.modifiers.EnvelopeModifier;
import com.ralleq.influx.objects.modules.modifiers.Modifier;

public class ModifierArray extends ModuleReturnerArray {

    private double currentModuleOutputValue = 0;
    private Modifier selectedModifier;

    public ModifierArray(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);

        moduleReturners = new ModuleReturner[1];
        moduleReturners[0] = new EnvelopeModifier(location, 0);

        setSelectedModuleReturnerIndex(0);
    }

    @Override
    public void setSelectedModuleReturnerIndex(int selectedModuleReturnerIndex) {
        super.setSelectedModuleReturnerIndex(selectedModuleReturnerIndex);
        selectedModifier = (Modifier) getModule();
    }

    @Override
    public void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame) {
        getModule().audio(inLeft, inRight, outLeft, outRight, currentFrame);
        currentModuleOutputValue = selectedModifier.getOutputValue();
    }
    @Override
    public void midi(MidiEvent midiEvent) {
        getSelectedModuleReturner().getModule().midi(midiEvent);
    }

    public double getCurrentModuleOutputValue() {
        return currentModuleOutputValue;
    }
}
