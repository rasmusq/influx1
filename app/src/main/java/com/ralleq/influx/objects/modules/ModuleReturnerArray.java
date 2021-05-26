package com.ralleq.influx.objects.modules;

import android.graphics.Canvas;

import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.objects.controlknobs.KnobEvent;
import com.ralleq.influx.screen.touch.TouchEvent;

public abstract class ModuleReturnerArray extends ModuleReturner {

    protected static ModuleReturnerSelector selector;
    static {
        selector = new ModuleReturnerSelector();
    }

    protected ModuleReturner[] moduleReturners;
    protected int selectedModuleReturnerIndex = 0;
    public ModuleReturner getSelectedModuleReturner() {
        return moduleReturners[selectedModuleReturnerIndex];
    }
    public int getSelectedModuleReturnerIndex() {
        return selectedModuleReturnerIndex;
    }
    public ModuleReturner[] getModuleReturners() {
        return moduleReturners;
    }
    public ModuleReturner getModuleReturner(int index) { return moduleReturners[index]; }

    public ModuleReturnerArray(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);
    }

    @Override
    public Module getModule() {
        return getSelectedModuleReturner().getModule();
    }
    @Override
    public String getName() {
        return "No Name Array";
    }

    @Override
    public void knob(KnobEvent knobEvent) {
        getSelectedModuleReturner().getModule().knob(knobEvent);
    }

    @Override
    public void draw(Canvas canvas) {
        getSelectedModuleReturner().getModule().draw(canvas);
        selector.draw(canvas);
    }

    @Override
    public void touch(TouchEvent touchEvent) {
        if(!selector.isSelecting())
            getSelectedModuleReturner().getModule().touch(touchEvent);
        selector.touch(touchEvent);
    }

    @Override
    public void tick() {
        for(ModuleReturner moduleReturner: moduleReturners) {
            if(moduleReturner != null)
                moduleReturner.getModule().tick();
        }
        selector.tick();
    }

    @Override
    public void midi(MidiEvent midiEvent) {
        for(ModuleReturner moduleReturner: moduleReturners) {
            if(moduleReturner != null)
                moduleReturner.midi(midiEvent);
        }
    }
    @Override
    public void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame) {
        for(ModuleReturner moduleReturner: moduleReturners) {
            if(moduleReturner != null)
                moduleReturner.audio(inLeft, inRight, outLeft, outRight, currentFrame);
        }
    }

    @Override
    public void updateBounds(float left, float top, float right, float bottom) {
        for(ModuleReturner moduleReturner: moduleReturners) {
            if(moduleReturner != null)
                moduleReturner.updateBounds(left, top, right, bottom);
        }
        selector.updateBounds(left, top, right, bottom);
    }

    @Override
    public void save(String savePath) {
        for(int i = 0; i < moduleReturners.length; i++) {
            if(moduleReturners[i] != null)
                moduleReturners[i].save(savePath + "moduleReturnerArray"+i+"_");
        }
    }
    @Override
    public void load(String savePath) {
        for(int i = 0; i < moduleReturners.length; i++) {
            if(moduleReturners[i] != null)
                moduleReturners[i].load(savePath + "moduleReturnerArray"+i+"_");
        }
    }

    public void nextModuleIndex() {
        if(selectedModuleReturnerIndex+1 >= moduleReturners.length)
            selectedModuleReturnerIndex = 0;
        else
            selectedModuleReturnerIndex++;
    }
    public void setSelectedModuleReturnerIndex(int selectedModuleReturnerIndex) {
        this.selectedModuleReturnerIndex = selectedModuleReturnerIndex;
    }

}
