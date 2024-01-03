package com.example.telemetryviewer.controllers;

import com.example.telemetryviewer.models.chart.ChartSettings;
import com.example.telemetryviewer.models.chart.ChartType;
import com.example.telemetryviewer.models.storage.DepthData;
import com.example.telemetryviewer.models.storage.MagnetData;
import com.example.telemetryviewer.models.storage.TensionData;
import com.example.telemetryviewer.service.BinaryReader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private String pathToFiles = null;
    private ChartSettings chartSettings;
    private DepthData depth;
    private TensionData tension;
    private MagnetData magnet;

    private String outputDate = null;

    @FXML
    DatePicker datePicker;

    @FXML
    LineChart<Number, Number> chart;
    public MainController() {
        this.depth = new DepthData();
        this.tension = new TensionData();
        this.magnet = new MagnetData();
        this.chartSettings = new ChartSettings(ChartType.DEPTH, null);


    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //init chart


    }

    @FXML
    protected void buttonEventFile(){
        System.out.println("check");
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if(selectedDirectory == null){
            return;
        }

        File[] files = selectedDirectory.listFiles(f -> f.getName().endsWith(".bin"));

        if(files.length == 0 || files == null){
            System.out.println("файлы не обнаружены");
            showError("Ошибка — требуемые файлы не обнаружены", "в указанной директории не обнаружены файлы с расширением .bin");
            return;
        }
        File eventLogFile = Objects.requireNonNull(selectedDirectory.listFiles(f -> f.getName().equals("event_log.txt")))[0];
        if(eventLogFile == null){
            System.out.println("файл событий не обнаружен");
            showError("Ошибка — требуемые файлы не обнаружены", "в указанной директории не обнаружен файл event_log.txt");
            return;
        }
        pathToFiles = selectedDirectory.getAbsolutePath();
        pathToFiles =  pathToFiles.replace("\\", "/");

        //read file event_log.txt

    }

    @FXML
    protected void selectDepth(){
        if(this.chartSettings.getChartType() != ChartType.DEPTH){
            chartSettings.setChartType(ChartType.DEPTH);

            if(pathToFiles == null){
                showError("Ошибка — не выбрана директория с файлами", "Укажите папку с файлами .bin");
                return;
            }

            if(outputDate == null){
                showError("Ошибка — нужно выбрать дату", "Укажите дату");
                return;
            }

            File file = new File(chartSettings.getFilepath());
            if(! file.exists()){
                showError("Ошибка — бинарный файл на указанную дату не обнаружен", "Отсутствует файл логирования на дату: " + outputDate);
                return;
            }

            BinaryReader binaryReader = new BinaryReader(depth);
            binaryReader.readFile(Path.of(chartSettings.getFilepath()));
        }
    }

    @FXML
    protected void selectTension(){
        if(this.chartSettings.getChartType() != ChartType.TENSION){
            chartSettings.setChartType(ChartType.TENSION);

            if(pathToFiles == null){
                showError("Ошибка — не выбрана директория с файлами", "Укажите папку с файлами .bin");
                return;
            }

            if(outputDate == null){
                showError("Ошибка — нужно выбрать дату", "Укажите дату");
                return;
            }

            File file = new File(chartSettings.getFilepath());
            if(! file.exists()){
                showError("Ошибка — бинарный файл на указанную дату не обнаружен", "Отсутствует файл логирования на дату - " + outputDate);
                return;
            }

            BinaryReader binaryReader = new BinaryReader(tension);
            binaryReader.readFile(Path.of(chartSettings.getFilepath()));
        }
    }

    @FXML
    protected void selectMagnet() {
        if (this.chartSettings.getChartType() != ChartType.MAGNET) {
            chartSettings.setChartType(ChartType.MAGNET);

            if(pathToFiles == null){
                showError("Ошибка — не выбрана директория с файлами", "Укажите папку с файлами .bin");
                return;
            }

            if(outputDate == null){
                showError("Ошибка — нужно выбрать дату", "Укажите дату");
                return;
            }

            File file = new File(chartSettings.getFilepath());
            if(! file.exists()){
                showError("Ошибка — бинарный файл на указанную дату не обнаружен", "Отсутствует файл логирования на дату - " + outputDate);
                return;
            }

            BinaryReader binaryReader = new BinaryReader(magnet);
            binaryReader.readFile(Path.of(chartSettings.getFilepath()));
        }
    }

    @FXML
    protected void calendarEvent(){
        LocalDate selectedDate = datePicker.getValue();
        if(selectedDate == null){
            return;
        }
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatOutput = new SimpleDateFormat("d-M-yyyy");
        try{
            Date inputDate = formatInput.parse(selectedDate.toString());
            outputDate = formatOutput.format(inputDate);

            chartSettings.setFilepath(pathToFiles + "/" + outputDate + ".bin");

            //check file is exist
            if(pathToFiles == null){
                showError("Ошибка — не выбрана директория с файлами", "Укажите папку с файлами .bin");
                return;
            }

            File file = new File(chartSettings.getFilepath());
            if(! file.exists()){
                showError("Ошибка — бинарный файл на указанную дату не обнаружен", "Отсутствует файл логирования на дату - " + outputDate);
                return;
            }
            //read new series for current date
            BinaryReader binaryReader;
            ChartType chartType = chartSettings.getChartType();
            if(chartType == ChartType.DEPTH){
                binaryReader = new BinaryReader(depth);
            }
            else if(chartType == ChartType.TENSION){
                binaryReader = new BinaryReader(tension);
            }
            else if (chartType == ChartType.MAGNET){
                binaryReader = new BinaryReader(magnet);
            }
            else {
                return;
            }
            binaryReader.readFile( Path.of(chartSettings.getFilepath()) );

        }catch (ParseException e){
            e.printStackTrace();
        }
    }


    private void showError(String title, String description) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Подробности ошибки:");
        alert.setContentText(description);
        alert.show();

    }
}
