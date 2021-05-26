package com.ralleq.influx.objects.mididevices;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.ralleq.influx.MainActivity;
import com.ralleq.influx.files.ColorHandler;
import com.ralleq.influx.math.ColorMath;
import com.ralleq.influx.math.Units;
import com.ralleq.influx.midi.MidiEvent;

public class PianoKey {

    protected RectF bounds;
    protected int arrayIndex, noteIndex;
    protected boolean hasTouchPointerOver, down, white;
    protected Path path;
    private float cornerRadius;
    protected Paint paint, bgPaint;

    protected Piano parentPiano;

    protected boolean baseNote, processedNote;

    public PianoKey(int arrayIndex, boolean white, Piano parentPiano) {
        this.arrayIndex = arrayIndex;
        this.noteIndex = arrayIndex+60;
        this.white = white;
        this.parentPiano = parentPiano;
        bounds = new RectF();
        path = new Path();

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(ColorHandler.lnColorDark);
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void receiveMidiEvent(MidiEvent midiEvent) {
        baseNote = MainActivity.midiHandler.getBaseMidiDataKeeper().getNotesOn()[noteIndex];
        processedNote = midiEvent.getMidiDataKeeper().getNotesOn()[noteIndex];
    }

    public void draw(Canvas canvas) {
        float topBuffer = bounds.width();
        float bottomBuffer = bounds.width();
        if(!white) {
            topBuffer = bounds.width()*2;
            bottomBuffer = 0;
        }
        if(down) {
            bgPaint.setColor(ColorHandler.lnColorDark);
            canvas.drawRoundRect(bounds.left, bounds.top - topBuffer, bounds.right, bounds.bottom + bottomBuffer,
                    cornerRadius, cornerRadius, bgPaint);
        } else if(baseNote) {
            bgPaint.setColor(ColorMath.colorBetween(ColorHandler.bgColor, ColorHandler.lnColorDark, 0.25f));
            canvas.drawRoundRect(bounds.left, bounds.top - topBuffer, bounds.right, bounds.bottom + bottomBuffer,
                    cornerRadius, cornerRadius, bgPaint);
        } else if(processedNote) {
            bgPaint.setColor(ColorHandler.bgColor);
            canvas.drawRoundRect(bounds.left, bounds.top - topBuffer, bounds.right, bounds.bottom + bottomBuffer,
                    cornerRadius, cornerRadius, bgPaint);
            bgPaint.setColor(ColorHandler.lnColorDark);
            canvas.drawCircle(bounds.centerX(), bounds.centerY() + bounds.height()/4, cornerRadius, bgPaint);
        } else {
            bgPaint.setColor(ColorHandler.bgColor);
            canvas.drawRoundRect(bounds.left, bounds.top - topBuffer, bounds.right, bounds.bottom + bottomBuffer,
                    cornerRadius, cornerRadius, bgPaint);
        }
        canvas.drawPath(path, paint);
        bgPaint.setColor(ColorHandler.lnColorDark);
    }
    public void updateBounds(float left, float top, float right, float bottom) {
        bounds.set(left, top, right, bottom);
        cornerRadius = bounds.width()/6;
        if(white) {
            recreatePath(left, top, right, bottom + cornerRadius);
        } else {
            recreatePath(left, top, right, bottom);
        }
        paint.setStrokeWidth(Units.globalStrokeWidth);
        paint.setPathEffect(new CornerPathEffect(cornerRadius));
    }
    public void recreatePath(float left, float top, float right, float bottom) {
        path.reset();
        path.moveTo(left, top+Units.globalStrokeWidth/2);
        path.lineTo(left, bottom);
        path.lineTo(right, bottom);
        path.lineTo(right, top+Units.globalStrokeWidth/2);
    }

    public boolean isDown() {
        return down;
    }
    public void setDown(boolean down) {
        this.down = down;
    }
    public void sendMidiOffEvent() {
        if (down && !hasTouchPointerOver) {
            parentPiano.sendMidiEvent(MidiEvent.createNoteOffEvent(
                    0, noteIndex, 0, parentPiano.getId(), parentPiano.getName()));
        }
    }
    public void sendMidiOnEvent() {
        if (!down && hasTouchPointerOver) {
            parentPiano.sendMidiEvent(MidiEvent.createNoteOnEvent(
                    0, noteIndex, 64, parentPiano.getId(), parentPiano.getName()));
        }
    }
    public void commitTouch() {
        down = hasTouchPointerOver;
        hasTouchPointerOver = false;
    }
    public boolean hasTouchPointerOver() {
        return hasTouchPointerOver;
    }
    public void setHasTouchPointerOver(boolean hasTouchPointerOver) {
        this.hasTouchPointerOver = hasTouchPointerOver;
    }

    public RectF getBounds() {
        return bounds;
    }
}
