package at.fhtw.tourplanner.view.controller;

import at.fhtw.tourplanner.service.MapService;
import at.fhtw.tourplanner.service.MapService.Suggestion;
import at.fhtw.tourplanner.viewmodel.AddTourViewModel;
import at.fhtw.tourplanner.viewmodel.MainViewModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import javafx.geometry.Side;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

//Controller für das "Add Tour"-Dialogfenster
public class AddTourController {
    private static final Logger logger = LogManager.getLogger(AddTourController.class);

    // FXML‑Felder
    @FXML private TextField nameField;
    @FXML private TextArea descField;
    @FXML private TextField fromField;
    @FXML private TextField toField;
    @FXML private ComboBox<String> transportField;
    @FXML private TextField distanceField;
    @FXML private TextField timeField;
    @FXML private Label errorLabel;
    @FXML private Button createButton;

    // interne Felder
    private AddTourViewModel viewModel;
    private Stage dialogStage;
    private final MapService map = new MapService();

    // Merkt sich die mittels Auto‑Complete gewählten Koordinaten
    private double[] fromCoord = null;
    private double[] toCoord = null;

    // Initialisierung, wird direkt nach dem Erzeugen des Dialog-Fensters aus AddTourWindow aufgerufen
    public void setViewModel(AddTourViewModel vm, MainViewModel mainVM, Stage stage) {
        this.viewModel = vm;
        this.dialogStage = stage;
        bindFields();
        initAutoComplete(fromField, true);
        initAutoComplete(toField,false);
        logger.debug("AddTourController ready");
    }

    // Property‑Bindungen
    //Bidirektional, zwischen UI-Feldern und Properties des AddTourViewModel.
    private void bindFields() {

        //Strings
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        descField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        viewModel.transportTypeProperty().set("driving-car");               // Default

        // Transport‑Typ: interne ORS-Profile
        // jede Änderung triggert recomputeDistance()
        transportField.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if (n == null) return;
            switch (n) {
                case "Car" -> viewModel.transportTypeProperty().set("driving-car");
                case "Walking" -> viewModel.transportTypeProperty().set("foot-walking");
                case "Bike" -> viewModel.transportTypeProperty().set("cycling-regular");
            }
            recomputeDistance();
        });

        // Zahlen
        distanceField.textProperty().bindBidirectional(
                viewModel.distanceProperty(), new NumberStringConverter());
        timeField.textProperty().bindBidirectional(
                viewModel.estimatedTimeProperty(), new NumberStringConverter());

        // Validierung
        createButton.disableProperty().bind(viewModel.canCreateProperty().not());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
    }

    /* =============================================================
       Auto‑Complete für From / To
       ============================================================= */
    private void initAutoComplete(TextField field, boolean isFrom) {

        ContextMenu popup = new ContextMenu();
        popup.setAutoHide(true);

        /* Wenn der Nutzer tippt → Vorschläge nach <300 ms> laden */
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
        field.textProperty().addListener((obs, o, n) -> {
            pause.stop();
            pause.setOnFinished(e -> loadSuggestions(n, popup, field, isFrom));
            pause.playFromStart();

            /* Koordinate verwerfen, falls Text manuell geändert wurde */
            if (isFrom)
                fromCoord = null;
            else
                toCoord = null;
        });
    }
    //Lädt Vorschläge im Hintergrund und zeigt sie an
    private void loadSuggestions(String query,
                                 ContextMenu popup,
                                 TextField owner,
                                 boolean isFrom) {

        if (query == null || query.length() < 3) {
            popup.hide();
            return;
        }

        //Task ist ein Objekt in JavaFX, das eine asynchrone Aufgabe kapselt, um
        // die Haupt-GUI nicht zu blockieren.
        //map.suggest() ruft den ORS-Proxy des Servers auf und liefert max.5 Treffer
        Task<List<Suggestion>> task = new Task<>() {
            @Override protected List<Suggestion> call() throws Exception {
                return map.suggest(query);
            }
        };
        task.setOnSucceeded(evt -> {
            List<Suggestion> list = task.getValue();
            if (list.isEmpty()) {
                popup.hide();
                return;
            }

            popup.getItems().clear();
            for (Suggestion s : list) {
                MenuItem mi = new MenuItem(s.label());
                mi.setOnAction(a -> {
                    owner.setText(s.label());
                    if (isFrom)
                        fromCoord = new double[]{s.lon(), s.lat()};
                    else
                        toCoord = new double[]{s.lon(), s.lat()};
                    popup.hide();
                    recomputeDistance();
                });
                popup.getItems().add(mi);
            }
            popup.show(owner, Side.BOTTOM, 0, 0);
        });
        task.setOnFailed(evt -> popup.hide());

        new Thread(task).start();
    }

    /* =============================================================
       Entfernung & Dauer berechnen, sobald beide Koordinaten bekannt
       ============================================================= */
    private void recomputeDistance() {
        if (fromCoord == null || toCoord == null) return;

        Task<Void> t = new Task<>() {
            @Override protected Void call() throws Exception {
                JsonNode root = map.directions(fromCoord, toCoord,
                        viewModel.transportTypeProperty().get());
                ObjectNode sum = (ObjectNode) root.at("/features/0/properties/summary");
                double distKm = sum.get("distance").asDouble() / 1000.0;
                double timeMin= sum.get("duration").asDouble() / 60.0;

                //Asynchrone Methode um Code aus andren Threads in
                // den JavaFX-Anwendungsthread zu verschieben
                Platform.runLater(() -> {
                    viewModel.distanceProperty().set(distKm);
                    viewModel.estimatedTimeProperty().set(timeMin);
                    viewModel.fromProperty().set(nameOrLabel(fromField.getText()));
                    viewModel.toProperty().set(nameOrLabel(toField.getText()));
                });
                return null;
            }
        };
        new Thread(t).start();
    }

    private static String nameOrLabel(String txt) {
        return txt == null ? "" : txt.strip();
    }


    @FXML
    private void onCreate() {
        logger.debug("Create‑Tour button pressed");
        viewModel.createTour();
        if (viewModel.canCreateProperty().get())
            dialogStage.close();
    }
}
