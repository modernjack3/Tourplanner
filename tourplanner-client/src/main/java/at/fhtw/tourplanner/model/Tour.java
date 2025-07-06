package at.fhtw.tourplanner.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Tour {
    private String id;               // or use UUID
    private String name;
    private String description;
    private String fromLocation;
    private String toLocation;
    private String transportType;
    private double distance;
    private double estimatedTime;
    private String imagePath;        // placeholder for the map image

    private List<TourLog> logs = new ArrayList<>();

    public Tour(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
