package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.service.TourService;
import at.fhtw.tourplanner.view.controller.TourDetailsController;
import at.fhtw.tourplanner.viewmodel.TourDetailsViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TourDetailsWindow extends Stage {

    public TourDetailsWindow(TourService service, Tour tour) {
        try {
            setTitle("Tour Details: " + tour.getName());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/at/fhtw/tourplanner/view/TourDetailsView.fxml"));
            VBox root = loader.load();

            TourDetailsController controller = loader.getController();
            TourDetailsViewModel vm = new TourDetailsViewModel(service);
            vm.setTour(tour);
            controller.setViewModel(vm);

            setScene(new Scene(root, 400, 400));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
