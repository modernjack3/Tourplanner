package at.fhtw.tourplannerserver.service;

import at.fhtw.tourplannercommon.dto.TourDto;
import at.fhtw.tourplannercommon.dto.TourLogDto;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.*;


//Baut HTML mit Tymeleaf und rendert es anschließend mit OpetHTMLtoPDF
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final Logger log = LogManager.getLogger(ReportService.class);

    private final SpringTemplateEngine thymeleaf;
    private final TourService tours;
    private final TourLogService logs;

    //Einzel‑Tour Report
    public byte[] buildTourReport(UUID tourId) {

        TourDto tour = tours.getOne(tourId);
        List<TourLogDto> lgs = logs.getLogs(tourId);

        Context ctx = new Context(Locale.GERMAN);
        ctx.setVariable("tour", tour);
        ctx.setVariable("logs", lgs);
        ctx.setVariable("fmtDate", "dd.MM.yyyy HH:mm");

        String html = thymeleaf.process("tour-report", ctx);
        log.debug("HTML for tour {} generated ({} bytes)", tourId, html.length());

        return renderPdf(html);
    }

    // Summary
    public byte[] buildSummaryReport() {

        List<TourDto> allTours = tours.getAll();
        List<SummaryRow> rows = new ArrayList<>();

        for (TourDto t : allTours) {
            List<TourLogDto> l = logs.getLogs(t.getId());
            rows.add(new SummaryRow(t, l));
        }

        Context ctx = new Context(Locale.GERMAN);
        ctx.setVariable("rows", rows);

        String html = thymeleaf.process("summary-report", ctx);
        log.debug("HTML summary generated ({} bytes)", html.length());

        return renderPdf(html);
    }

    // intern helper
    private static byte[] renderPdf(String html) {
        try (var out = new ByteArrayOutputStream()) {
            new PdfRendererBuilder()
                    .useFastMode()
                    .withHtmlContent(html, null)
                    .toStream(out)
                    .run();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("PDF rendering failed", ex);
        }
    }

    /* Hilfsklasse für Summary‑Tabelle */
    public record SummaryRow(
            TourDto tour,
            double avgDistance,
            double avgTime,
            double avgRating,
            int numLogs) {

        SummaryRow(TourDto tour, List<TourLogDto> logs) {
            this(tour,
                    logs.stream().mapToDouble(TourLogDto::getDistance ).average().orElse(0),
                    logs.stream().mapToDouble(TourLogDto::getTotalTime).average().orElse(0),
                    logs.stream().mapToInt(TourLogDto::getRating).average().orElse(0),
                    logs.size());
        }
    }
}
