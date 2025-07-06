package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.TourLogRepository;
import at.fhtw.tourplanner.dal.TourRepository;
import at.fhtw.tourplanner.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddTourViewModelTest {

    private AddTourViewModel vm;
    private TourService tourService;

    @BeforeEach
    public void setUp() {
        // In-Memory Setup
        tourService = new TourService(new TourRepository(), new TourLogRepository());
        vm = new AddTourViewModel(tourService);
    }

    @Test
    public void testValidationNameIsRequired() {
        // Arrange
        vm.nameProperty().set("");
        vm.distanceProperty().set(100);
        vm.estimatedTimeProperty().set(50);

        // Act
        vm.createTour();

        // Assert -> canCreate is false, so no Tour is created
        assertFalse(vm.canCreateProperty().get());
        assertTrue(vm.errorMessageProperty().get().contains("Name is required"));
    }

    @Test
    public void testValidationNegativeDistance() {
        vm.nameProperty().set("My Tour");
        vm.distanceProperty().set(-10);
        vm.estimatedTimeProperty().set(30);

        vm.createTour();

        assertFalse(vm.canCreateProperty().get());
        assertTrue(vm.errorMessageProperty().get().contains("Distance cannot be negative"));
    }

    @Test
    public void testCreateTourSuccess() {
        // All valid data
        vm.nameProperty().set("Valid Tour");
        vm.descriptionProperty().set("Some desc");
        vm.fromProperty().set("A");
        vm.toProperty().set("B");
        vm.transportTypeProperty().set("Car");
        vm.distanceProperty().set(10);
        vm.estimatedTimeProperty().set(100);

        vm.createTour();

        assertTrue(vm.canCreateProperty().get());
        // Check if the Tour was actually created in the service
        assertEquals(1, tourService.getAllTours().size());
        assertEquals("Valid Tour", tourService.getAllTours().get(0).getName());
    }
}
