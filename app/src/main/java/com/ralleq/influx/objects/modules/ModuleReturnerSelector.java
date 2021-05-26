package com.ralleq.influx.objects.modules;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

import com.ralleq.influx.MainActivity;
import com.ralleq.influx.objects.modules.arrays.TextList;
import com.ralleq.influx.screen.touch.TouchEvent;

public class ModuleReturnerSelector {

    private ModuleReturnerArray operatingArray;
    private TextList list;
    private RectF bounds;
    private boolean selecting = false;

    public ModuleReturnerSelector() {
        list = new TextList();
        bounds = new RectF();
    }

    public void initiateSelection(ModuleReturnerArray newArray) {
        operatingArray = newArray;
        list.reset();
        ModuleReturner[] moduleReturners = operatingArray.moduleReturners;
        for(ModuleReturner moduleReturner: moduleReturners) {
            list.addText(moduleReturner.getName());
        }
        list.setSelectedIndex(newArray.getSelectedModuleReturnerIndex());
        selecting = true;
    }
    public void chooseSelection() {
        operatingArray.setSelectedModuleReturnerIndex(list.getSelectedIndex());
        selecting = false;
    }
    public void toggleSelection(ModuleReturnerArray newArray) {
        if(selecting) {
            chooseSelection();
        } else {
            initiateSelection(newArray);
        }
    }

    public void tick() {
        if(selecting) {
            list.tick();
        }
    }
    public void draw(Canvas canvas) {
        if(selecting) {
            list.draw(canvas);
        }
    }
    public void touch(TouchEvent touchEvent) {
        if(selecting) {
            if(touchEvent.getActivePointer().getPosition().isInsideRect(bounds) && touchEvent.isUpEvent() && touchEvent.getActivePointer().lastUpWasTap())
                chooseSelection();
            list.touch(touchEvent);
        }
    }

    public void updateBounds(float left, float top, float right, float bottom) {
        bounds.set(left, top, right, bottom);
        list.updateBounds(left, top, right, bottom);
    }

    public boolean isSelecting() {
        return selecting;
    }
}
