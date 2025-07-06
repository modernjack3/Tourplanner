package at.fhtw.tourplanner.view.controller;

import at.fhtw.tourplanner.viewmodel.LogFormViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EditLogController {

    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private LogFormController logFormController;

    private LogFormViewModel formViewModel;
    private Stage dialogStage;

    public void setData(LogFormViewModel vm, Stage stage) {
        this.formViewModel = vm;
        this.dialogStage = stage;

        logFormController.setViewModel(formViewModel);

        // Binding
        saveButton.disableProperty().bind(formViewModel.canCreateProperty().not());
        errorLabel.textProperty().bind(formViewModel.errorMessageProperty());
    }

    @FXML
    private void onSave() {
        formViewModel.applyChanges();
        if (formViewModel.canCreateProperty().get()) {
            dialogStage.close();
        }
    }
}
