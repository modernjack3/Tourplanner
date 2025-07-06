package at.fhtw.tourplannerserver.controller;

import at.fhtw.tourplannerserver.service.OrsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;


//Proxy zu OpenRouteService
@RestController
@RequestMapping("/api/ors")
@RequiredArgsConstructor
@CrossOrigin
public class OrsController {
    private static final Logger log = LogManager.getLogger(OrsController.class);

    private final OrsService ors;

    @GetMapping("/geocode")
    public String geocode(@RequestParam("address") String address) {
        log.debug("GET /api/ors/geocode address={}", address);
        String response = ors.geocode(address);
        log.trace("Geocode-Result: {}", response);
        return response;
    }

    @PostMapping("/directions")
    public String directions(@RequestBody String coordinates, @RequestParam("transportType") String transportType) {
        log.debug("POST /api/ors/directions transportType={} coords={}", transportType, coordinates);
        String response = ors.directions(coordinates, transportType);
        log.trace("Directions-Result: {}", response);
        return response;
    }
}
