package com.ralleq.influx.objects.modules.tools;

public class Spring {

    private double[] samplePoints, pointVelocities;
    private int sampleResolution = 192;
    private double tension, dampening;

    public Spring() {
        samplePoints = new double[sampleResolution];
        pointVelocities = new double[sampleResolution];
    }

    public int generateNextSample(int inputValue) {
        samplePoints[0] = inputValue;
        for(int i = 1; i < samplePoints.length - 1; i++) {
            pointVelocities[i] += (samplePoints[i-1] - samplePoints[i]) * tension;
            pointVelocities[i] += (samplePoints[i+1] - samplePoints[i]) * tension;
            pointVelocities[i] *= dampening;
        }
        for(int i = 1; i < samplePoints.length - 1; i++) {
            samplePoints[i] += pointVelocities[i];
        }
        return (int) (samplePoints[samplePoints.length-2]);
    }

    public void setTension(double tension) {
        this.tension = tension;
    }

    public void setDampening(double dampening) {
        this.dampening = dampening;
    }
}
