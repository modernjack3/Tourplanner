package at.fhtw.tourplannercommon.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TourLogDto {
    private UUID id;
    private LocalDateTime dateTime;
    private String comment;
    private String difficulty;
    private double totalTime;
    private double distance;
    private int rating;
}