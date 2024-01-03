package com.example.telemetryviewer.models;

import com.example.telemetryviewer.models.storage.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileInfo {
    List<Data> dataList;
    private String pathToFiles;
}
