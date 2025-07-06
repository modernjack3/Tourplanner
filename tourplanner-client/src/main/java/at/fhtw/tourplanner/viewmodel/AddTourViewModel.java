package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.service.TourService;
import javafx.beans.property.*;

//Kapselt die Logik und Validierung
public class AddTourViewModel {

    private final TourService tourService;

    // Input fields: werden via Bindings vom Controller befüllt
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final StringProperty from = new SimpleStringProperty("");
    private final StringProperty to = new SimpleStringProperty("");
    private final StringProperty transportType = new SimpleStringProperty("");
    private final DoubleProperty distance = new SimpleDoubleProperty(0);
    private final DoubleProperty estimatedTime = new SimpleDoubleProperty(0);

    // Validation properties
    private final BooleanProperty canCreate = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");

    public AddTourViewModel(TourService service) {
        this.tourService = service;

        // Re-validate whenever user changes a field
        name.addListener((obs, oldVal, newVal) -> validate());
        description.addListener((obs, oldVal, newVal) -> validate());
        from.addListener((obs, oldVal, newVal) -> validate());
        to.addListener((obs, oldVal, newVal) -> validate());
        distance.addListener((obs, oldVal, newVal) -> validate());
        estimatedTime.addListener((obs, oldVal, newVal) -> validate());
        transportType.addListener((obs, oldVal, newVal) -> validate());

        // Initial check
        validate();
    }

    public void createTour() {
        // If invalid, do nothing
        if (!canCreate.get()) {
            return;
        }

        // Otherwise create
        tourService.createTour(
                name.get(),
                description.get(),
                from.get(),
                to.get(),
                transportType.get(),
                distance.get(),
                estimatedTime.get()
        );
    }

    private void validate() {
        StringBuilder errors = new StringBuilder();

        if (name.get().trim().isEmpty()) {
            errors.append("Name is required.\n");
        }
        if (distance.get() < 0) {
            errors.append("Distance cannot be negative.\n");
        }
        if (estimatedTime.get() < 0) {
            errors.append("Estimated time cannot be negative.\n");
        }

        if (errors.length() > 0) {
            canCreate.set(false);
            errorMessage.set(errors.toString().trim());
        } else {
            canCreate.set(true);
            errorMessage.set("");
        }
    }

    // Property getters for binding
    public StringProperty nameProperty() {
        return name;
    }
    public StringProperty descriptionProperty() {
        return description;
    }
    public StringProperty fromProperty() {
        return from;
    }
    public StringProperty toProperty() {
        return to;
    }
    public StringProperty transportTypeProperty() {
        return transportType;
    }
    public DoubleProperty distanceProperty() {
        return distance;
    }
    public DoubleProperty estimatedTimeProperty() {
        return estimatedTime;
    }

    public BooleanProperty canCreateProperty() {
        return canCreate;
    }
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }
}
