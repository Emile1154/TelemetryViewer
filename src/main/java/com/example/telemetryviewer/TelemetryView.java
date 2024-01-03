package com.example.telemetryviewer;

import com.example.telemetryviewer.controllers.MainController;
import com.example.telemetryviewer.models.chart.ChartSettings;
import com.example.telemetryviewer.models.chart.ChartType;
import com.example.telemetryviewer.models.storage.DepthData;
import com.example.telemetryviewer.models.storage.MagnetData;
import com.example.telemetryviewer.models.storage.TensionData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TelemetryView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TelemetryView.class.getResource("main_form.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Telemetry Viewer");
        stage.setMinHeight(800);
        stage.setMinWidth(1024);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        //super.init();
    }

    public static void main(String[] args) {
        launch();
    }
}