package at.fhtw.tourplannerserver.service;

import at.fhtw.tourplannercommon.dto.TourLogDto;
import at.fhtw.tourplannerserver.entity.TourEntity;
import at.fhtw.tourplannerserver.mapper.TourLogMapper;
import at.fhtw.tourplannerserver.repository.TourLogRepository;
import at.fhtw.tourplannerserver.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TourLogService {
    private static final Logger log = LogManager.getLogger(TourLogService.class);

    private final TourRepository tourRepo;
    private final TourLogRepository logRepo;
    private final TourLogMapper mapper;

    public List<TourLogDto> getLogs(UUID tourId) {
        log.debug("Get logs for tour {}", tourId);
        return logRepo.findByTour_Id(tourId).stream().map(mapper::toDto).toList();
    }

    public TourLogDto addLog(UUID tourId, TourLogDto dto) {
        TourEntity tour = tourRepo.findById(tourId)
                .orElseThrow(() -> {
                    log.warn("Cannot find tour with id {}", tourId);
                   return new IllegalArgumentException("Tour not found");
                });
        var entity = mapper.toEntity(dto);
        entity.setTour(tour);

        return mapper.toDto(logRepo.save(entity));
    }

    public TourLogDto updateLog(UUID tourId, UUID logId, TourLogDto dto) {
        var entity = logRepo.findById(logId)
                .orElseThrow(() -> {
                    log.warn("Cannot find log with id {}", logId);
                    return new IllegalArgumentException("Log not found");
                });
        if (!entity.getTour().getId().equals(tourId)) {
            log.warn("Cannot update log with id {}", logId);
            throw new IllegalArgumentException("Log does not belong to tour");
        }


        entity.setDateTime(dto.getDateTime());
        entity.setComment(dto.getComment());
        entity.setDifficulty(dto.getDifficulty());
        entity.setDistance(dto.getDistance());
        entity.setTotalTime(dto.getTotalTime());
        entity.setRating(dto.getRating());

        log.info("Updated log {}", logId);
        return mapper.toDto(entity);
    }

    public void deleteLog(UUID tourId, UUID logId) {
        var entity = logRepo.findById(logId)
                .orElseThrow(() -> {
                    log.warn("Cannot find log with id {} - DELETE could not be executed", logId);
                    return new IllegalArgumentException("Log not found");
                });
        if (!entity.getTour().getId().equals(tourId)) {
            log.warn("Cannot delete a log {} that belongs to another tour (Tour ID {} )", logId,  tourId);
            throw new IllegalArgumentException("Log does not belong to tour");
        }

        logRepo.delete(entity);
        log.info("Deleted log {} from tour {}", logId,  tourId);
    }
}