package com.ralleq.influx.audio;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioRouting;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.Arrays;

public class AudioHandler {

    private AudioTrack.Builder audioTrackBuilder;
    private AudioAttributes outAudioAttributes;
    private AudioFormat outAudioFormat;
    private static final int out_usage = AudioAttributes.USAGE_GAME;
    private static final int out_contentType = AudioAttributes.CONTENT_TYPE_MUSIC;
    private static final int out_encoding = AudioFormat.ENCODING_PCM_16BIT;
    private static final int out_channelMask = AudioFormat.CHANNEL_OUT_DEFAULT;
    private static final int out_performanceMode = AudioTrack.PERFORMANCE_MODE_LOW_LATENCY;
    private static final int out_transferMode = AudioTrack.MODE_STREAM;
    private static AudioTrack audioTrack;

    private AudioRecord.Builder audioRecordBuilder;
    private AudioFormat inAudioFormat;
    private static final int in_encoding = AudioFormat.ENCODING_PCM_16BIT;
    private static final int in_channelMask = AudioFormat.CHANNEL_IN_DEFAULT;
    private static final int in_audioSource = MediaRecorder.AudioSource.DEFAULT;
    private static AudioRecord audioRecord;
    private boolean recordingGranted = false;

    private static AudioManager audioManager;

    private short[] shortInputBuffer, shortOutputBuffer;
    private int[] intInputBuffer, intOutputBuffer;
    public static int bufferSize, sampleRate;
    private static int bufferMultiplier = 1;
    private Thread audioThread;
    private boolean keepLoopAlive = true, playing = true;

