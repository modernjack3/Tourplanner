package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplanner.service.TourService;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class LogFormViewModel {

    private final TourService tourService;
    private final Tour selectedTour;
    private TourLog existingLog;

    // Fields bound from the UI
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(null);
    private final StringProperty time = new SimpleStringProperty("");
    private final StringProperty comment = new SimpleStringProperty("");
    private final StringProperty difficulty = new SimpleStringProperty("Medium");
    private final DoubleProperty distance = new SimpleDoubleProperty(0);
    private final DoubleProperty totalTime = new SimpleDoubleProperty(0);
    private final IntegerProperty rating = new SimpleIntegerProperty(3);

    // Store the combined LocalDateTime if valid
    private LocalDateTime dateTime;

    // Validation
    private final BooleanProperty canCreate = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");

    public LogFormViewModel(TourService service, Tour selected) {
        this.tourService = service;
        this.selectedTour = selected;

        // Whenever a property changes, re-validate
        date.addListener((obs, oldVal, newVal) -> validate());
        time.addListener((obs, oldVal, newVal) -> validate());
        comment.addListener((obs, oldVal, newVal) -> validate());
        difficulty.addListener((obs, oldVal, newVal) -> validate());
        distance.addListener((obs, oldVal, newVal) -> validate());
        totalTime.addListener((obs, oldVal, newVal) -> validate());
        rating.addListener((obs, oldVal, newVal) -> validate());

        validate();
    }


    // Falls wir editieren, laden wir die bestehenden Werte ins ViewModel
    public void setExistingLog(TourLog log) {
        this.existingLog = log;
        if (log != null) {
            if (log.getDateTime() != null) {
                date.set(log.getDateTime().toLocalDate());
                time.set(log.getDateTime().toLocalTime().toString());
            }
            comment.set(log.getComment());
            difficulty.set(log.getDifficulty());
            distance.set(log.getDistance());
            totalTime.set(log.getTotalTime());
            rating.set(log.getRating());
        }
        validate();
    }

    public void applyChanges() {
        if (!canCreate.get() || selectedTour == null) {
            return;
        }
        // valid => build dateTime

        if (existingLog == null) {
            // => "Add" scenario
            //TourLog newLog = new TourLog(java.util.UUID.randomUUID().toString());
            TourLog newLog = new TourLog(null);
            newLog.setDateTime(dateTime);
            newLog.setComment(comment.get());
            newLog.setDifficulty(difficulty.get());
            newLog.setDistance(distance.get());
            newLog.setTotalTime(totalTime.get());
            newLog.setRating(rating.get());
            tourService.addLogToTour(selectedTour, newLog);
        } else {
            // => "Edit" scenario
            existingLog.setDateTime(dateTime);
            existingLog.setComment(comment.get());
            existingLog.setDifficulty(difficulty.get());
            existingLog.setDistance(distance.get());
            existingLog.setTotalTime(totalTime.get());
            existingLog.setRating(rating.get());
            tourService.updateLog(existingLog);
        }
    }

    // The single "all-in-one" validation method
    private void validate() {
        StringBuilder sb = new StringBuilder();

        //Validate date/time
        LocalDate d = date.get();
        if (d == null) {
            sb.append("Please select a date.\n");
        } else {
            String timeStr = time.get().trim();
            if (timeStr.isEmpty()) {
                sb.append("Please enter a time in HH:MM format.\n");
            } else {
                try {
                    LocalTime parsedTime = LocalTime.parse(timeStr);
                    // If success, set dateTime
                    dateTime = d.atTime(parsedTime);
                } catch (DateTimeParseException e) {
                    sb.append("Invalid time format (use HH:MM).\n");
                }
            }
        }

        //Validate other fields
        if (comment.get().trim().isEmpty()) {
            sb.append("Comment is required.\n");
        }
        if (distance.get() < 0) {
            sb.append("Distance cannot be negative.\n");
        }
        if (totalTime.get() < 0) {
            sb.append("Total Time cannot be negative.\n");
        }
        if (rating.get() < 1 || rating.get() > 5) {
            sb.append("Rating must be between 1 and 5.\n");
        }

        //Set validation results
        if (sb.length() > 0) {
            canCreate.set(false);
            errorMessage.set(sb.toString().trim());
        } else {
            canCreate.set(true);
            errorMessage.set("");
        }
    }

    // Expose property getters for the controller to bind
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty timeProperty() { return time; }
    public StringProperty commentProperty() { return comment; }
    public StringProperty difficultyProperty() { return difficulty; }
    public DoubleProperty distanceProperty() { return distance; }
    public DoubleProperty totalTimeProperty() { return totalTime; }
    public IntegerProperty ratingProperty() { return rating; }

    public BooleanProperty canCreateProperty() {
        return canCreate;
    }
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    // Optional helper to forcibly set error
    public void setErrorMessage(String msg) {
        errorMessage.set(msg);
        canCreate.set(false);
    }
}
