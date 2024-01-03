package com.example.telemetryviewer.service;

import com.example.telemetryviewer.models.storage.Data;
import com.example.telemetryviewer.models.storage.DepthData;
import com.example.telemetryviewer.models.storage.MagnetData;
import com.example.telemetryviewer.models.storage.TensionData;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class BinaryReader {

    public static int DATA_ARRAY_SIZE = 86400; // 60 * 60 * 24 = 86400 seconds a day

    private final Data data;

    public BinaryReader(DepthData data) {
        this.data = data;
    }
    public BinaryReader(TensionData data) {
        this.data = data;
    }
    public BinaryReader(MagnetData data) {
        this.data = data;
    }


    public void readFile(Path filepath) {
        try{
            RandomAccessFile file = new RandomAccessFile(filepath.toFile(), "r");
            if(data instanceof DepthData){
                DepthData depthData = (DepthData) data;
                file.seek(DepthData.ADDRESSES_START_BYTE);

                float[] depth = new float[DATA_ARRAY_SIZE];

                for (int i = 0; i < DATA_ARRAY_SIZE ; i++) {
                    depth[i] = file.readFloat(); // pointer +4 bytes for next data
                }
                depthData.setDepth(depth);  //send the data
            }
            else if (data instanceof TensionData){
                TensionData tensionData = (TensionData) data;
                file.seek(TensionData.ADDRESSES_START_BYTE);

                short[] tension = new short[DATA_ARRAY_SIZE];

                for (int i = 0; i < DATA_ARRAY_SIZE ; i++) {
                    tension[i] = file.readShort(); // pointer +2 bytes for next data
                }
                tensionData.setTension(tension);  //send the data
            }
            else if (data instanceof MagnetData){
                MagnetData magnetData = (MagnetData) data;
                file.seek(MagnetData.ADDRESSES_START_BYTE);

                byte[] magnet = new byte[DATA_ARRAY_SIZE];

                for (int i = 0; i < DATA_ARRAY_SIZE ; i++) {
                    magnet[i] = file.readByte(); // pointer +1 bytes for next data
                }
                magnetData.setMagnet(magnet);  //send the data
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
