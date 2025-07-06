package at.fhtw.tourplannerserver;

import at.fhtw.tourplannercommon.dto.TourDto;
import at.fhtw.tourplannercommon.dto.TourLogDto;
import at.fhtw.tourplannerserver.service.TourLogService;
import at.fhtw.tourplannerserver.service.TourService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TourLogServiceTest {
    @Autowired TourService tours;
    @Autowired TourLogService logs;

    /* Helper */
    private UUID newTour() {
        TourDto dto = new TourDto(); dto.setName("Tour");
        return tours.create(dto).getId();
    }

    // add & getLogs
    @Test
    void addLogThenRetrieve() {
        // Arrange
        UUID tourId = newTour();
        TourLogDto log = new TourLogDto();
        log.setComment("Nice");
        log.setDateTime(LocalDateTime.now());

        // Act
        logs.addLog(tourId, log);
        List<TourLogDto> list = logs.getLogs(tourId);

        // Assert
        assertEquals(1, list.size());
        assertEquals("Nice", list.get(0).getComment());
    }

    //pdate wrong tour
    @Test
    void updateLogWrongTour() {
        UUID tourA = newTour();
        UUID tourB = newTour();

        TourLogDto log = new TourLogDto();
        UUID logId = logs.addLog(tourA, log).getId();

        TourLogDto update = new TourLogDto();
        update.setComment("X");

        assertThrows(IllegalArgumentException.class,
                () -> logs.updateLog(tourB, logId, update));
    }

    // deleteLog
    @Test
    void deleteLogRemovesEntry() {
        UUID tourId = newTour();
        UUID logId = logs.addLog(tourId, new TourLogDto()).getId();

        logs.deleteLog(tourId, logId);
        assertTrue(logs.getLogs(tourId).isEmpty());
    }
}
