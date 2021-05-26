package com.ralleq.influx.objects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.ralleq.influx.files.FileHandler;
import com.ralleq.influx.math.Units;
import com.ralleq.influx.rendertools.NumberRenderer;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Value {

    private double baseValue, modifierInfluence = 0.5;

    private int index, location[];
    private String name;

    public abstract void changeValue();
    public Value(String name, int index, int[] location, double defaultValue) {
        this.name = name;
        this.index = index;
        this.location = location;
        baseValue = defaultValue;
    }

    public void save(String path) {
        FileHandler.writeFile(path + name, new String[] {
                Double.toString(baseValue)});
        Log.i("Value", "Value: " + get() + ", at path: " + path + name + ", was saved");
    }
    public void load(String path) {
        ArrayList<String> fileLines = FileHandler.readFile(path + name);
        if(fileLines.size() > 0)
            set(Double.parseDouble(fileLines.get(0)));
        //Log.i("Value", "Value: " + get() + ", at path: " + path + name + ", was loaded");
        Log.i("Value", Arrays.toString(location) + ", " + index + ", " + get());
        //MainActivity.changeValueOfSpecificModule(location, index, get());
    }

    public void move(double move, double bottomLimit, double topLimit) {
        baseValue += move;
        limit(bottomLimit, topLimit);
        changeValue();
    }
    public void audio() {
        changeValue();
    }

    public void drawValue(Canvas canvas, Paint paint, float x, float y, float size) {
        NumberRenderer.renderBigNumber(canvas, paint, (int) (get()*100), x, y+size, size, size);
    }
    public void draw(Canvas canvas, RectF bounds) {
        //To be overridden
    }

    public void set(double newValue) {
        this.baseValue = newValue;
    }
    public void limit(double bottomLimit, double topLimit) {
        baseValue = Units.limit(baseValue, bottomLimit, topLimit);
    }
    public void moveModifierInfluence(double difference) {
        if(modifierInfluence+difference < 0)
            this.modifierInfluence = 0;
        else if(modifierInfluence+difference > 1)
            this.modifierInfluence = 1;
        else
            this.modifierInfluence += difference;
    }

    public double get() {
        return baseValue;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

}
