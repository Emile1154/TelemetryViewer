package com.example.telemetryviewer.service;

import com.example.telemetryviewer.models.InfoWell;
import com.example.telemetryviewer.models.TelemetryData;
import com.example.telemetryviewer.models.TelemetryIndex;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import static java.lang.Math.abs;

public class LogASCIIStandart {
    public String filepath;

    private String hh_mm_ss_sum_seconds(TelemetryIndex index, long sec) {
        long total_seconds = index.hour * 3600 + index.minute * 60 + index.second + sec;
        if(total_seconds >= 86400){  // is a 00:00:00
            total_seconds = total_seconds - 86400;
        }
        return String.format("%02d%02d%02d", total_seconds/3600, (total_seconds % 3600)/60, total_seconds % 60);
    }

    public void convertDataToLAS(TelemetryIndex index, TelemetryData data, InfoWell info) throws FileNotFoundException, UnsupportedEncodingException {
        String filename = filepath +"/"+index.event+ "_"+index.date+"-"+index.month+"-"+index.year+ "_" +index.hour+"-"+index.minute + "_" +index.end_hour+"-"+index.end_minute +".las";
        PrintWriter writer = new PrintWriter(filename, "UTF-8");
        writer.println("~Version Information");
        writer.println("VERS. 1.20: LOG ASCII STANDARD VERSION 1.2");
        writer.println("WRAP. NO: One line per depth step");

        writer.println("~Well Information");
        writer.println("STRT.M " + data.depth[0]);
        writer.println("STOP.M " + data.depth[data.depth.length - 1]);
        writer.println("STEP.M " + 0.01);

        writer.println("NULL.M " + -999.25);
        writer.println("COMP. " + info.company);
        writer.println("WELL. " + info.well);
        writer.println("KUST. " + info.kust);
        writer.println("FLD . " + info.fld);
        writer.println("LOC . " + info.loc);
        writer.println("CNTY. " + info.cnty);
        writer.println("STAT. " + info.stat);
        writer.println("CTRY. " + info.ctry);
        writer.println("SRVC. " + info.srvc);
        writer.println("DATE.DD/MM/YYYY " + String.format("%02d/%02d/%04d", index.date, index.month, index.year));
        writer.println("TIME.HH-MM-SS " + String.format("%02d-%02d-%02d", index.hour, index.minute, index.second));
        writer.println("API . : API ");

        writer.println("~Cursor Information");
        writer.println("DEPT.M");
        writer.println("TIME.S");
        writer.println("S.dV/dT");
        writer.println("TEN.KG");

        writer.println("~ASCII Log Data");

        for (int i = 0; i < data.depth.length; i++) {
            float velocity = 0;
            String time = hh_mm_ss_sum_seconds(index, i);
            if(i > 1) {
                velocity = abs(data.depth[i] - data.depth[i - 1])*3600;  // m/hour
            }
            writer.println(data.depth[i] + " " + time + " " + velocity + " " + data.tension[i]);
        }
        writer.close();
    }
}
