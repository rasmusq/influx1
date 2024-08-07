#include <jni.h>
#include <string>
#include <android/log.h>

#include "AudioEngine.h"
#include "oboe/Oboe.h"

AudioEngine* audioEngine;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_ralleq_influx_MainActivity_initializeNativeAudioEngine(JNIEnv* env, jclass thiz) {

    __android_log_print(ANDROID_LOG_VERBOSE, "jni-bridge", "Starting native AudioEngine...");
    audioEngine = new AudioEngine();

    return true;
}
extern "C" JNIEXPORT jint JNICALL
Java_com_ralleq_influx_MainActivity_getSampleRate(JNIEnv* env, jclass thiz) {
    return audioEngine->getSampleRate();
}
extern "C" JNIEXPORT jint JNICALL
Java_com_ralleq_influx_MainActivity_getChannelCount(JNIEnv* env, jclass thiz) {
    return audioEngine->getChannelCount();
}
extern "C" JNIEXPORT void JNICALL
Java_com_ralleq_influx_MainActivity_restartNativeAudioEngine(JNIEnv* env, jclass thiz) {
    audioEngine->restart();
}
extern "C" JNIEXPORT void JNICALL
Java_com_ralleq_influx_MainActivity_startNativeAudioEngine(JNIEnv* env, jclass thiz) {
    audioEngine->start();
}
extern "C" JNIEXPORT void JNICALL
Java_com_ralleq_influx_MainActivity_stopNativeAudioEngine(JNIEnv* env, jclass thiz) {
    audioEngine->stop();
}
extern "C" JNIEXPORT void JNICALL
Java_com_ralleq_influx_MainActivity_initializeAudioMethod(JNIEnv* env, jclass thiz) {
    jobject globalClass = env->NewGlobalRef(thiz);
    JavaVM *vm;
    int result = env->GetJavaVM(&vm);
    __android_log_print(ANDROID_LOG_VERBOSE, "jni_bridge.cpp", "Got vm with result: %d", result);
    audioEngine->setJNICallbackMethod(globalClass, vm);
}