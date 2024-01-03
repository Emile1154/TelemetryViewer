package com.example.telemetryviewer.models;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DayWorkLogs {
    private List<WorkLog> list;
    private String date;
}
