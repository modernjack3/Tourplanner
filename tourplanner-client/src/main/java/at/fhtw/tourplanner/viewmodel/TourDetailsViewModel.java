package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.service.TourService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TourDetailsViewModel {

    private final TourService tourService;
    private Tour currentTour;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    // etc. for from, to, distance, time, etc.

    public TourDetailsViewModel(TourService service) {
        this.tourService = service;
    }

    public void setTour(Tour tour) {
        currentTour = tour;
        if (tour != null) {
            name.set(tour.getName());
            description.set(tour.getDescription());
        }
    }

    public void saveChanges() {
        if (currentTour == null) return;
        // parse numeric fields as needed
        currentTour.setName(name.get());
        currentTour.setDescription(description.get());
        // ...
        tourService.updateTour(currentTour);
    }

    // getters for properties
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}
