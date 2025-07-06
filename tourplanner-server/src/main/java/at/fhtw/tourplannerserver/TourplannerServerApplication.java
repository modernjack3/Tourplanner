package at.fhtw.tourplannerserver;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TourplannerServerApplication {
    private static final Logger log = LogManager.getLogger(TourplannerServerApplication.class);

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        SpringApplication.run(TourplannerServerApplication.class, args);
        log.info("TourplannerServerApplication started in " + (System.currentTimeMillis() - time) + " ms");
    }

}
