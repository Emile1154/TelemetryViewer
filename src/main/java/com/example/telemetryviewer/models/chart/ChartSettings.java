package com.example.telemetryviewer.models.chart;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartSettings {
    private ChartType chartType;
    private String filepath;
}
