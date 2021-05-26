package com.ralleq.influx.objects.modules.modifiers;

import com.ralleq.influx.objects.modules.Module;

public abstract class Modifier extends Module {

    protected double outputValue;

    public Modifier(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);

    }

    public double getOutputValue() {
        return outputValue;
    }
}
