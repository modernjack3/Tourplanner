package at.fhtw.tourplanner.service;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplanner.dal.TourLogRepository;
import at.fhtw.tourplanner.dal.TourRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class TourService {
    private static final Logger logger = LogManager.getLogger(TourService.class);

    private final TourRepository tourRepository;
    private final TourLogRepository logRepository;

    //Durch Konstruktor-Injection im MainController wurden tatsächlich RemoteRepos übergeben
    public TourService(TourRepository tourRepository, TourLogRepository logRepository) {
        this.tourRepository = tourRepository;
        this.logRepository = logRepository;
        logger.debug("Created TourService");
    }

    // Tour- und Log-Operationen: reine Weiterleitung an das Repository

    // TOURS
    public List<Tour> getAllTours() {
        logger.debug("Getting all tours");
        return tourRepository.findAll();
    }

    public Tour createTour(String name, String description, String from, String to,
                           String transportType, double distance, double estimatedTime) {
        //String id = UUID.randomUUID().toString();
        //Tour t = new Tour(id, name);
        Tour t = new Tour(null, name);
        t.setDescription(description);
        t.setFromLocation(from);
        t.setToLocation(to);
        t.setTransportType(transportType);
        t.setDistance(distance);
        t.setEstimatedTime(estimatedTime);
        t.setImagePath("placeholder.png");

        tourRepository.insert(t);
        logger.info("Created Tour: {} ({} -> {})", name, from, to );
        return t;
    }

    public void updateTour(Tour tour) {
        tourRepository.update(tour);
        logger.info("Updated Tour: {}", tour.getName());
    }

    public void deleteTour(Tour tour) {
        tourRepository.delete(tour);
        logger.info("Deleted Tour: {}", tour.getName());
    }

    // TOUR-LOGS
    public void addLogToTour(Tour tour, TourLog log) {
        logRepository.insertLog(tour, log);
        logger.info("Added Log to Tour: {}", tour.getName());
    }

    public void updateLog(TourLog log) {
        logRepository.updateLog(log);
        logger.info("Log updated: {}", log.getId());
    }

    public void deleteLog(Tour tour, TourLog log) {
        logRepository.deleteLog(tour, log);
        logger.info("Deleted Log: {}", log.getId());
    }

    public TourLogRepository getLogRepository() {
        return logRepository;
    }

    // search tours by text
    public List<Tour> searchTours(String query) {
        if (query == null || query.trim().isEmpty()) {
            logger.debug("Search is empty, returning all tours");
            return getAllTours();
        }
        String lowerQ = query.trim().toLowerCase(); //trim() entfernt alle Leerzeichen
        logger.debug("Filtering tours by: \"{}\"", lowerQ);

        //Filterung per Stream/Pipeline
        return getAllTours().stream()
                .filter(t -> {
                    boolean match = t.getName().toLowerCase().contains(lowerQ)
                            || t.getDescription().toLowerCase().contains(lowerQ)
                            || t.getFromLocation().toLowerCase().contains(lowerQ)
                            || t.getToLocation().toLowerCase().contains(lowerQ);

                    if (match) {
                        System.out.println("Match: " + t.getName());
                    }
                    return match;
                })
                .collect(Collectors.toList());
    }

}
