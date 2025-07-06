package at.fhtw.tourplannerserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tour_logs")
public class TourLogEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private LocalDateTime dateTime;
    private String comment;
    private String difficulty;
    private double totalTime;
    private double distance;
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    private TourEntity tour;
}