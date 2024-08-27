package com.example.telemetryviewer.controllers;

import com.example.telemetryviewer.models.ChartType;
import com.example.telemetryviewer.models.InfoWell;
import com.example.telemetryviewer.models.TelemetryData;
import com.example.telemetryviewer.models.TelemetryIndex;
import com.example.telemetryviewer.service.BinaryReader;
import com.example.telemetryviewer.service.LogASCIIStandart;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;


public class MainController implements Initializable {
    private String filepath;
    private BinaryReader binaryReader = null;
    private TelemetryIndex[] index;
    protected TelemetryData currentData;
    protected TelemetryIndex currentIndex;
    private LocalDate selectedDate;
    private ChartType chartType;

    protected LogASCIIStandart las;

    TimeSeriesCollection dataset;
    private JFreeChart chart;

    @FXML
    AnchorPane chartPane;

    @FXML
    ChartViewer chartViewer;

    private final String LAST_USED_BINARY_PATH_KEY = "path_binary";

    TimeSeries depthSeries;
    TimeSeries tensionSeries;
    TimeSeries magnetSeries;

    private void createChart() {
        dataset = new TimeSeriesCollection();
        TimeSeries series = new TimeSeries("");

        String title = "Укажите файл c расширением .bin и выберите дату и событие";
        chart = ChartFactory.createTimeSeriesChart(
                title,
                "ВРЕМЯ (ЧЧ:ММ:СС)",
                "ЗНАЧЕНИЕ",
                dataset,
                false,
                true,
                true
        );

        XYPlot plot = (XYPlot) chart.getPlot();

        plot.setDomainPannable(true);

        DateAxis axis = (DateAxis) plot.getDomainAxis();

        axis.setAutoRange(true);
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss"));

        chartViewer = new ChartViewer(chart, true);

//        chartViewer.setMaxWidth(Double.MAX_VALUE);
//        chartViewer.setMaxHeight(Double.MAX_VALUE);
        chartPane.getChildren().add(chartViewer);
    }

    @FXML
    DatePicker datePicker;
    @FXML
    VBox buttonContainer;
    @FXML
    ScrollPane scrollPane;
    @FXML
    VBox mainVbox;

