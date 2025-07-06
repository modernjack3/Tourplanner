package at.fhtw.tourplanner.service;

import at.fhtw.tourplanner.Main;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import javafx.application.HostServices;


import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapService {
    private static final Logger log  = LogManager.getLogger(MapService.class);

    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final ObjectMapper JSON = new ObjectMapper();

    private final HostServices host;

    /* Pfad der statischen Leaflet‑Vorlage aus den Ressourcen */
    private static final String HTML_TEMPLATE = "/leaflet/leaflet.html";

    //Datentransfer-Objekt für Auto-Completion bei den Adressen
    // record == POJO (nur für Daten-Transfer
    public record Suggestion(String label, double lon, double lat) { }

    public MapService() {
        this.host = Main.getHostServicesInstance();
        log.debug("MapService initialized. HostServices available: {}", host != null);
    }

    /* -------- 1. Adresse → Koordinaten (Proxy‑Endpoint) -------- */

    // Ruft einen Proxy-Endpoint des Spring-Servers auf (/api/ors/geocode), der
    //die Anfrage an OpenRouteService weiterleitet.
    //Liefert bis zu fünf Vorschläge für das Auto-Complete-Popup
    public List<Suggestion> suggest(String query) throws Exception {
        String url = "http://localhost:8080/api/ors/geocode?address=" +
                URLEncoder.encode(query, StandardCharsets.UTF_8);

        log.debug("ORS-Geocode URL: {}", url);

        JsonNode root = JSON.readTree(HTTP.send(
                HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        ).body());

        ArrayNode feats = (ArrayNode) root.at("/features");
        List<Suggestion> result = new ArrayList<>();
        for (JsonNode f : feats) {
            String label = f.at("/properties/label").asText();
            double lon = f.at("/geometry/coordinates/0").asDouble();
            double lat = f.at("/geometry/coordinates/1").asDouble();
            result.add(new Suggestion(label, lon, lat));
            if (result.size() == 5) break;                 // max. 5 Einträge
        }
        log.debug("Suggestions for \"{}\" found: {}", query, result);
        return result;
    }
    //Ein Volltreffer für die spätere Routenberechnung
    public double[] geocode(String address) throws Exception {
        List<Suggestion> list = suggest(address);
        if (list.isEmpty()) {
            log.warn("No coordinates found for \"{}\"", address);
            throw new IllegalStateException("Keine Koordinaten gefunden");
        }

        Suggestion best = list.get(0); // erste (beste) Wahl
        log.info("Geocode target: \"{}\" -> [{},{}]", best.label, best.lon, best.lat);
        return new double[]{best.lon(), best.lat()};
    }
    public JsonNode geocodeRaw(String address) throws Exception {
        String url = "http://localhost:8080/api/ors/geocode?address=" +
                URLEncoder.encode(address, StandardCharsets.UTF_8);
        String json = HTTP.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                HttpResponse.BodyHandlers.ofString()).body();
        return JSON.readTree(json);

    }


    //-------- 2. Directions (Proxy‑Endpoint)
    // Sendet ein JSON-Array der Koordinaten: POST /api/ors/direction
    // Rücgabe: GeoJSON-ähnlicher Baum
    public JsonNode directions(double[] from, double[] to, String transportType) {
        try {
            String coords = "[[" + from[0] + "," + from[1] + "],"
                    + "[" + to[0]   + "," + to[1]   + "]]";

            HttpRequest req = HttpRequest.newBuilder(
                            URI.create("http://localhost:8080/api/ors/directions?transportType="+ transportType))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(coords))
                    .build();

            String json = HTTP.send(req, HttpResponse.BodyHandlers.ofString()).body();
            return JSON.readTree(json);
        } catch (Exception e) {
            log.error("Directions-Request failed: {}", e.getMessage(), e);
            throw new RuntimeException("Directions failed", e);
        }
    }

    //------- 3. Leaflet‑Seite vorbereiten & Browser öffnen
    //1.Kopiert eine statische leaflet.html Vorlage in ein Temp-Verzeichnis
    //2.Schreibt die Response als direction.js
    //3.Öffnet die HTML im Default-Browser
    public void showOnMap(JsonNode geoJson) {
        try {
            // tmp‑Verzeichnis erstellen
            Path tmpDir = Files.createTempDirectory("leaflet-map");
            tmpDir.toFile().deleteOnExit();

            // leaflet.html aus Ressourcen kopieren
            try (var in = getClass().getResourceAsStream(HTML_TEMPLATE)) {
                Files.copy(in, tmpDir.resolve("leaflet.html"),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            // directions.js erzeugen
            String js = "var directions = " +
                    JSON.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(geoJson) + ";";
            Files.writeString(tmpDir.resolve("directions.js"), js, StandardOpenOption.CREATE);

            // Default‑Browser öffnen
            host.showDocument(tmpDir.resolve("leaflet.html").toUri().toString());
            log.info("Leaflet Map opened: {}", tmpDir);

        } catch (IOException ex) {
            log.error("Cannot create leaflet map: {}", ex.getMessage(), ex);
            throw new RuntimeException("Cannot create Leaflet files", ex);
        }
    }
}
