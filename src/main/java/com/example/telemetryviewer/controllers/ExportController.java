package com.example.telemetryviewer.controllers;

import com.example.telemetryviewer.models.InfoWell;
import com.example.telemetryviewer.models.TelemetryData;
import com.example.telemetryviewer.models.TelemetryIndex;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ExportController extends MainController implements Initializable {
    private final String LAST_USED_LAS_PATH_KEY = "path_las";

    @FXML
    Label pathLabel;

    @FXML
    TextField well;

    @FXML
    TextField cnty;

    @FXML
    TextField fld;

    @FXML
    TextField ctry;

    @FXML
    TextField company;

    @FXML
    TextField loc;

    @FXML
    TextField srvc;

    @FXML
    TextField stat;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        las.filepath = Preferences.userRoot().node(getClass().getName()).get(LAST_USED_LAS_PATH_KEY, System.getProperty("user.home"));
        pathLabel.setText(las.filepath);
    }

    @FXML
    protected void pathForLAS(){

        Preferences preferences = Preferences.userRoot().node(getClass().getName());
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(preferences.get(LAST_USED_LAS_PATH_KEY, System.getProperty("user.home"))));
        File directory = directoryChooser.showDialog(null);
        if(directory == null){
            showError("Ошибка — путь не выбран", "Укажите папку для экспорта");
            return;
        }
        preferences.put(LAST_USED_LAS_PATH_KEY, directory.getAbsolutePath());
        las.filepath = preferences.get(LAST_USED_LAS_PATH_KEY, System.getProperty("user.home"));
        pathLabel.setText(las.filepath);
    }

    public void setData(TelemetryData data){
        this.currentData = data;
    }

    public void setIndex(TelemetryIndex index){
        this.currentIndex = index;
    }

    @FXML
    protected void exportLASexecute(){
        if(las.filepath == null){
            showError("Ошибка — путь не выбран", "Укажите папку для экспорта");
            return;
        }
        InfoWell info = new InfoWell();

        info.well = well.getText();
        info.cnty = cnty.getText();
        info.fld = fld.getText();
        info.ctry = ctry.getText();
        info.company = company.getText();
        info.srvc = srvc.getText();
        info.loc = loc.getText();
        info.stat = stat.getText();

        try {
            las.convertDataToLAS(this.currentIndex, this.currentData, info);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успешно");
            alert.setHeaderText("Экспорт завершен");
            alert.setContentText("Данные успешно экспортированы");
            alert.show();
        }catch (Exception e){
            showError("Не удалось экспортировать данные", e.getMessage());
//            throw new Exception(e.getMessage(), e.getCause());
        }

    }
}
