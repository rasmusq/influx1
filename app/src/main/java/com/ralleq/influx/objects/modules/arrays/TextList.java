package com.ralleq.influx.objects.modules.arrays;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.ralleq.influx.MainActivity;
import com.ralleq.influx.files.ColorHandler;
import com.ralleq.influx.files.FontHandler;
import com.ralleq.influx.math.Units;
import com.ralleq.influx.screen.touch.TouchEvent;

import java.util.ArrayList;

public class TextList {

    private String bigCharacterText = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private ArrayList<String> texts;
    private RectF bounds, textBoxBounds;
    private Rect bigTextBounds;
    private Paint paint;
    private float scroll, scrollTarget;
    private float scrollBuffer, scrollThreshold;
    private int selectedIndex;

    public TextList() {
        texts = new ArrayList<>();

        bounds = new RectF();
        textBoxBounds = new RectF();
        bigTextBounds = new Rect();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(FontHandler.mainFont);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void reset() {
        texts.clear();
    }
    public void addText(String text) {
        texts.add(text);
    }

    public void tick() {
        scrollTarget = selectedIndex * paint.getTextSize();
        float scrollSpeed = 0.3f;
        scroll += (scrollTarget - scroll)*scrollSpeed;
    }
    public void draw(Canvas canvas) {
        paint.setColor(ColorHandler.bgColor);
        canvas.drawRect(bounds, paint);
        float yOffset = bounds.centerY() - scroll + paint.getTextSize()*0.3666f;
        for(String text: texts) {
            paint.setColor(ColorHandler.lnColorLight);
            canvas.drawText(text, bounds.centerX(), yOffset, paint);
            canvas.drawRoundRect(textBoxBounds, Units.globalCornerSize/4, Units.globalCornerSize/4, paint);
            yOffset += paint.getTextSize();
        }
        yOffset = bounds.centerY() - scroll + paint.getTextSize()*0.3666f;
        for(String text: texts) {
            canvas.save();
            canvas.clipRect(textBoxBounds);
            paint.setColor(ColorHandler.bgColor);
            canvas.drawText(text, bounds.centerX(), yOffset, paint);
            canvas.restore();
            yOffset += paint.getTextSize();
        }
    }
    public void touch(TouchEvent touchEvent) {
        if(touchEvent.getActivePointer().getLastDownPosition().isInsideRect(bounds)) {
            scrollBuffer += touchEvent.getActivePointer().getVelocity().getY();
            if(scrollBuffer > scrollThreshold) {
                MainActivity.vibrationHandler.vibrate(25, 100);
                decreaseIndex();
                Log.i("HI", selectedIndex + "");
                scrollBuffer = 0;
            } else if(scrollBuffer < -scrollThreshold) {
                MainActivity.vibrationHandler.vibrate(25, 100);
                increaseIndex();
                scrollBuffer = 0;
            }
        }
        if(touchEvent.isUpEvent()) {
            scrollBuffer = 0;
        }
    }
    public void increaseIndex() {
        if(selectedIndex+1 >= texts.size()) {
            selectedIndex = 0;
        } else {
            selectedIndex++;
        }
    }
    public void decreaseIndex() {
        if(selectedIndex-1 < 0) {
            selectedIndex = texts.size()-1;
        } else {
            selectedIndex--;
        }
    }

    public void updateBounds(float left, float top, float right, float bottom) {
        bounds.set(left, top, right, bottom);
        paint.setTextSize(Units.globalSmallTextSize);
        paint.getTextBounds(bigCharacterText, 0, bigCharacterText.length(), bigTextBounds);
        textBoxBounds.set(bounds.left, bounds.centerY() - bigTextBounds.height()/2.0f,
                bounds.right, bounds.centerY() + bigTextBounds.height()/2.0f);
        scrollThreshold = Units.centimetersToPixels(0.3f);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
