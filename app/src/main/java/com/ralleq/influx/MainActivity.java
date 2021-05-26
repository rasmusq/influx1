package com.ralleq.influx;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ralleq.influx.files.FileHandler;
import com.ralleq.influx.files.FontHandler;
import com.ralleq.influx.math.Units;
import com.ralleq.influx.midi.MidiEvent;
import com.ralleq.influx.midi.MidiHandler;
import com.ralleq.influx.objects.MainHandler;
import com.ralleq.influx.objects.TickHandler;
import com.ralleq.influx.screen.ScreenHandler;
import com.ralleq.influx.screen.accelerometer.AccelerometerHandler;
import com.ralleq.influx.screen.touch.TouchEvent;
import com.ralleq.influx.vibration.VibrationHandler;

public class MainActivity extends Activity {

    public static FileHandler fileHandler;
    public static MidiHandler midiHandler;
    public static ScreenHandler screenHandler;
    public static AccelerometerHandler accelerometerHandler;
    public static VibrationHandler vibrationHandler;
    public static TickHandler tickHandler;
    public static MainHandler mainHandler;

    static {
        System.loadLibrary("native-lib");
    }
    public static native boolean initializeNativeAudioEngine();
    public static native void initializeAudioMethod();
    public static int sampleRate, channelCount;
    private static int[] outLeft, outRight;
    private static short[] finalOutputBuffer, inLeft, inRight;
    private static boolean nextOutputBuffer = false, buffersInitialized = false, buffered = false;
    private static long framesMissed = 0;
    public static short[] requestOutputBuffer(short[] inputBuffer) {
        if(!buffersInitialized) {
            inLeft = new short[inputBuffer.length/2];
            inRight = new short[inputBuffer.length/2];
            outLeft = new int[inputBuffer.length/2];
            outRight = new int[inputBuffer.length/2];
            finalOutputBuffer = new short[inputBuffer.length];
            buffersInitialized = true;
        }
        int frame = 0;
        for(int sample = 0; sample < inputBuffer.length;) {
            for(int c = 0; c < channelCount; c++) {
                if(c==0)
                    inLeft[frame] = inputBuffer[sample];
                else if(c==1)
                    inRight[frame] = inputBuffer[sample];
                sample++;
            }
            frame++;
        }

        if(!buffered) {
            for(int i = 0; i < outLeft.length; i++) {
                audio(inLeft, inRight, outLeft, outRight, i);        //No buffering
            }
        }

        frame = 0;
        for(int sample = 0; sample < inputBuffer.length;) {
            for(int c = 0; c < channelCount; c++) {
                if(c==0)
                    finalOutputBuffer[sample] = convertIntToShortWithoutClipping(outLeft[frame]);
                else if(c==1)
                    finalOutputBuffer[sample] = convertIntToShortWithoutClipping(outLeft[frame]);
                sample++;
            }
            frame++;
        }

        if(buffered) {
            if(nextOutputBuffer) {        //Only for buffered audio
                framesMissed++;
                Log.i("MainActivity", "Frames Missed: " + framesMissed);
            }
            nextOutputBuffer = true;
        }

        return finalOutputBuffer;
    }
    public static short convertIntToShortWithoutClipping(int integer) {
        if(integer > Short.MAX_VALUE)
            return Short.MAX_VALUE;
        else if(integer < Short.MIN_VALUE)
            return Short.MIN_VALUE;
        else
            return (short)integer;
    }
    public static native void startNativeAudioEngine();
    public static native void restartNativeAudioEngine();
    public static native void stopNativeAudioEngine();
    public static native int getSampleRate();
    public static native int getChannelCount();
    private Thread nativeAudioThread;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrationHandler = new VibrationHandler(this);
        fileHandler = new FileHandler(this);
        new FontHandler(this);
        midiHandler = new MidiHandler(this) {
            @Override
            public void fetchMidiEvent(MidiEvent midiEvent) {
                midi(midiEvent);
            }
        };
        mainHandler = new MainHandler(midiHandler.getBaseMidiEventFetcher());
        screenHandler = new ScreenHandler(this) {
            @Override
            public void draw(Canvas canvas) {
                MainActivity.this.draw(canvas);
            }
            @Override
            public void fetchTouchEvent(TouchEvent touchEvent) {
                touch(touchEvent);
            }
            @Override
            public void fetchSurfaceDimensions(int width, int height) {
                updateBounds(width, height);
            }
        };
        accelerometerHandler = new AccelerometerHandler(this);
        tickHandler = new TickHandler() {
            @Override
            public void tick() {
                MainActivity.this.tick();
            }
        };

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        setContentView(screenHandler.getScreen());
        screenHandler.hideUI();

