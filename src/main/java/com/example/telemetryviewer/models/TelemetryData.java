package com.example.telemetryviewer.models;

public class TelemetryData {
    public float[] depth;
    public short[] tension;
    public byte[] magnet;

    public TelemetryData(float[] depth, short[] tension, byte[] magnet) {
        this.depth = depth;
        this.tension = tension;
        this.magnet = magnet;
    }
}
