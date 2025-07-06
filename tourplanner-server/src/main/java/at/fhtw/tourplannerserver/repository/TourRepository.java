package at.fhtw.tourplannerserver.repository;

import at.fhtw.tourplannerserver.entity.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
/*
Kapselt alle DB-Zugriffe
 */
public interface TourRepository extends JpaRepository<TourEntity, UUID> {
}