package com.ralleq.influx.audio;

public interface AudioDataFetcher {

    void fetch(int[] inputBuffer, int[] outputBuffer);

}
