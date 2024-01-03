package com.example.telemetryviewer.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkLog {
    private WinchEvent startWork;
    private WinchEvent endWork;

    @Override
    public String toString() {
        // [dd-MM-yyyy] период работы [HH:mm:ss — HH:mm:ss]
        String startTime = getStringTimeFromSeconds(startWork.getTimeSecondsDay());
        String endTime;
        if(endWork == null){
            endTime = "23:59:59";
            return "["+ startWork.getDate() + "] период работы [" + startTime + " — " + endTime + "]";
        }
        endTime = getStringTimeFromSeconds(endWork.getTimeSecondsDay());
        return "["+ startWork.getDate() + "] период работы [" + startTime + " — " + endTime + "]";
    }

    private String getStringTimeFromSeconds(int totalSeconds){
        byte hours, minutes, seconds;
        double template = (totalSeconds / 3600.0);
        hours = (byte) Math.floor(template);

        template = (template - hours) * 60.0;
        minutes = (byte) Math.floor(template);

        template = (template - minutes) * 60.0;
        seconds = (byte) Math.floor(template);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
