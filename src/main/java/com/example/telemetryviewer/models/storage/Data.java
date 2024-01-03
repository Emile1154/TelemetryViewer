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
}



