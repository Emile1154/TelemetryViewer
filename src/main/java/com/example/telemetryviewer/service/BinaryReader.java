package com.example.telemetryviewer.service;


import com.example.telemetryviewer.models.TelemetryData;
import com.example.telemetryviewer.models.TelemetryIndex;

public class BinaryReader {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public void init() throws RuntimeException{
        String nativeLib;
        System.out.println(OS);
        if (isWindows()) {
            System.out.println("This is Windows");
            nativeLib = System.getProperty("user.dir") + "/filedecoder.dll";
        } else if (isUnix()) {
            System.out.println("This is Unix or Linux");
            nativeLib = System.getProperty("user.dir") + "/filedecoder_linux.so";
        } else{
            throw new RuntimeException("Ваша операционная система не поддерживается");
        }
        System.out.println("nativeLib: " + nativeLib);
        try {
            System.load(nativeLib);
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException("" + nativeLib, e);
        }
    }


    public native TelemetryIndex[] readData(String path);
    public native TelemetryData getData(int address_start, int address_end);
}
