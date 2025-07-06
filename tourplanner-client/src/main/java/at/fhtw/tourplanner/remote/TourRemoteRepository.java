package at.fhtw.tourplanner.remote;

import at.fhtw.tourplannercommon.dto.TourDto;
import at.fhtw.tourplanner.dal.TourRepository;
import at.fhtw.tourplanner.model.Tour;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


//REST-Adapter
public class TourRemoteRepository extends TourRepository {
    private static final Logger logger = LogManager.getLogger(TourRemoteRepository.class);

    private static final String BASE_URL = "http://localhost:8080/api/tours";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper;

    public TourRemoteRepository() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        logger.info("TourRemoteRepository initialized with BASE_URL={}", BASE_URL);
    }

    // Überschreibt die In‑Memory‑Implementierungen
    //b HTTP GET /api/tours -> JSON-Liste -> TourDto -> Tour mittels Mapper
    @Override
    public List<Tour> findAll() {
        logger.debug("HTTP GET {}", BASE_URL);
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL)).GET().build();
        try {
            String json = client.send(req, HttpResponse.BodyHandlers.ofString()).body();
            List<TourDto> dtos = mapper.readValue(json, new TypeReference<>() {});
            List<Tour> result = new ArrayList<>();
            dtos.forEach(dto -> result.add(DtoMapper.toModel(dto)));
            logger.info("Loaded {} tours", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Unable to load tours", e);
            throw new RuntimeException("Unable to load tours", e);
        }
    }

    @Override
    public void insert(Tour tour) {
        try {
            String body = mapper.writeValueAsString(DtoMapper.toDto(tour));
            logger.debug("HTTP POST {} -> {}", BASE_URL, body);

            HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            System.out.println("POST body = " + body);

            // String resp = client.send(req, HttpResponse.BodyHandlers.ofString()).body();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() >= 300) {
                logger.warn("Server returned {} {}", resp.statusCode(), resp.body());
                throw new RuntimeException("Server returned " + resp.statusCode() + ": " + resp.body());
            }
            // Server liefert die komplette Tour mit ID zurück → aktualisieren
            TourDto saved = mapper.readValue(resp.body(), TourDto.class);
            Tour updated = DtoMapper.toModel(saved);
            // Kopiere die generierte ID zurück ins übergebene Objekt
            tour.setId(updated.getId());
            logger.info("Tour created: {} (id={})", tour.getName(), tour.getId());

        } catch (Exception e) {
            logger.error("Unable to create tour", e);
            throw new RuntimeException("Unable to create tour", e);
        }
    }

    @Override
    public void update(Tour tour) {
        try {
            String body = mapper.writeValueAsString(DtoMapper.toDto(tour));
            HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL + "/" + tour.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            client.send(req, HttpResponse.BodyHandlers.discarding());
            logger.info("Tour updated: {}", tour.getName());
        } catch (Exception e) {
            logger.error("Unable to update tour {}", tour.getId(), e);
            throw new RuntimeException("Unable to update tour", e);
        }
    }

    @Override
    public void delete(Tour tour) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL + "/" + tour.getId()))
                    .DELETE()
                    .build();
            client.send(req, HttpResponse.BodyHandlers.discarding());
            logger.info("Tour deleted: {}", tour.getName());
        } catch (Exception e) {
            logger.error("Unable to delete tour {}", tour.getId(), e);
            throw new RuntimeException("Unable to delete tour", e);
        }
    }
}