        checkAudioFeatures(this);
        requestPermissions(this);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.registerAudioDeviceCallback(new AudioDeviceCallback() {
            @Override
            public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
                super.onAudioDevicesAdded(addedDevices);
                stopNativeAudioEngine();
                initializeNativeAudioEngine();
                initializeAudioMethod();
                startNativeAudioEngine();
            }

            @Override
            public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
                super.onAudioDevicesRemoved(removedDevices);
                stopNativeAudioEngine();
                initializeNativeAudioEngine();
                initializeAudioMethod();
                startNativeAudioEngine();
            }
        }, null);

        initializeNativeAudioEngine();
        initializeAudioMethod();
        startNativeAudioEngine();
        sampleRate = getSampleRate();
        channelCount = getChannelCount();

        if(buffered) {
            nativeAudioThread = new Thread(new Runnable() {       //Thread for buffered audio
                @Override
                public void run() {
                    while(true) {
                        if(nextOutputBuffer) {
                            for(int i = 0; i < outLeft.length; i++) {
                                audio(inLeft, inRight, outLeft, outRight, i);
                            }
                            nextOutputBuffer = false;
                        }
                    }
                }
            });
            nativeAudioThread.setName("NativeAudioThread");
            nativeAudioThread.setPriority(Thread.MAX_PRIORITY);
            nativeAudioThread.start();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        screenHandler.hideUI();
    }
    @Override
    protected void onPause() {
        super.onPause();
        save();
        stopNativeAudioEngine();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void audio(short[] inLeft, short[] inRight, int[] outLeft, int[] outRight, int currentFrame) {
        mainHandler.audio(inLeft, inRight, outLeft, outRight, currentFrame);
    }
    public void tick() {
        mainHandler.tick();
    }
    public void draw(Canvas canvas) {
        mainHandler.draw(canvas);
    }
    public void touch(TouchEvent touchEvent) {
        mainHandler.touch(touchEvent);
    }
    public void updateBounds(int width, int height) {
        Units.setScreenDpi(screenHandler.getDpi(), width, height);
        mainHandler.updateBounds(width, height);
        accelerometerHandler.forceOrientationUpdate();
        Log.i(getClass().getName(), "New screen dimensions: [ " + width + " , " + height + " ]");
    }
    public void midi(MidiEvent midiEvent) {
        mainHandler.baseMidi(midiEvent);
        //receiveMidiEvent(midiEvent.getAction(), midiEvent.getIntParameters());
        //Log.i(getClass().getName(), midiEvent.toString());
    }
    public void save() {
        mainHandler.save();
    }


    public void checkAudioFeatures(Activity activity) {
        boolean hasLowLatencyFeature =
                activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_LOW_LATENCY);
        boolean hasProFeature =
                activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_PRO);
        Log.i(getClass().getName(), "Low latency feature: " + hasLowLatencyFeature);
        Log.i(getClass().getName(), "Pro feature: " + hasProFeature);
    }
    public void requestPermissions(Activity activity) {
        boolean recordingGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if(!recordingGranted) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            Log.i(getClass().getName(), "Permission for recording audio already granted");
        }
    }

}
