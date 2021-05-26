package com.ralleq.influx.screen.touch;

import android.view.MotionEvent;

public class TouchEvent {

    private TouchPointer[] pointers;
    private int activePointerIndex;
    private MotionEvent motionEvent;
    private boolean upEvent, downEvent;

    public TouchEvent(TouchPointer[] pointers, int activePointerIndex, MotionEvent motionEvent) {
        this.pointers = pointers;
        this.activePointerIndex = activePointerIndex;
        this.motionEvent = motionEvent;

        calculateEventDirection();
    }

    private void calculateEventDirection() {
        int upValue = 6;
        for(int i = 0; i < 10; i++) {
            upValue += 256;
            if(motionEvent.getAction() == upValue || motionEvent.getAction() == MotionEvent.ACTION_UP) {
                upEvent = true;
                return;
            }
        }
        int downValue = 5;
        for(int i = 0; i < 10; i++) {
            downValue += 256;
            if(motionEvent.getAction() == downValue || motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                downEvent = true;
            }
        }
    }

    public boolean isUpEvent() {
        return upEvent;
    }

    public boolean isDownEvent() {
        return downEvent;
    }

    public TouchPointer[] getPointers() {
        return pointers;
    }

    public int getActivePointerIndex() {
        return activePointerIndex;
    }
    public TouchPointer getActivePointer() {
        return pointers[activePointerIndex];
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }
}
