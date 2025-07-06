package at.fhtw.tourplanner.view.controller;

import at.fhtw.tourplanner.model.Tour;
import at.fhtw.tourplanner.service.TourService;
import at.fhtw.tourplanner.viewmodel.LogFormViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddLogController {
    private static final Logger logger = LogManager.getLogger(AddLogController.class);

    @FXML private Label errorLabel;
    @FXML private Button createButton;

    // fx:id stammt vom <fx:include fx:id="logForm"/>
    @FXML private LogFormController logFormController;

    private LogFormViewModel formViewModel;
    private Stage dialogStage;

    public void setData(TourService service, Tour tour, Stage stage) {
        this.dialogStage = stage;

        // Neues LogFormViewModel => Add-Modus (existingLog=null)
        formViewModel = new LogFormViewModel(service, tour);

        // Binde das LogFormViewModel an den eingebetteten Controller
        logFormController.setViewModel(formViewModel);

        // Optional: Button disable & Errorlabel
        createButton.disableProperty().bind(formViewModel.canCreateProperty().not());
        errorLabel.textProperty().bind(formViewModel.errorMessageProperty());

        logger.debug("AddLogController ready (tour={})", tour.getName());
    }

    @FXML
    private void onCreate() {
        logger.debug("Create‑Log button pressed");
        formViewModel.applyChanges();
        // Falls canSave immer noch true => close
        if (formViewModel.canCreateProperty().get()) {
            dialogStage.close();
        }
    }
}
