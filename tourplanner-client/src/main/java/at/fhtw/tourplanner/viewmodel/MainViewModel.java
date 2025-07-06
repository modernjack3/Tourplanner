package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.service.ReportClientService;
import at.fhtw.tourplanner.service.TourService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.List;

//Bindeglied zwischn dem Controller (also der GUI) und der Geschäftslogik (Service)
//ViewModel hält den Zustand, den das UI anzeigt
//Controller kümmert sich um UI-spezifische Dinge (z.B. Button-Klicks, Auswählen von Elementen),
//ruft aber nur einfache Methoden im ViewModel auf, statt selbst die Daten zu manipulieren.
//Service ist die "Fachschicht", die die Tour-Daten verwaltet (Erstellen, Löschen, Suchen). Das ViewModel ruft diesen Service auf
public class MainViewModel {

    //Das MainViewModel behält einen Verweis auf die TourService, damit es Touren laden, löschen oder filtern kann
    private final TourService tourService;

    //Die Liste, die mit der ListView verknüpft wird.
    // Wenn Elemente hinzugefügt oder entfernt werden, aktualisiert sich auch das UI automatisch
    private final ObservableList<Tour> toursObservable = FXCollections.observableArrayList();

    //Property, sodass man sie leicht binden kann.
    // Der Controller bindet searchField.textProperty() an searchTextProperty()
    private final StringProperty searchText = new SimpleStringProperty("");

    private Tour selectedTour;

    private final ReportClientService reports = new ReportClientService();

    public MainViewModel(TourService tourService) {
        this.tourService = tourService;
        //Sobald das ViewModel erzeugt wird, rufen wir loadTours() auf und befüllen somit die toursObservable direkt
        //mit allen Touren, die TourService liefert
        loadTours();
    }

    private void loadTours() {
        toursObservable.setAll(tourService.getAllTours());
    }

    //Diese Methode wird aufgerufen, wenn sich das Suchfeld ändert
    // ( durch den Listener im MainController)
    public void filterTours(String query) {
        if (query == null || query.isBlank()) {
            // Suchfeld leer -> alle Touren
            toursObservable.setAll(tourService.getAllTours());
        } else {
            // Suchfeld nicht leer -> delegation an tourService.searchTours(...)
            List<Tour> filtered = tourService.searchTours(query);
            toursObservable.setAll(filtered);
        }
    }


    public void setSelectedTour(Tour tour) {
        this.selectedTour = tour;
    }

    public Tour getSelectedTour() {
        return selectedTour;
    }

    public void deleteTour(Tour tour) {
        tourService.deleteTour(tour);
        toursObservable.remove(tour);
    }

    // For the main list
    public ObservableList<Tour> getToursObservable() {
        return toursObservable;
    }

    // For the search field
    public StringProperty searchTextProperty() {
        return searchText;
    }

    public Path generateTourReport(Tour tour) {
        try {
            return reports.downloadTourReport(tour.getId());
        } catch (Exception e) {
            throw new RuntimeException("Tour‑Report fehlgeschlagen", e);
        }
    }

    public Path generateSummaryReport() {
        try {
            return reports.downloadSummary();
        } catch (Exception e) {
            throw new RuntimeException("Summary‑Report fehlgeschlagen", e);
        }
    }
}
