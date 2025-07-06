module at.fhtw.tourplanner {
    /*
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens at.fhtw.tourplanner.model to javafx.base;
    opens at.fhtw.tourplanner.view.controller to javafx.fxml;
    opens at.fhtw.tourplanner.view to javafx.fxml;
    opens at.fhtw.tourplanner to javafx.fxml;
    opens at.fhtw.tourplanner.viewmodel to javafx.fxml;
    opens at.fhtw.tourplanner.service to javafx.fxml;
    exports at.fhtw.tourplanner;

     */
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;                // ← REST‑Client
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires at.fhtw.tourplannercommon;
    requires static lombok;
    requires java.desktop;
    requires org.apache.logging.log4j;

    opens at.fhtw.tourplanner.model to javafx.base, com.fasterxml.jackson.databind;
    opens at.fhtw.tourplanner.view.controller to javafx.fxml;
    opens at.fhtw.tourplanner.view to javafx.fxml;
    opens at.fhtw.tourplanner to javafx.fxml;
    opens at.fhtw.tourplanner.viewmodel to javafx.fxml;
    opens at.fhtw.tourplanner.service to javafx.fxml;
    exports at.fhtw.tourplanner;
}