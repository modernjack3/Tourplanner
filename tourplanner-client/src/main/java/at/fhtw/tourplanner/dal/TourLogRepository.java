package at.fhtw.tourplanner.dal;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;

import java.util.UUID;

public class TourLogRepository {

    public void insertLog(Tour tour, TourLog log) {
        // generate unique ID, then add to the tour
        log.setId(UUID.randomUUID().toString());
        tour.getLogs().add(log);
    }

    public void updateLog(TourLog log) {
        // in-memory reference; do nothing special
    }

    public void deleteLog(Tour tour, TourLog log) {
        tour.getLogs().remove(log);
    }
}
