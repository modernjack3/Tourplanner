package at.fhtw.tourplannercommon.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TourDto {
    private UUID id;
    private String name;
    private String description;
    private String fromLocation;
    private String toLocation;
    private String transportType;
    private double distance;
    private double estimatedTime;
    private String imagePath; // URL or relative path
}
