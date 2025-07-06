package at.fhtw.tourplanner.service;

import at.fhtw.tourplanner.dal.TourLogRepository;
import at.fhtw.tourplanner.dal.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TourServiceTest {
    private TourService tourService;

    @BeforeEach
    public void setUp() {
        //Neue, leere In-Memory-Repos für jeden Test
        TourRepository tourRepository = new TourRepository();
        TourLogRepository tourLogRepository = new TourLogRepository();
        tourService = new TourService(tourRepository, tourLogRepository);
    }

    @Test
    public void testCreateTour() {
        // Arrange
        String name = "My Tour";
        String desc = "A short description";

        // Act
        Tour created = tourService.createTour(name, desc, "Vienna", "Graz", "Car", 200, 120);

        // Assert
        assertNotNull(created.getId());
        assertEquals(name, created.getName());
        assertEquals(desc, created.getDescription());
        // Überprüfen, ob Tour in getAllTours() erscheint
        List<Tour> allTours = tourService.getAllTours();
        assertTrue(allTours.contains(created));
    }

    @Test
    public void testDeleteTour() {
        // Arrange
        Tour t1 = tourService.createTour("Tour A", "desc", "A", "B", "Car", 100, 60);
        Tour t2 = tourService.createTour("Tour B", "desc", "C", "D", "Bike", 50, 30);

        // Act
        tourService.deleteTour(t1);

        // Assert
        List<Tour> allTours = tourService.getAllTours();
        assertFalse(allTours.contains(t1));
        assertTrue(allTours.contains(t2));
    }

    @Test
    public void testSearchTours() {
        // Arrange
        Tour t1 = tourService.createTour("City Tour", "Enjoy city center", "Vienna", "Vienna", "Walk", 5, 120);
        Tour t2 = tourService.createTour("Mountain Hike", "Alps climbing", "Innsbruck", "Peak", "Hike", 15, 240);

        // Act
        List<Tour> result1 = tourService.searchTours("city");
        List<Tour> result2 = tourService.searchTours("alps");
        List<Tour> resultEmpty = tourService.searchTours("xxxxxxx");

        // Assert
        assertTrue(result1.contains(t1));
        assertFalse(result1.contains(t2));

        assertTrue(result2.contains(t2));
        assertFalse(result2.contains(t1));

        assertTrue(resultEmpty.isEmpty());
    }

}
