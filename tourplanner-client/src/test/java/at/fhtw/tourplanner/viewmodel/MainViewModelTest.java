package at.fhtw.tourplanner.viewmodel;

import at.fhtw.tourplanner.dal.TourLogRepository;
import at.fhtw.tourplanner.dal.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.service.TourService;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainViewModelTest {

    private MainViewModel mainViewModel;
    private TourService tourService;

    @BeforeEach
    public void setUp() {
        TourRepository tourRepo = new TourRepository();
        TourLogRepository logRepo = new TourLogRepository();
        tourService = new TourService(tourRepo, logRepo);

        mainViewModel = new MainViewModel(tourService);
    }

    @Test
    public void testFilterToursEmptyQuery() {
        // Arrange
        Tour t1 = tourService.createTour("Tour A", "desc A", "X", "Y", "Car", 100, 60);
        Tour t2 = tourService.createTour("Tour B", "desc B", "Z", "W", "Bike", 50, 30);

        // Act
        mainViewModel.filterTours("");

        // Assert
        ObservableList<Tour> tours = mainViewModel.getToursObservable();
        assertTrue(tours.contains(t1));
        assertTrue(tours.contains(t2));
    }

    @Test
    public void testFilterToursWithQuery() {
        // Arrange
        Tour t1 = tourService.createTour("City Tour", "desc city", "Vienna", "Vienna", "Car", 5, 120);
        Tour t2 = tourService.createTour("Mountain Tour", "desc mountain", "Innsbruck", "Peak", "Hike", 15, 240);

        // Act
        mainViewModel.filterTours("city");

        // Assert
        ObservableList<Tour> tours = mainViewModel.getToursObservable();
        assertTrue(tours.contains(t1));
        assertFalse(tours.contains(t2));
    }


    @Test
    public void testDeleteTourInViewModel() {
        // Arrange
        Tour t1 = tourService.createTour("Tour A", "desc", "A", "B", "Car", 100, 60);
        mainViewModel.filterTours("");  // load all

        // Act
        mainViewModel.deleteTour(t1);

        // Assert
        assertFalse(mainViewModel.getToursObservable().contains(t1));
        // check also in the service
        assertFalse(tourService.getAllTours().contains(t1));
    }
}