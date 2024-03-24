package com.example.telemetryviewer.service;


import com.example.telemetryviewer.models.TelemetryData;
import com.example.telemetryviewer.models.TelemetryIndex;

public class BinaryReader {
    public void init() throws RuntimeException{
        String nativeLib = System.getProperty("user.dir") + "/filedecoder.dll";
        try {
            System.load(nativeLib);
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException("" + nativeLib, e);
        }
    }

    public native TelemetryIndex[] readData(String path);
    public native TelemetryData getData(int address_start, int address_end);
}
