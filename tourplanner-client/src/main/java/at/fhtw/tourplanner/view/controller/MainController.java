package at.fhtw.tourplanner.view.controller;

import at.fhtw.tourplanner.Main;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplanner.dal.TourLogRepository;
import at.fhtw.tourplanner.dal.TourRepository;
import at.fhtw.tourplanner.remote.TourLogRemoteRepository;
import at.fhtw.tourplanner.remote.TourRemoteRepository;
import at.fhtw.tourplanner.service.MapService;
import at.fhtw.tourplanner.service.TourService;
import at.fhtw.tourplanner.view.AddLogWindow;
import at.fhtw.tourplanner.view.AddTourWindow;
import at.fhtw.tourplanner.view.EditLogWindow;
import at.fhtw.tourplanner.view.TourDetailsWindow;
import at.fhtw.tourplanner.viewmodel.MainViewModel;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.List;


//Übernimmt die Steuernung (Event-Handling, Bindings)
// für die UI-Elemente in MainView.fxml
public class MainController {
    private static final Logger logger = LogManager.getLogger(MainController.class);

    private static final String DARK_SHEET = Main.class.getResource("/style/dark.css").toExternalForm();
    private static final String LIGHT_SHEET = Main.class.getResource("/style/light.css").toExternalForm();

    //markiert Variablen, die mit FXML-Elementen(fx:id="...") verknüpf ist
    @FXML private TextField searchField;
    @FXML private ListView<Tour> tourListView;
    @FXML private TableView<TourLog> logsTable;
    @FXML private TableColumn<TourLog, String> colDateTime;
    @FXML private TableColumn<TourLog, String> colComment;
    @FXML private TableColumn<TourLog, String> colDifficulty;
    @FXML private TableColumn<TourLog, Double> colDistance;
    @FXML private TableColumn<TourLog, Double> colTime;
    @FXML private TableColumn<TourLog, Integer> colRating;
    @FXML private ToggleButton darkToggle;

    //Kapseln Business-Logik und Server-Zugriff
    private MainViewModel mainViewModel;
    private TourService tourService;
    private MapService mapService;

