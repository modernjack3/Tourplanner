package at.fhtw.tourplannerserver;

import at.fhtw.tourplannercommon.dto.TourDto;
import at.fhtw.tourplannerserver.service.TourService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional   // jede Methode bekommt saubere DB
public class TourServiceTest {
    //Automatisch Abhängigkeitsinjektion in einer Klasse
    @Autowired TourService service;

    // create & getAll
    @Test
    void getAll() {
        // Arrange
        int initialSize = service.getAll().size();
        TourDto dto = new TourDto();
        dto.setName("Alps");
        // Act
        service.create(dto);
        List<TourDto> list = service.getAll();
        // Assert
        assertEquals(initialSize + 1, list.size());
        assertTrue(list.stream().anyMatch(t -> "Alps".equals(t.getName())));
    }

    // getOne
    @Test
    void getOneById() {
        TourDto dto = new TourDto();
        dto.setName("City");
        UUID id = service.create(dto).getId();

        TourDto loaded = service.getOne(id);

        assertEquals("City", loaded.getName());
    }

    // getOne - not found
    @Test
    void getOneNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getOne(UUID.randomUUID()));
    }

    // update
    @Test
    void updateChangesFields() {
        TourDto dto = new TourDto();
        dto.setName("Old");
        UUID id = service.create(dto).getId();

        TourDto update = new TourDto();
        update.setName("New");
        TourDto result = service.update(id, update);

        assertEquals("New", result.getName());
    }

    // delete
    @Test
    void deleteRemovesTour() {
        int initialSize = service.getAll().size();
        TourDto dto = new TourDto();
        dto.setName("Temp");
        UUID id = service.create(dto).getId();

        service.delete(id);

        assertEquals(initialSize, service.getAll().size());
        assertFalse(service.getAll().stream().anyMatch(t -> t.getId().equals(id)));
    }


}
