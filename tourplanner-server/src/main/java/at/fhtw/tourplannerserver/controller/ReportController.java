package at.fhtw.tourplannerserver.controller;

import at.fhtw.tourplannerserver.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@CrossOrigin           // erlaubt Aufruf aus JavaFX‑Client
public class ReportController {

    private static final Logger log = LogManager.getLogger(ReportController.class);

    private final ReportService reports;

    //Baut aus dem Byte-Array ein ResponseEntity<byte[]> mit Content-Disposition: attachment
    // und der Client speichert die Datei per ReportClientService in ein temp-Verzeichnis und öffnet
    @GetMapping(value = "/tour/{id}.pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> tour(@PathVariable("id") UUID id) {
        log.info("Generate tour‑report {}", id);
        byte[] pdf = reports.buildTourReport(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=tour-" + id + ".pdf")
                .body(pdf);
    }

    @GetMapping(value = "/summary.pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> summary() {
        log.info("Generate summary‑report");
        byte[] pdf = reports.buildSummaryReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=summary.pdf")
                .body(pdf);
    }
}