    public MainController() {
        this.binaryReader = new BinaryReader();
        try {
            this.binaryReader.init();
        }catch (RuntimeException e){
            showError("Не удалось загрузить библиотеку filedecoder.dll"," убедитесь что файл filedecoder.dll находится в папке с jar файлом");
        }

        this.chartType = ChartType.DEPTH;

        las = new LogASCIIStandart();

        depthSeries = new TimeSeries("Глубина");
        tensionSeries = new TimeSeries("Натяжение");
        magnetSeries = new TimeSeries("ДММ");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonContainer = new VBox();
        scrollPane.setContent(buttonContainer);
        datePicker.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                int day   = date.getDayOfMonth();
                int month = date.getMonthValue();
                int year  = date.getYear();

                boolean isAvailable = false;

                if(index != null) {
                    for (TelemetryIndex telemetryIndex : index) {
                        if (telemetryIndex.checkIsAvailable(day, month, year)) {
                            isAvailable = true;
                            break;
                        }
                    }
                }
                if(! isAvailable){
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        createChart();

        mainVbox.heightProperty().addListener((observable, oldValue, newValue) -> {
            chartViewer.setPrefHeight(newValue.doubleValue()/1.11);
        });
        mainVbox.widthProperty().addListener((observable, oldValue, newValue) -> {
            chartViewer.setPrefWidth((newValue.doubleValue()/1.3));
        });
    }

    @FXML
    protected void exportLAS() throws IOException {
        if(currentData == null){
            showError("Ошибка — выберете данные", "Данные находятся в журнале событий по указанной дате");
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/telemetryviewer/las_form.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Экспорт в LAS");
        stage.setScene(new Scene(root, 550, 400));
        stage.setResizable(false);
        stage.show();
    }



    @FXML
    protected void buttonEventFile(){
        Preferences preferences = Preferences.userRoot().node(getClass().getName());
        FileChooser fileChooser = new FileChooser();
        System.out.println(preferences.get(LAST_USED_BINARY_PATH_KEY, System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL FILES","*.bin"));
        fileChooser.setInitialDirectory(new File(new File(preferences.get(LAST_USED_BINARY_PATH_KEY, System.getProperty("user.home"))).getParent()));
        File binaryFile = fileChooser.showOpenDialog(null);

        if(binaryFile == null){
            System.out.println("файлы не обнаружены");
            showError("Ошибка — требуемый файл не обнаружен", "не обнаружен файл с расширением .bin");
            return;
        }

        this.filepath = binaryFile.getAbsolutePath();
        preferences.put(LAST_USED_BINARY_PATH_KEY, filepath);
        this.filepath = filepath.replace("\\", "/");


        System.out.println("filepath: " + filepath);

        index = this.binaryReader.readData(filepath);

        for (TelemetryIndex telemetryIndex : index) {
            System.out.println(telemetryIndex.toString());
        }
    }


    @FXML
    protected void selectDepth(){
        if(chartType != ChartType.DEPTH ){
            chartType = ChartType.DEPTH;
            if(filepath == null){
                showError("Ошибка — не выбран бинарный файл", "Укажите путь к файлу .bin");
                return;
            }

            if(selectedDate == null){
                showError("Ошибка — нужно выбрать дату", "Укажите дату");
                return;
            }

            if(currentData == null){
                showError("Ошибка — выберете событие", "События находятся в журнале событий по указанной дате");
                return;
            }
            chart.setTitle("Функция глубины: метры/чч:мм:сс");
            dataset.addSeries(depthSeries);
        }
    }
    @FXML
    protected void selectTension(){
        if(chartType != ChartType.TENSION ){
            chartType = ChartType.TENSION;
            if(filepath == null){
                showError("Ошибка — не выбрана директория с файлами", "Укажите папку с файлами .bin");
                return;
            }

            if(selectedDate == null){
                showError("Ошибка — нужно выбрать дату", "Укажите дату");
                return;
            }
            if(currentData == null){
                showError("Ошибка — выберете событие", "События находятся в журнале событий по указанной дате");
                return;
            }

            chart.setTitle("Функция натяжения: кг/чч:мм:сс");
            dataset.addSeries(tensionSeries);
        }
    }
    @FXML
    protected void selectMagnet() {
        if (chartType != ChartType.MAGNET ) {
            chartType = ChartType.MAGNET;
            if(filepath == null){
                showError("Ошибка — не выбрана директория с файлами", "Укажите папку с файлами .bin");
                return;
            }

            if(selectedDate == null){
                showError("Ошибка — нужно выбрать дату", "Укажите дату");
                return;
            }
            if(currentData == null){
                showError("Ошибка — выберете событие", "События находятся в журнале событий по указанной дате");
                return;
            }
            chart.setTitle("Функция магнитного поля: метка/чч:мм:сс");
            dataset.addSeries(magnetSeries);
        }
    }
    @FXML
    protected void calendarEvent(){
        //clear button container
        if(! buttonContainer.getChildren().isEmpty()){
            buttonContainer.getChildren().removeAll(buttonContainer.getChildren());
        }
        selectedDate = datePicker.getValue();
        if(selectedDate == null) {
            return;
        }
        for(TelemetryIndex telemetryIndex : index){
            if(! telemetryIndex.checkIsAvailable(selectedDate.getDayOfMonth(), selectedDate.getMonthValue(), selectedDate.getYear())){
                continue;
            }
            Button button = new Button();
            button.setText(telemetryIndex.toString());
            button.setFont(Font.font(14));
            button.setMinSize(270, 20);
            button.setOnAction(this::handleLogButtons);
            buttonContainer.getChildren().add(button);
        }
    }
    private void handleLogButtons(ActionEvent event){
        Button clickedButton = (Button) event.getSource();
        for(TelemetryIndex telemetryIndex : index){
            if(clickedButton.getText().equals(telemetryIndex.toString())){
                currentIndex = telemetryIndex;
                break;
            }
        }
        if(currentIndex == null){
            showError("Ошибка — не найдена запись в бинарном файле", null);
            return;
        }
        currentData = binaryReader.getData((int)currentIndex.start_address,(int)currentIndex.end_address);

        dataset.removeAllSeries();

        depthSeries.clear();
        tensionSeries.clear();
        magnetSeries.clear();

        RegularTimePeriod timeAxis = new Second(
                currentIndex.second,
                currentIndex.minute,
                currentIndex.hour,
                currentIndex.date,
                currentIndex.month,
                currentIndex.year
        );
        for(int i = 0; i < currentData.depth.length ; i++){
            depthSeries.add(timeAxis, currentData.depth[i]);
            tensionSeries.add(timeAxis, currentData.tension[i]);
            magnetSeries.add(timeAxis, currentData.magnet[i]);
            timeAxis = timeAxis.next();
        }
        if(chartType == ChartType.DEPTH){
            chart.setTitle("Функция глубины: метры/чч:мм:сс");
            dataset.addSeries(depthSeries);
        }else if (chartType == ChartType.TENSION){
            chart.setTitle("Функция натяжения: кг/чч:мм:сс");
            dataset.addSeries(tensionSeries);
        }else if(chartType == ChartType.MAGNET){
            chart.setTitle("Функция магнитных меток: метка/чч:мм:сс");
            dataset.addSeries(magnetSeries);
        }
    }

    protected void showError(String title, String description) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Подробности ошибки:");
        alert.setContentText(description);
        alert.show();

    }
}
