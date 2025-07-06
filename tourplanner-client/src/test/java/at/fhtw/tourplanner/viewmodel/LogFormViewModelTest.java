package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.TourLogRepository;
import at.fhtw.tourplanner.dal.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LogFormViewModelTest {

    private TourService tourService;
    private Tour dummyTour;

    @BeforeEach
    public void setup() {
        // Wir initialisieren den Service mit In-Memory-Repos
        tourService = new TourService(new TourRepository(), new TourLogRepository());
        // Lege eine Dummy-Tour an, an welche wir Logs hängen können
        dummyTour = tourService.createTour("Dummy", "desc", "X", "Y", "Car", 100, 60);
    }

    @Test
    public void testValidationNoDate() {
        LogFormViewModel vm = new LogFormViewModel(tourService, dummyTour);
        vm.timeProperty().set("10:00");
        vm.commentProperty().set("A comment");
        vm.distanceProperty().set(5);
        vm.totalTimeProperty().set(60);
        vm.ratingProperty().set(3);

        vm.applyChanges();

        // Erwartung: Datum fehlt -> kann nicht speichern
        assertFalse(vm.canCreateProperty().get());
        assertTrue(vm.errorMessageProperty().get().contains("Please select a date"));
        // Log sollte nicht erstellt werden
        assertTrue(dummyTour.getLogs().isEmpty());
    }

    @Test
    public void testValidationInvalidTimeFormat() {
        LogFormViewModel vm = new LogFormViewModel(tourService, dummyTour);
        vm.dateProperty().set(LocalDate.now());
        vm.timeProperty().set("10-99"); // ungültig
        vm.commentProperty().set("Nice tour");
        vm.distanceProperty().set(5);
        vm.totalTimeProperty().set(60);
        vm.ratingProperty().set(4);

        vm.applyChanges();

        // Falsches Zeitformat -> Fehlermeldung
        assertFalse(vm.canCreateProperty().get());
        assertTrue(vm.errorMessageProperty().get().contains("Invalid time format"));
    }

    @Test
    public void testValidationRatingOutOfRange() {
        LogFormViewModel vm = new LogFormViewModel(tourService, dummyTour);
        vm.dateProperty().set(LocalDate.now());
        vm.timeProperty().set("10:00");
        vm.commentProperty().set("OK");
        vm.distanceProperty().set(5);
        vm.totalTimeProperty().set(60);
        vm.ratingProperty().set(99); // außerhalb 1..5

        vm.applyChanges();

        assertFalse(vm.canCreateProperty().get());
        assertTrue(vm.errorMessageProperty().get().contains("Rating must be between 1 and 5"));
    }

    @Test
    public void testCreateLogSuccess() {
        LogFormViewModel vm = new LogFormViewModel(tourService, dummyTour);
        vm.dateProperty().set(LocalDate.now());
        vm.timeProperty().set("12:00");
        vm.commentProperty().set("Perfect weather");
        vm.distanceProperty().set(5);
        vm.totalTimeProperty().set(60);
        vm.ratingProperty().set(5);

        vm.applyChanges();

        // Alles ist gültig -> canSave = true
        assertTrue(vm.canCreateProperty().get());
        // Log sollte in dummyTour.getLogs() erscheinen
        assertEquals(1, dummyTour.getLogs().size());
        assertEquals("Perfect weather", dummyTour.getLogs().get(0).getComment());
    }
}

