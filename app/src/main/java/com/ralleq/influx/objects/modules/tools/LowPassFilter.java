package com.ralleq.influx.objects.modules.tools;

public class LowPassFilter {

    private double value, velocity, acceleration = 0.01f, dampening = 0.95f;

    public LowPassFilter() {

    }

    public int generateNextSample(int sample) {
        velocity += (sample - value)*acceleration;
        value += velocity;
        velocity *= dampening;
        return (int)value;
    }

    public void setCutoff(double acceleration) {
        this.acceleration = Math.pow(acceleration, 2);
    }

    public void setResonance(double dampening) {
        this.dampening = Math.max(dampening*0.96, 0);
    }
}