    public AudioHandler(Activity activity) {
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        audioManager.registerAudioDeviceCallback(new AudioDeviceCallback() {
            @Override
            public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                super.onAudioDevicesAdded(addedDevices);

            }

            @Override
            public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                super.onAudioDevicesRemoved(removedDevices);

            }
        }, null);
    }

    public void initializeAndStart() {
        loadParameters();
        try {
            buildAudioTrack();  //Playing audio is ready
            buildAudioRecord();
            startAudioRecord();
            resume();
        } catch (Exception e) {
            Log.e(getClass().getName(), "Could not start audio IO");
            e.printStackTrace();
        }

        shortInputBuffer = new short[bufferSize];
        shortOutputBuffer = new short[bufferSize];
        intOutputBuffer = new int[bufferSize];

        audioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(keepLoopAlive) {
                    if(playing) {
                        readAudioFromMicrophone();
                        //fetchAudio(shortInputBuffer, intOutputBuffer);
                        sendAudioToSpeaker(intOutputBuffer);
                    }
                }
            }
        });
        audioThread.setName("Java Audio Thread");
        audioThread.start();
    }
    public void stop() {
        keepLoopAlive = false;
        try {
            audioThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopAudioRecord();
        audioTrack.stop();
    }

    public void resume() {
        if(audioTrack != null)
            audioTrack.play();
        playing = true;
    }
    public void pause() {
        if(audioTrack != null)
            audioTrack.pause();
        playing = false;
    }
    public void startAudioRecord() {
        if(recordingGranted) {
            audioRecord.startRecording();
            Log.i(getClass().getName(), "AudioRecord was started");
        }
    }
    public void stopAudioRecord() {
        if(audioRecord != null) {
            if(audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                Log.i(getClass().getName(), "AudioRecord was stopped");
                audioRecord.stop();
            }
            if(audioRecord.getRecordingState() == AudioRecord.STATE_INITIALIZED) {
                Log.i(getClass().getName(), "AudioRecord was released");
                audioRecord.release();
            }
        }

    }

    public void loadParameters() {
        sampleRate = Integer.parseInt(audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE));
        bufferSize = Integer.parseInt(audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER))*bufferMultiplier;
        Log.i(getClass().getName(), "Parameters sampleRate is " + sampleRate + " and bufferSize is " + bufferSize);
    }

    public void buildAudioTrack() {
        try {
            outAudioAttributes = new AudioAttributes.Builder()
                    .setUsage(out_usage)
                    .setContentType(out_contentType)
                    .build();
            outAudioFormat = new AudioFormat.Builder()
                    .setEncoding(out_encoding)
                    .setSampleRate(sampleRate)
                    .setChannelMask(out_channelMask)
                    .build();
            audioTrackBuilder = new AudioTrack.Builder()
                    .setAudioAttributes(outAudioAttributes)
                    .setAudioFormat(outAudioFormat)
                    .setPerformanceMode(out_performanceMode)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(out_transferMode);
            audioTrack = audioTrackBuilder.build();
            Log.i(getClass().getName(), "AudioTrack built with sampleRate: " + sampleRate + " and bufferSize: " + bufferSize);
        } catch(UnsupportedOperationException e) {
            Log.i(getClass().getName(), "AudioTrack was not built");
            e.printStackTrace();
        }
    }
    public void buildAudioRecord() {
        if(audioRecord == null || audioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
            inAudioFormat = new AudioFormat.Builder()
                    .setEncoding(in_encoding)
                    .setSampleRate(sampleRate)
                    .setChannelMask(in_channelMask)
                    .build();
            audioRecordBuilder = new AudioRecord.Builder()
                    .setBufferSizeInBytes(AudioRecord.getMinBufferSize(sampleRate, in_channelMask, in_encoding))
                    .setAudioFormat(inAudioFormat)
                    .setAudioSource(in_audioSource);
            try {
                audioRecord = audioRecordBuilder.build();
            } catch(Exception e) {
                e.printStackTrace();
            }
            if(audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED)
                Log.i(getClass().getName(), "AudioRecord built with sampleRate: " +
                        sampleRate + " and bufferSize: " + bufferSize);
            Log.i(getClass().getName(), "Recording device is " +
                    audioRecord.getRoutedDevice().getProductName().toString() + ", " +
                    "Sample Rates: " + Arrays.toString(audioRecord.getRoutedDevice().getSampleRates()));
        }
    }

    //Managing input and output devices connected to the phone
    public static AudioDeviceInfo[] getOutputDevices() {
        return audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
    }
    public static AudioDeviceInfo[] getInputDevices() {
        return audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
    }
    public static AudioDeviceInfo[] getAllDevices() {
        return audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
    }
    public static void setPreferredInputDevice(AudioDeviceInfo inputDevice) {
        audioRecord.setPreferredDevice(inputDevice);
    }
    public static void setPreferredOutputDevice(AudioDeviceInfo outputDevice) {
        audioTrack.setPreferredDevice(outputDevice);
    }
    public static AudioDeviceInfo getPreferredInputDevice() {
        return audioRecord.getPreferredDevice();
    }
    public static AudioDeviceInfo getPreferredOutputDevice() {
        return audioTrack.getPreferredDevice();
    }
    public static AudioDeviceInfo getRoutedInputDevice() {
        return audioRecord.getRoutedDevice();
    }
    public static AudioDeviceInfo getRoutedOutputDevice() {
        return audioTrack.getRoutedDevice();
    }

    public void sendAudioToSpeaker(int[] outputBuffer) {
        for(int i = 0; i < outputBuffer.length; i++) {
            if(outputBuffer[i] > Short.MAX_VALUE) {
                shortOutputBuffer[i] = Short.MAX_VALUE;
            } else if(outputBuffer[i] < Short.MIN_VALUE) {
                shortOutputBuffer[i] = Short.MIN_VALUE;
            } else {
                shortOutputBuffer[i] = (short)outputBuffer[i];
            }
        }
        if(audioTrack != null)
            audioTrack.write(shortOutputBuffer, 0, outputBuffer.length, AudioTrack.WRITE_BLOCKING);
    }
    public void readAudioFromMicrophone() {
        if(recordingGranted) {
            if(audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.read(shortInputBuffer, 0, shortInputBuffer.length, AudioRecord.READ_NON_BLOCKING);
                for(int i = 0; i < shortInputBuffer.length; i++) {
                    intInputBuffer[i] = shortInputBuffer[i];
                }
            }
        }
    }
}