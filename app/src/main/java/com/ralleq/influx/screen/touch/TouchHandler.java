package com.ralleq.influx.screen.touch;

import android.view.MotionEvent;

public abstract class TouchHandler {

    private TouchPointer[] touchPointers;
    private final int maxPointers = 10;

    public TouchHandler() {
        touchPointers = new TouchPointer[maxPointers];
        for(int i = 0; i < touchPointers.length; i++) {
            touchPointers[i] = new TouchPointer();
        }
    }

    public abstract void fetchTouchEvent(TouchEvent touchEvent);
    public void receiveMotionEvent(MotionEvent motionEvent) {
        int action = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        int id = motionEvent.getPointerId(actionIndex);

        //Update the active pointer
        if(id < touchPointers.length) {
            if(action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_DOWN) {
                updatePointerCoordinates(id, motionEvent);
                touchPointers[id].setDown(true);
            }
            if(action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP) {
                updatePointerCoordinates(id, motionEvent);
                touchPointers[id].setDown(false);
            }
        }

        //Update coordinates for all pointers
        for(int i = 0; i < motionEvent.getPointerCount(); i++) {
            updatePointerCoordinates(i, motionEvent);
        }

        fetchTouchEvent(new TouchEvent(touchPointers, id, motionEvent));
    }

    public void updatePointerCoordinates(int index, MotionEvent motionEvent) {
        //Pointers: index is the place in the array, id is the tracked finger
        if(index < motionEvent.getPointerCount()) {
            int id = motionEvent.getPointerId(index);
            double currentPointerX = motionEvent.getX(index),
                    currentPointerY = motionEvent.getY(index),
                    previousPointerX = touchPointers[id].getPosition().getX(),
                    previousPointerY = touchPointers[id].getPosition().getY();
            touchPointers[id].getVelocity().set(currentPointerX-previousPointerX, currentPointerY-previousPointerY);
            touchPointers[id].getPosition().set(currentPointerX, currentPointerY);
            touchPointers[id].getPreviousPosition().set(previousPointerX, previousPointerY);
            touchPointers[id].addTravelX(Math.abs(currentPointerX-previousPointerX));
            touchPointers[id].addTravelY(Math.abs(currentPointerY-previousPointerY));
        }
    }

    public TouchPointer[] getTouchPointers() {
        return touchPointers;
    }

}
