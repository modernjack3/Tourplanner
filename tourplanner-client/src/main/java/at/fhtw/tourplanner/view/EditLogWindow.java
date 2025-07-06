package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.model.TourLog;
import at.fhtw.tourplanner.service.TourService;
import at.fhtw.tourplanner.view.controller.EditLogController;
import at.fhtw.tourplanner.viewmodel.LogFormViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class EditLogWindow extends Stage {

    public EditLogWindow(TourService service, Tour tour, TourLog log) {
        try {
            setTitle("Edit Tour Log");
            initModality(Modality.APPLICATION_MODAL);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/at/fhtw/tourplanner/view/EditLogView.fxml")
            );
            VBox root = loader.load();

            EditLogController controller = loader.getController();

            // Erzeuge das Form-ViewModel
            LogFormViewModel vm = new LogFormViewModel(service, tour);
            vm.setExistingLog(log); // => Modus "bearbeiten"

            controller.setData(vm, this);

            setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
