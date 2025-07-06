package at.fhtw.tourplanner.remote;

import at.fhtw.tourplannercommon.dto.TourLogDto;
import at.fhtw.tourplanner.dal.TourLogRepository;
import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*Remote-Implementierung für Tour-Logs.
Ruft das Spring-BAckend via REST auf und hält die Log-Liste
der zugehörigen Tour aktuell.
 */
public class TourLogRemoteRepository extends TourLogRepository {
    private static final Logger logger = LogManager.getLogger(TourLogRemoteRepository.class);

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    public TourLogRemoteRepository() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        logger.debug("TourLogRemoteRepository initialised");
    }

    private static String base(UUID tourId) {
        return "http://localhost:8080/api/tours/" + tourId + "/logs";
    }

    // Hilfsmethode: Response prüfen

    private static void ensureSuccess(HttpResponse<String> resp) {
        if (resp.statusCode() >= 300) {
            throw new RuntimeException("Server responded " + resp.statusCode()
                    + ":\n" + resp.body());
        }
    }

    @Override
    public void insertLog(Tour tour, TourLog log) {
        try {
            String body = mapper.writeValueAsString(DtoMapper.toDto(log));
            HttpRequest req = HttpRequest.newBuilder(URI.create(base(UUID.fromString(tour.getId()))))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            ensureSuccess(resp);

            TourLogDto saved = mapper.readValue(resp.body(), TourLogDto.class);
            TourLog savedModel = DtoMapper.toModel(saved, tour);  // Tour-Referenz setzen
            log.setId(savedModel.getId());     // ID zurückkopieren
            tour.getLogs().add(savedModel);    // Liste aktuell halten

            logger.info("Log added (tour={}, id={})", tour.getName(), log.getId());
        } catch (Exception e) {
            logger.error("Add log failed (tour={})", tour.getName(), e);
            throw new RuntimeException("Add log failed", e);
        }
    }

    @Override
    public void updateLog(TourLog log) {
        try {
            String body = mapper.writeValueAsString(DtoMapper.toDto(log));
            HttpRequest req = HttpRequest.newBuilder(URI.create(
                            base(UUID.fromString(log.getTour().getId())) + "/" + log.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            ensureSuccess(resp);

            logger.info("Log updated (id={})", log.getId());
        } catch (Exception e) {
            logger.error("Update log failed (id={})", log.getId(), e);
            throw new RuntimeException("Update log failed", e);
        }
    }

    @Override
    public void deleteLog(Tour tour, TourLog log) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(
                            base(UUID.fromString(tour.getId())) + "/" + log.getId()))
                    .DELETE().build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            ensureSuccess(resp);

            //Liste aktuell halten
            tour.getLogs().remove(log);

            logger.info("Log deleted (id={})", log.getId());
        } catch (Exception e) {
            logger.error("Delete log failed (id={})", log.getId(), e);
            throw new RuntimeException("Delete log failed", e);
        }
    }

    // Hilfs‑Methode zum Nachladen: Lädt alle Logs einer Tour frisch vom
    // Server und ersetzt die aktuelle Liste in Tour.
    public List<TourLog> fetchLogs(Tour tour) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(base(UUID.fromString(tour.getId()))))
                    .GET().build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            ensureSuccess(resp);

            List<TourLogDto> dtos = mapper.readValue(resp.body(), new TypeReference<>() {});
            List<TourLog> logs = new ArrayList<>();
            for (TourLogDto dto : dtos) {
                logs.add(DtoMapper.toModel(dto, tour)); // Tour-Referenz setzen
            }
            tour.setLogs(logs);
            logger.info("Fetched {} logs for tour {}", logs.size(), tour.getName());

            return logs;
        } catch (Exception e) {
            logger.error("Load logs failed (tour={})", tour.getName(), e);
            throw new RuntimeException("Load logs failed", e);
        }
    }
}
