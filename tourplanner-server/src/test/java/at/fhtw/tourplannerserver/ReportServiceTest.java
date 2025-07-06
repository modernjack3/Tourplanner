package at.fhtw.tourplannerserver;
import at.fhtw.tourplannercommon.dto.TourDto;
import at.fhtw.tourplannerserver.service.ReportService;
import at.fhtw.tourplannerserver.service.TourService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReportServiceTest {
    @Autowired TourService tours;
    @Autowired ReportService reports;

    //tour‑report not empty
    @Test
    void buildTourReport_returnsPdfBytes() throws Exception {
        // Arrange
        TourDto dto = new TourDto(); dto.setName("Report Tour");
        UUID id = tours.create(dto).getId();

        // Act
        byte[] pdf = reports.buildTourReport(id);

        // Assert
        assertTrue(pdf.length > 500);
    }

    // summary contains tour name
    @Test
    void summaryReportContainsName() throws Exception {
        // Arrange
        TourDto dto = new TourDto(); dto.setName("ListMe");
        tours.create(dto);

        // Act
        byte[] pdf = reports.buildSummaryReport();

        // Assert – simple text extraction
        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(pdf))) {
            String text = new PDFTextStripper().getText(doc);
            assertTrue(text.contains("ListMe"));
        }
    }
}
