package at.fhtw.tourplannerserver.repository;

import at.fhtw.tourplannerserver.entity.TourLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TourLogRepository extends JpaRepository<TourLogEntity, UUID> {
    List<TourLogEntity> findByTour_Id(UUID tourId);
}