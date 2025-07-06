package at.fhtw.tourplanner.view;

import at.fhtw.tourplanner.service.MapService;
import at.fhtw.tourplanner.service.TourService;
import at.fhtw.tourplanner.view.controller.AddTourController;
import at.fhtw.tourplanner.viewmodel.AddTourViewModel;
import at.fhtw.tourplanner.viewmodel.MainViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

//Die eigene Stage, welche das AddTourView.fxml lädt
//Erbt von Stage, so ist dieses Fenster eigenständig (kann Title-Bar, Größe usw. anpassen)
public class AddTourWindow extends Stage {

    // service - TourService handles ceration in DB
    // mainVM - to refresh the main list in MainViewModel after creation
    public AddTourWindow(TourService service, MainViewModel mainVM, MapService mapService) {
        try {
            setTitle("Add New Tour");
            // Make this dialog modal (blocks the main window)
            initModality(Modality.APPLICATION_MODAL);

            // Load FXML from resources
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/at/fhtw/tourplanner/view/AddTourView.fxml")
            );
            VBox root = loader.load();

            // Retrieve the controller, which is prior constructed thanks FXML
            AddTourController controller = loader.getController();

            // Create the ViewModel for adding tours, and managing input fields
            AddTourViewModel vm = new AddTourViewModel(service);

            // Pass it to the controller along with optional mainVM (for refreshing the List) and this stage
            controller.setViewModel(vm, mainVM, this);

            // Scene setup
            setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
