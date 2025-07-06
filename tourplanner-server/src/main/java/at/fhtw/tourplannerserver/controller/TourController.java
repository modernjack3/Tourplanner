package at.fhtw.tourplannerserver.controller;

import at.fhtw.tourplannercommon.dto.TourDto;
import at.fhtw.tourplannerserver.service.TourService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
Client ruft diese Endpunkte indirekt über TourRemoteRepository
Wandelt HTTP <--> Java Objekte (JSON via Spring)
 */
@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor  // injiziert final-Feld TourService
@CrossOrigin    // allow the JavaFX client (different origin) to call
public class TourController {
    private static final Logger log = LogManager.getLogger(TourController.class);

    private final TourService service;

    @GetMapping
    public List<TourDto> all() {
        log.debug("Getting all tours");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TourDto one(@PathVariable("id") UUID id) {
        log.debug("Getting tour {}", id);
        return service.getOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TourDto create(@RequestBody TourDto dto) {
        log.debug("Creating tour {}", dto);
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public TourDto update(@PathVariable("id") UUID id, @RequestBody TourDto dto) {
        log.debug("Updating tour {}", id);
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) {
        log.debug("Deleting tour {}", id);
        service.delete(id);
    }
}