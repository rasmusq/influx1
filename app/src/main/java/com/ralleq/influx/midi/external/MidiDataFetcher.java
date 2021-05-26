package com.ralleq.influx.midi.external;

public interface MidiDataFetcher {

    void fetchData(byte[] bytes, int offset, int count, long time, int midiDeviceId, String midiDeviceName);

}
