package at.fhtw.tourplanner.remote;

import at.fhtw.tourplanner.model.Tour;

import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplannercommon.dto.TourDto;
import at.fhtw.tourplannercommon.dto.TourLogDto;

import java.util.UUID;


/*Manuelle Mapper-Klasse für die Knvetierung zwischen
Client-Modellen Tour und TourLog, und den DTOs, welche über
REST übertragen werden.
 */
public class DtoMapper {

    private DtoMapper() {}

    public static Tour toModel(TourDto dto) {
        Tour t = new Tour(
                dto.getId() == null ? null : dto.getId().toString(),
                dto.getName()
        );
        t.setDescription (dto.getDescription());
        t.setFromLocation (dto.getFromLocation());
        t.setToLocation (dto.getToLocation());
        t.setTransportType (dto.getTransportType());
        t.setDistance (dto.getDistance());
        t.setEstimatedTime (dto.getEstimatedTime());
        t.setImagePath (dto.getImagePath());
        return t;
    }

    public static TourDto toDto(Tour m) {
        TourDto dto = new TourDto();
        // Bei neu angelegten Tours ist id eventuell noch null/leer
        if (m.getId() != null && !m.getId().isBlank()) {
            dto.setId(java.util.UUID.fromString(m.getId()));
        }
        dto.setName (m.getName());
        dto.setDescription (m.getDescription());
        dto.setFromLocation (m.getFromLocation());
        dto.setToLocation (m.getToLocation());
        dto.setTransportType (m.getTransportType());
        dto.setDistance (m.getDistance());
        dto.setEstimatedTime (m.getEstimatedTime());
        dto.setImagePath (m.getImagePath());
        return dto;
    }

    // Mappt DTo ohne Tour-Referenz
    public static TourLog toModel(TourLogDto dto) {
        TourLog log = new TourLog(dto.getId() == null ? null : dto.getId().toString());
        log.setDateTime(dto.getDateTime());
        log.setComment(dto.getComment());
        log.setDifficulty(dto.getDifficulty());
        log.setDistance(dto.getDistance());
        log.setTotalTime(dto.getTotalTime());
        log.setRating(dto.getRating());
        return log;
    }

    // Variante, die direkt die Tour-Referenz setzt
    public static TourLog toModel(TourLogDto dto, Tour parent) {
        TourLog log = toModel(dto);
        log.setTour(parent);
        return log;
    }

    public static TourLogDto toDto(TourLog log) {
        TourLogDto dto = new TourLogDto();
        if (log.getId() != null && !log.getId().isBlank())
            dto.setId(UUID.fromString(log.getId()));
        dto.setDateTime(log.getDateTime());
        dto.setComment(log.getComment());
        dto.setDifficulty(log.getDifficulty());
        dto.setDistance(log.getDistance());
        dto.setTotalTime(log.getTotalTime());
        dto.setRating(log.getRating());
        return dto;
    }

}
