package at.fhtw.tourplannerserver.service;

import at.fhtw.tourplannerserver.config.OpenRouteConfig;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

//Proxy zu OpenRouteService
@Service
@RequiredArgsConstructor
public class OrsService {

    private static final Logger log = LogManager.getLogger(OrsService.class);

    private final OpenRouteConfig cfg;
    private final RestTemplate rest = new RestTemplate();

    // Geocoding
    public String geocode(String address) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.openrouteservice.org/geocode/search")
                .queryParam("api_key", cfg.getApiKey())
                .queryParam("text", address)//URLEncoder.encode(address, StandardCharsets.UTF_8))   // UriComponentsBuilder encodiert korrekt
                .queryParam("size", 5)
                .toUriString();

        log.debug("GEOCODE -> {}", url);

        //UriComponentsBuilder -> Spring erledigt URL-Encoding
        URI uri = URI.create(url);
        String json = rest.getForObject(uri, String.class);
        log.trace("GEOCODE <- {}", json);

        return json;
    }

    // Directions
    public String directions(String coordinates, String transportType) {
        String url = "https://api.openrouteservice.org/v2/directions/" + transportType + "/geojson";

        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", cfg.getApiKey());
        h.setContentType(MediaType.APPLICATION_JSON);

        String body = """
                      {"coordinates": %s}
                      """.formatted(coordinates);

        log.debug("DIRECTIONS -> {}, body={}", url, body);

        try {
            String json = rest.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(body, h),
                    String.class).getBody();
            log.trace("DIRECTIONS <- {}", json);

            return json;
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("DIRECTIONS X {}", e.getResponseBodyAsString());
            throw new RuntimeException("Routing failded: " + e.getResponseBodyAsString(), e);
        }

    }
}
