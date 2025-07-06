package at.fhtw.tourplanner.view.controller;

import at.fhtw.tourplanner.viewmodel.TourDetailsViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TourDetailsController {

    @FXML private TextField nameField;
    @FXML private TextArea descField;

    private TourDetailsViewModel viewModel;

    public void setViewModel(TourDetailsViewModel vm) {
        this.viewModel = vm;
        bindFields();
    }

    private void bindFields() {
        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        descField.textProperty().bindBidirectional(viewModel.descriptionProperty());
    }

    @FXML
    private void onSave() {
        viewModel.saveChanges();
        nameField.getScene().getWindow().hide();
    }
}
