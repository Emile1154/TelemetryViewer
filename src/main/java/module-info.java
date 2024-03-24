module com.example.telemetryviewer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;

    requires org.jfree.chart.fx;
    requires org.jfree.fxgraphics2d;
    requires org.jfree.jfreechart;
    opens com.example.telemetryviewer.controllers to javafx.fxml;
    exports com.example.telemetryviewer;
}