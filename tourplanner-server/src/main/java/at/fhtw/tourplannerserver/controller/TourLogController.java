package at.fhtw.tourplannerserver.controller;

import at.fhtw.tourplannercommon.dto.TourLogDto;
import at.fhtw.tourplannerserver.service.TourLogService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tours/{tourId}/logs")
@RequiredArgsConstructor
@CrossOrigin
public class TourLogController {
    private static final Logger log = LogManager.getLogger(TourLogController.class);

    private final TourLogService service;

    @GetMapping
    public List<TourLogDto> all(@PathVariable("tourId") UUID tourId) {   // ← Name angegeben
        log.debug("Getting all tour logs for {}", tourId);
        return service.getLogs(tourId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TourLogDto add(@PathVariable("tourId") UUID tourId,
                          @RequestBody TourLogDto dto) {
        log.debug("Adding tour log {} for the tour {}", dto,  tourId);
        return service.addLog(tourId, dto);
    }

    @PutMapping("/{logId}")
    public TourLogDto update(@PathVariable("tourId") UUID tourId,
                             @PathVariable("logId") UUID logId,        // ← Name angegeben
                             @RequestBody TourLogDto dto) {
        log.debug("Updating tour log {} for the tour {}", dto,  tourId);
        return service.updateLog(tourId, logId, dto);
    }

    @DeleteMapping("/{logId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("tourId") UUID tourId,
                       @PathVariable("logId") UUID logId) {            // ← Name angegeben
        log.debug("Deleting tour log {} for the tour {}", logId, tourId);
        service.deleteLog(tourId, logId);
    }
}
