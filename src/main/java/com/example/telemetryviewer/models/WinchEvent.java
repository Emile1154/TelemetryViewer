package com.example.telemetryviewer.models;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class WinchEvent {
    private int timeSecondsDay;
    private String date;
    private EventEnum event;
}