    public void initialize() {
        logger.debug("Initialising MainController");

        // TourService -> Fassade über zwei Remote-Repositories (REST-Calls zum Spring-Boot-Server
        this.tourService = new TourService(
                new TourRemoteRepository(),
                new TourLogRemoteRepository()
        );
        //MainViewModel -> Hält ObservableLists & Properties, damit die UI bei Datenänderungen reagiert
        this.mainViewModel = new MainViewModel(tourService);
        this.mapService = new MapService();

        // SUCHE
        // Bind search field: dadurch synchronisiert sich der Inhalt von searchField automatisch
        //mit mainViewModel.searchText
        searchField.textProperty().bindBidirectional(mainViewModel.searchTextProperty());
        //Sobald sich der Such-Text ändert, ruft er filterTours(..) auf dem ViewModel auf
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            logger.debug("Search changed -> {}", newVal);
            mainViewModel.filterTours(newVal);
        });

        // TOUR-LISTE
        // Set up the tours ListView: damit binden wir die angezeigten Touren an die ObservableList, die das
        //ViewModel verwaltet. So aktualisiert sich die ListView bei Änderungen automatisch
        tourListView.setItems(mainViewModel.getToursObservable());
        tourListView.getSelectionModel().selectedItemProperty().addListener(onSelectTour());
        tourListView.setOnMouseClicked(this::onTourDoubleClick);

        // LOG-TABELLE
        // Columns for logs table: welches Feld im ToourLog-Objekt wird in welcher spalte angezeit.
        //"dateTime" korrespondiert mit Getter getDateTime() in TourLog, "comment" mit getComment() usw.
        colDateTime.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        colComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
        colDifficulty.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        colDistance.setCellValueFactory(new PropertyValueFactory<>("distance"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

        logsTable.setItems(FXCollections.observableArrayList());
        //Beim Umschalten der Tour-Auswahl werden die Logs aus dem Server nachgeladen:
        tourListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // newVal is the newly selected Tour
                //logsTable.setItems(FXCollections.observableArrayList(newVal.getLogs()));
                logger.debug("Tour selected: {}", newVal.getName());

                List<TourLog> logs = ((TourLogRemoteRepository) tourService.getLogRepository())
                        .fetchLogs(newVal);
                logsTable.getItems().setAll(logs);

            } else {
                /*
                logsTable.setItems(null);
                Nachdem wir eine neue Tour erstellen, wir so die logsTable auf null gesetzt.
                Dann reagiert auch der Listener onSelectTour (oder JavaFX ruft selbst beim
                Layout/Rendering irgendwelche Methoden auf, die logsTable.getItems() abfragen)
                Da das nun null ist, kommt es zum NullPointerException. Besser ist es, wenn wir es
                auf eine leere Liste, anstatt null:
                */
                logsTable.setItems(FXCollections.observableArrayList());
            }
        });

        logger.info("MainController initialised");
    }

    @FXML
    private void toggleDarkMode() {
        ObservableList<String> sheets = darkToggle.getScene().getStylesheets();

        if (darkToggle.isSelected()) {
            sheets.remove(LIGHT_SHEET);
            if (!sheets.contains(DARK_SHEET)) sheets.add(DARK_SHEET);
            logger.info("Switched to dark mode");
        } else {
            sheets.remove(DARK_SHEET);
            if (!sheets.contains(LIGHT_SHEET)) sheets.add(LIGHT_SHEET);
            logger.info("Switched to light mode");
        }
    }

    private ChangeListener<Tour> onSelectTour() {
        return (obs, oldVal, newVal) -> {
            mainViewModel.setSelectedTour(newVal);
            if (newVal != null) {
                if(!newVal.getLogs().isEmpty()) logsTable.getItems().setAll(newVal.getLogs());
            } else {
                logsTable.getItems().clear();
            }
        };
    }

    //Button-Events erlauben das Erstellen/Löschen von Touren und Logs
    //Buttonklick -> Controller-Methode -> ViewModel/Service
    @FXML
    private void onAddTour() {
        logger.debug("AddTour clicked");
        // Open a pop-up to add a new tour
        AddTourWindow window = new AddTourWindow(tourService, mainViewModel, mapService);
        window.showAndWait(); // user adds a new tour
        //Update List
        mainViewModel.filterTours(searchField.getText());
    }

    @FXML
    private void onDeleteTour() {
        //Aus der tourListView (linke Seite aus der fxml) hollen wir die aktuelll markierte (selected) Tour
        Tour selected = tourListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            mainViewModel.deleteTour(selected);
            logger.info("Deleting tour {}", selected.getName());
        }
        //Update List
        mainViewModel.filterTours(searchField.getText());
    }

    @FXML
    private void onAddLog() {
        Tour selected = tourListView.getSelectionModel().getSelectedItem();
        //User muss zuertst eine Tour ausgewählt haben. Wenn "selected" null ist, passsiert nichts
        if (selected != null) {
            logger.debug("AddLog for tour {}", selected.getName());
            //AddLogWindow (pop up) wird geöffnet. Hier übergeben wir den tourService und die gewählte Tour.
            //Damit weiß das Dialogfenster genau, zu welcher Tour das neue Log gehört
            AddLogWindow window = new AddLogWindow(tourService, selected);
            window.showAndWait();

            // After the window closes, refresh the logs table
            logsTable.setItems(FXCollections.observableArrayList(selected.getLogs()));
        }
    }

    @FXML
    private void onEditLog() {
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        TourLog selectedLog = logsTable.getSelectionModel().getSelectedItem();
        if (selectedTour != null && selectedLog != null) {
            logger.debug("EditLog {}", selectedLog.getId());
            EditLogWindow editWindow = new EditLogWindow(tourService, selectedTour, selectedLog);
            editWindow.showAndWait();

            // Danach die Table refreshen
            logsTable.setItems(FXCollections.observableArrayList(selectedTour.getLogs()));
        }
    }


    @FXML
    private void onDeleteLog() {
        TourLog selectedLog = logsTable.getSelectionModel().getSelectedItem();
        Tour selectedTour = tourListView.getSelectionModel().getSelectedItem();
        if (selectedLog != null && selectedTour != null) {
            logger.info("Deleting log {}", selectedLog.getId());
            //Entfernen des Logs aus der DB
            tourService.deleteLog(selectedTour, selectedLog);
            //Damit es auch direkt aus der Tabelle verschwindet
            logsTable.getItems().remove(selectedLog);
        }
    }

    // KARTE ANZEIGEN!!
    @FXML private void onShowMap() {
        Tour sel = tourListView.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        //1.Geocoding: holt Koordinaten aus den Adressen
        //2.Routing: Weg zwischen den Kooridnaten anfragen (directions())
        //3. Anzeige in externem Browser
        try {
            double[] from = mapService.geocode(sel.getFromLocation());
            double[] to = mapService.geocode(sel.getToLocation());
            mapService.showOnMap(mapService.directions(from, to, sel.getTransportType()));
            logger.debug("Map opened for tour {}", sel.getName());
        } catch (Exception ex) {
            logger.error("Cannot load the rout", ex);
            new Alert(Alert.AlertType.ERROR, "Route konnte nicht geladen werden:\n" +
                    ex.getMessage()).showAndWait();
        }
    }

    // PDF Generieren
    //Beide Methoden erhalten einen Pfad, speichern die Datei und
    // öffnen sie über HostServices.showDocument(..)
    @FXML
    private void onGenerateTourReport() {
        Tour sel = tourListView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            logger.debug("Generate Tour-Report failed. Select a Tour first.");
            new Alert(Alert.AlertType.INFORMATION, "Bitte zuerts eine Tour auswählen").showAndWait();
            return;
        }
        try {
            Path pdf = mainViewModel.generateTourReport(sel);
            logger.info("Generated Report saved to {}", pdf);
            Main.getHostServicesInstance().showDocument(pdf.toUri().toString());
        } catch (Exception ex) {
            logger.debug("Generate Tour-Report failed", ex);
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }
    @FXML
    private void onGenerateSummaryReport() {
        try {
            Path pdf = mainViewModel.generateSummaryReport();
            logger.info("Generated Summary Report saved to {}", pdf);
            Main.getHostServicesInstance().showDocument(pdf.toUri().toString());
        } catch (Exception ex) {
            logger.debug("Generated Summary Report failed", ex);
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }


    //Öffnet ein TourDetailsWindow, das die Details der Tour anzeigt, beim Doppelklick
    private void onTourDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Tour selected = tourListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                //Open a detail window for the double-clicked tour
                logger.debug("Opening details window for {}", selected.getName());
                TourDetailsWindow detailsWindow = new TourDetailsWindow(tourService, selected);
                detailsWindow.show();
            }
        }
    }

}
