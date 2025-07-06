package at.fhtw.tourplannerserver.service;

import at.fhtw.tourplannercommon.dto.TourDto;
import at.fhtw.tourplannerserver.mapper.TourMapper;
import at.fhtw.tourplannerserver.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/*
Bildet das Herz der Business-Logik
Jede öffentliche Methode läuft standardmäßig in einer Datenbank-Transaktion
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TourService {

    private static final Logger log = LogManager.getLogger(TourService.class);

    private final TourRepository repo;
    private final TourMapper mapper;

    public List<TourDto> getAll() {
        log.debug("Getting all tours");
        return repo.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public TourDto getOne(UUID id) {
        log.debug("Getting tour {}", id);
        return repo.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                    log.warn("No tour found with id {}", id);
                    return new IllegalArgumentException("Tour not found");
                });
    }

    public TourDto create(TourDto dto) {
        log.info("Creating new tour {}", dto.getName());
        return mapper.toDto(repo.save(mapper.toEntity(dto)));
    }

    public TourDto update(UUID id, TourDto dto) {
        log.info("Updating tour {}", id);
        var entity = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("No tour found with id {} (Update)", id);
                    return new IllegalArgumentException("Tour not found");
                });
        // mapstruct should update in‑place, but just in case:
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setFromLocation(dto.getFromLocation());
        entity.setToLocation(dto.getToLocation());
        entity.setTransportType(dto.getTransportType());
        entity.setDistance(dto.getDistance());
        entity.setEstimatedTime(dto.getEstimatedTime());
        entity.setImagePath(dto.getImagePath());
        return mapper.toDto(entity);
    }

    public void delete(UUID id) {
        log.info("Deleting tour {}", id);
        repo.deleteById(id);
    }
}