package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.model.Tour;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TourRepository {

    private final List<Tour> tours = new ArrayList<>();

    public List<Tour> findAll() {
        return tours;
    }

    public Optional<Tour> findById(String id) {
        return tours.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public void insert(Tour tour) {
        if (tour.getId() == null || tour.getId().isBlank()) {
            tour.setId(UUID.randomUUID().toString());
        }
        tours.add(tour);
    }

    public void update(Tour tour) {
        // In-memory: do nothing special
    }

    public void delete(Tour tour) {
        tours.remove(tour);
    }
}
