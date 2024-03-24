package com.example.telemetryviewer;

import com.example.telemetryviewer.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TelemetryView extends Application {
    @Override
    public void start(Stage stage){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setController(new MainController());
            fxmlLoader.setLocation(getClass().getResource("main_form.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            stage.setTitle("Telemetry Viewer");
            stage.setMinHeight(800);
            stage.setMinWidth(1024);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    public static void main(String[] args) {
        launch();
    }
}