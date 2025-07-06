package at.fhtw.tourplanner.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TourLog {
    private String id;
    private LocalDateTime dateTime;
    private String comment;
    private String difficulty;
    private double totalTime;
    private double distance;
    private int rating;     // 1 to 5

    // Rückverweis auf die Tour – keine JSON‑Serialisierung nötig.
    // transient = wird nicht in DTO gemappt
    private transient Tour tour;

    public TourLog(String id) {
        this.id = id;
    }
}
