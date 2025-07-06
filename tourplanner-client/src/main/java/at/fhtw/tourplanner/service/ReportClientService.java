package at.fhtw.tourplanner.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReportClientService {

    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final String BASE = "http://localhost:8080/reports";

    public Path downloadTourReport(String tourId) throws IOException, InterruptedException {
        return download(BASE + "/tour/" + tourId + ".pdf", "tour-" + tourId);
    }

    public Path downloadSummary() throws IOException, InterruptedException {
        return download(BASE + "/summary.pdf", "summary");
    }

    /* intern */
    private static Path download(String url, String prefix)
            throws IOException, InterruptedException {

        //Anlegen einer temporären .pdf
        Path tmp = Files.createTempFile(prefix, ".pdf");
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).build();

        //HTTP-Client schreibt den Response-Body direkt in die Datei
        HTTP.send(req, HttpResponse.BodyHandlers.ofFile(tmp));

        return tmp;
    }
}
