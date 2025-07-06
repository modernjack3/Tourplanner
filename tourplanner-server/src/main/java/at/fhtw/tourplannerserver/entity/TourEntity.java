package at.fhtw.tourplannerserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tours")
public class TourEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;
    private String fromLocation;
    private String toLocation;
    private String transportType;
    private double distance;
    private double estimatedTime;
    private String imagePath;

    // orphanRemoval = true garantiert, dass Logs die aus der Liste entfertn werden auch in der DB gelöscht werden
    // LAZY verhindet, dass beim Laden einer Tour automatisch alle Logs gezogen werden. Erst wenn
    // der Client sie wirklich braucht werden sie selektib nachgeladen
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TourLogEntity> logs = new ArrayList<>();
}