package com.example.telemetryviewer.models.storage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Data{

    protected String fileName;
    protected int[] secondsTotal;
    protected byte[] hours;
    protected byte[] minutes;
    protected byte[] seconds;
    //protected String date;

    void secondsToDate(){
        // seconds_total = hours * 3600 + minutes * 60 + seconds
        for(int i = 0; i < secondsTotal.length; i++){
            double template = (seconds[i] / 3600.0);
            hours[i] = (byte) Math.floor(template);

            template = (template - hours[i]) * 60.0;
            minutes[i] = (byte) Math.floor(template);

            template = (template - minutes[i]) * 60.0;
            seconds[i] = (byte) Math.floor(template);
        }
    }
}



