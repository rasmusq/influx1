package com.ralleq.influx.objects.mididevices;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.ralleq.influx.math.Units;
import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.internal.InternalMidiDevice;
import com.ralleq.influx.screen.touch.TouchEvent;
import com.ralleq.influx.screen.touch.TouchPointer;

public class Piano extends InternalMidiDevice {

    protected RectF bounds;
    protected float scroll, unitKeyWidth, keyUnit, totalKeyWidth;
    protected boolean scrolling;

    protected PianoKey[] whiteKeys, blackKeys;

    public Piano() {
        bounds = new RectF();

        initializeKeys();
    }

    public void initializeKeys() {
        int whiteKeyAmount = 16, blackKeyAmount = 11;
        blackKeys = new PianoKey[blackKeyAmount];
        int keyCounter = 1;
        for(int i = 0; i < blackKeys.length; i++) {
            blackKeys[i] = new PianoKey(keyCounter, false, this);
            int indexInOctave = keyCounter%12;
            if(indexInOctave == 3 || indexInOctave == 10) {
                keyCounter += 3;
            } else {
                keyCounter += 2;
            }
        }
        whiteKeys = new PianoKey[whiteKeyAmount];
        keyCounter = 0;
        for(int i = 0; i < whiteKeys.length; i++) {
            whiteKeys[i] = new PianoKey(keyCounter, true, this);
            int indexInOctave = keyCounter%12;
            if(indexInOctave == 4 || indexInOctave == 11) {
                keyCounter += 1;
            } else {
                keyCounter += 2;
            }
        }
    }

    public void processedMidi(MidiEvent midiEvent) {
        for (PianoKey blackKey : blackKeys) {
            blackKey.receiveMidiEvent(midiEvent);
        }
        for (PianoKey whiteKey : whiteKeys) {
            whiteKey.receiveMidiEvent(midiEvent);
        }
    }

    public void touch(TouchEvent touchEvent) {
        boolean downWasInsideBounds = touchEvent.getActivePointer().getLastDownPosition().isInsideRect(bounds);
        if(touchEvent.isUpEvent() && downWasInsideBounds) {
            scrolling = false;
        }
        if(scrolling) {
            float increment = (float)-touchEvent.getActivePointer().getVelocity().getX()*3;
            if(scroll + increment < 0)
                scroll = 0;
            else if(scroll + increment > totalKeyWidth-bounds.width()) {
                scroll = totalKeyWidth-bounds.width();
            } else {
                scroll += increment;
            }
            updateBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
        } else {
            registerKeyTouches(touchEvent);
        }
    }
    public void registerKeyTouches(TouchEvent touchEvent) {
        for(TouchPointer pointer : touchEvent.getPointers()) {
            boolean pointerTaken = false;
            for (PianoKey key : blackKeys) {
                if (pointer.getLastDownPosition().isInsideRect(bounds)
                        && pointer.getPosition().isInsideRect(key.getBounds())
                        && pointer.isDown()) {
                    key.setHasTouchPointerOver(true);
                    pointerTaken = true;
                    break;
                }
            }
            if(pointerTaken)
                continue;
            for (PianoKey key : whiteKeys) {
                if (pointer.getLastDownPosition().isInsideRect(bounds)
                        && pointer.getPosition().isInsideRect(key.getBounds())
                        && pointer.isDown()) {
                    key.setHasTouchPointerOver(true);
                    break;
                }
            }
        }
        //Send off events first
        for (PianoKey blackKey : blackKeys) {
            blackKey.sendMidiOffEvent();
        }
        for (PianoKey whiteKey : whiteKeys) {
            whiteKey.sendMidiOffEvent();
        }
        //Send on events after
        for (PianoKey blackKey : blackKeys) {
            blackKey.sendMidiOnEvent();
        }
        for (PianoKey whiteKey : whiteKeys) {
            whiteKey.sendMidiOnEvent();
        }
        //React visually to touch and more
        for (PianoKey blackKey : blackKeys) {
            blackKey.commitTouch();
        }
        for (PianoKey whiteKey : whiteKeys) {
            whiteKey.commitTouch();
        }
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.clipRect(bounds);
        for(int i = 0; i < whiteKeys.length; i++) {
            whiteKeys[i].draw(canvas);
        }
        for(int i = 0; i < blackKeys.length; i++) {
            blackKeys[i].draw(canvas);
        }
        canvas.restore();
    }
    public void updateBounds(float left, float top, float right, float bottom) {
        //Must overflow a little and get clipped off onDraw();
        bounds.set(left, top, right, bottom);

        //Based on this image: https://images.app.goo.gl/3VVR93giyhQJxWS59
        unitKeyWidth = Units.centimetersToPixels(1.25f);
        keyUnit = unitKeyWidth / 24.0f;
        int blackExtraUnit = 4;

        totalKeyWidth = 0;
        for(int i = 0; i < whiteKeys.length; i++) {
            totalKeyWidth += keyUnit*23 + keyUnit*(i%2);
        }

        float xOffset = bounds.left + keyUnit*(14-blackExtraUnit/2.0f) - scroll;
        for(int i = 0; i < blackKeys.length; i++) {
            int indexInOctave = i%5;
            float blackKeyWidth = keyUnit*(14+blackExtraUnit);
            float rightSpacing;
            if(indexInOctave == 0)
                rightSpacing = keyUnit*(14-blackExtraUnit);
            else if(indexInOctave == 1 || indexInOctave == 4)
                rightSpacing = keyUnit*(14-blackExtraUnit)+keyUnit*13;
            else //if(indexInOctave == 2 || indexInOctave == 3) is always true
                rightSpacing = keyUnit*(13-blackExtraUnit);
            blackKeys[i].updateBounds(xOffset, bounds.top,
                    xOffset+blackKeyWidth, bounds.top + bounds.height()*0.5f);
            xOffset += blackKeyWidth+rightSpacing;
        }
        xOffset = bounds.left - scroll;
        for(int i = 0; i < whiteKeys.length; i++) {
            int indexInOctave = i%8;
            float whiteKeyWidth = keyUnit*23 + keyUnit*(i%2);   //Every other key is 24 keyUnits
            float rightSpacing = 0; //All keys are next to each other
            whiteKeys[i].updateBounds(xOffset, bounds.top,
                    xOffset+whiteKeyWidth, bounds.bottom);
            xOffset += whiteKeyWidth+rightSpacing;
        }
    }

    @Override
    protected String getName() {
        return "Piano";
    }

    public RectF getBounds() {
        return bounds;
    }

    public float getWidthInWhiteKeys() {
        float counter = 0;
        float remainingWidth = bounds.width();
        float keySize = keyUnit*23;
        for(int i = 1; remainingWidth < keySize; i++) {
            counter++;
            remainingWidth -= keySize;
            keySize = keyUnit*23 + keyUnit*(i%2);
        }
        return counter + remainingWidth/keySize;
    }

}
