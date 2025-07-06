package at.fhtw.tourplanner;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Das ist die Hauptklasse der GUI-Anwendung
public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(Main.class);

    /*HostServices erlaubt, aus der JavaFX-Anwendung heraus Dinge wie
    * den Standardbrowser zu öffnen
    * HostServices als globalen Zugriffspunkt verfügbar machen
     */
    private static HostServices hostServices;

    // Getter, damit andere Klassen, z.B. Controller, HostServices verwenden können
    public static HostServices getHostServicesInstance() {
        return hostServices;
    }

    //Initialisiert die JavaFX-UI
    @Override
    public void start(Stage primaryStage) throws Exception {
        //HostServices Referanz zwischenspeichern
        hostServices = getHostServices();
        logger.info("Starting TourPlanner Client");

        //loader.load() instanziiert unter der Haube den MainController für MainView.fxml und führt initialize() Methode
        //im Controller aus
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainView.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 600);

        /* Standard‑Stylesheet (hell) laden */
        scene.getStylesheets().add(Main.class.getResource("/style/light.css").toExternalForm());

        primaryStage.setTitle("Tour Planner");
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.debug("Main window shown");
    }
    //Startet die JavaFX-Anwendung
    public static void main(String[] args) {
        launch(args);
    }
}