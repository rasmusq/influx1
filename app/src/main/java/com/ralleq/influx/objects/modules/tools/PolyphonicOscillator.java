package com.ralleq.influx.objects.modules.tools;

import android.util.Log;

import com.ralleq.influx.midi.MidiEvent;

public class PolyphonicOscillator {

    private Oscillator[] oscillators;
    private Oscillator lastPlayedOscillator;
    private boolean modulator;
    private double waveform, amplitude = 1, pitchBend, pitchBendScalar = 12;

    public PolyphonicOscillator(int oscillatorAmount, boolean modulator) {
        this.modulator = modulator;
        oscillators = new Oscillator[oscillatorAmount];
        for(int i = 0; i < oscillators.length; i++) {
            oscillators[i] = new Oscillator(modulator, modulator);
        }

    }

    public int generateNextSample(PolyphonicOscillator modulator, double modulationInfluence) {
        double modulation = 0;
        int cumulativeOutput = 0;
        for(int i = 0; i < oscillators.length; i++) {
            if(modulator != null) {
                modulation = modulator.getOscillators()[i].getLastOutputSample()*modulationInfluence;
            }
            cumulativeOutput += oscillators[i].generateSample(pitchBend, modulation, waveform, amplitude);
        }
        return cumulativeOutput;
    }

    private int nextOscillator = 0;
    public void midi(MidiEvent midiEvent) {
        if(midiEvent.getAction() == MidiEvent.ACTION_NOTE_OFF) {
            for(int i = 0; i < oscillators.length; i++) {
                if(oscillators[i].isPlaying() &&
                        oscillators[i].getLastPlayMidiEvent().getNoteIndex() == midiEvent.getNoteIndex()) {
                    oscillators[i].stop(midiEvent);
                }
            }
        }
        if(midiEvent.getAction() == MidiEvent.ACTION_NOTE_ON) {
            int tryCount = 0;
            do {
                if(!oscillators[nextOscillator].isPlaying()) {
                    oscillators[nextOscillator].play(midiEvent);
                    oscillators[nextOscillator].setNote(midiEvent.getNoteIndex());
                    lastPlayedOscillator = oscillators[nextOscillator];
                } else {
                    nextOscillator++;
                    if(nextOscillator >= oscillators.length)
                        nextOscillator = 0;
                }
                tryCount++;
            } while(oscillators[nextOscillator].isPlaying() && tryCount < oscillators.length);
        }
    }

    public void setWaveform(double waveform) {
        this.waveform = waveform;
    }
    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }
    public void setPitchBend(double pitchBend) {
        this.pitchBend = (pitchBend-0.5)*2.0*pitchBendScalar;
    }
    public double getPitchBend() {
        return pitchBend;
    }

    public void setFilterCutoff(double cutoff) {
        for(Oscillator oscillator: oscillators) {
            oscillator.setStaticCutoff(cutoff);
        }
    }
    public void setFilterResonance(double resonance) {
        for(Oscillator oscillator: oscillators) {
            oscillator.getLowPassFilter().setResonance(resonance);
        }
    }
    public void setEnvelopeAttack(double attack) {
        for(Oscillator oscillator: oscillators) {
            oscillator.getEnvelope().setAtk(attack);
        }
    }
    public void setEnvelopeDecay(double decay) {
        for(Oscillator oscillator: oscillators) {
            oscillator.getEnvelope().setDcy(decay);
        }
    }
    public void setEnvelopeSustain(double sustain) {
        for(Oscillator oscillator: oscillators) {
            oscillator.getEnvelope().setStn(sustain);
        }
    }
    public void setEnvelopeRelease(double release) {
        for(Oscillator oscillator: oscillators) {
            oscillator.getEnvelope().setRls(release);
        }
    }

    public Oscillator[] getOscillators() {
        return oscillators;
    }
}
