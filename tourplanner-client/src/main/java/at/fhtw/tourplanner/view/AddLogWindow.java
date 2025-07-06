package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.service.TourService;
import at.fhtw.tourplanner.view.controller.AddLogController;
import at.fhtw.tourplanner.viewmodel.LogFormViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AddLogWindow extends Stage {

    public AddLogWindow(TourService service, Tour selectedTour) {
        try {
            setTitle("Add New Tour Log");
            // Make this dialog modal (blocks the main window)
            initModality(Modality.APPLICATION_MODAL);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/at/fhtw/tourplanner/view/AddLogView.fxml")
            );
            VBox root = loader.load();

            AddLogController controller = loader.getController();

            //LogFormViewModel vm = new LogFormViewModel(service, selectedTour);
            //controller.setViewModel(vm, this);

            controller.setData(service, selectedTour, this);

            setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
