package com.ralleq.influx.objects.modules.tools;

import com.ralleq.influx.MainActivity;
import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.objects.MainHandler;

public class Oscillator {

    private boolean playing, stopping;
    private double positionInWave, waveSpeed, note;
    private double cutoff, envelopeFilterControl;
    private int lastOutputSample;
    private boolean enveloped, filtered;
    private Envelope envelope;
    private LowPassFilter lowPassFilter;


    private MidiEvent lastPlayMidiEvent, lastStopMidiEvent;

    public Oscillator(boolean filtered, boolean enveloped) {
        this.filtered = filtered;
        this.enveloped = enveloped;
        lowPassFilter = new LowPassFilter();
        envelope = new Envelope();
    }

    public int generateSample(double pitchBend, double modulation, double waveform, double amplitude) {
        setNote(note + pitchBend + modulation);
        positionInWave += waveSpeed;
        double envelopeValue = 1;
        if(enveloped) {
            envelopeValue = envelope.generateNextValue();
        }
        int output = 0;
        double sineAmount = Math.abs(Math.min(waveform, 0.5)*2.0-1.0);
        double sineSample = Math.abs(positionInWave%(Math.PI*2.0) - Math.PI)/Math.PI * Short.MAX_VALUE;
        double sawAmount = Math.abs(Math.abs(waveform-0.5)*2.0-1.0);
        double sawSample = (positionInWave%(Math.PI*2.0) - Math.PI)/Math.PI * Short.MAX_VALUE;
        double squareAmount = (Math.max(waveform, 0.5)-0.5)*2.0;
        double squareSample = Math.round((positionInWave%(Math.PI*2.0))/(Math.PI*2.0)) * Short.MAX_VALUE*2 - Short.MAX_VALUE;
        output = (int) ((sineSample*sineAmount + sawSample*sawAmount + squareSample*squareAmount)/2*amplitude*envelopeValue);
        lastOutputSample = output;
        if(filtered) {
            lowPassFilter.setCutoff(cutoff + envelopeFilterControl*envelopeValue);
            //output = lowPassFilter.generateNextSample(output);
        }
        return output;
    }

    public void play(MidiEvent midiEvent) {
        lastPlayMidiEvent = midiEvent;
        note = midiEvent.getNoteIndex();
        if(!playing)
            positionInWave = 0;
        envelope.restart();
        playing = true;
    }
    public void stop(MidiEvent midiEvent) {
        lastStopMidiEvent = midiEvent;
        envelope.release();
        playing = false;
        stopping = true;
    }
    public void setNote(double note) {
        double frequency = MainHandler.moduleHandler.getMidiInterfaceHandler().getMidiInterpreter().getFrequencyFromNote(note);
        waveSpeed = calculateWaveSpeedWithFrequency(frequency);
    }
    public double calculateWaveSpeedWithFrequency(double frequency) {
        return 1.0/(double)MainActivity.sampleRate*Math.PI*2.0*frequency;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public LowPassFilter getLowPassFilter() {
        return lowPassFilter;
    }

    public void setStaticCutoff(double cutoff) {
        this.cutoff = cutoff;
    }

    public MidiEvent getLastPlayMidiEvent() {
        return lastPlayMidiEvent;
    }

    public MidiEvent getLastStopMidiEvent() {
        return lastStopMidiEvent;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public int getLastOutputSample() {
        return lastOutputSample;
    }
}
