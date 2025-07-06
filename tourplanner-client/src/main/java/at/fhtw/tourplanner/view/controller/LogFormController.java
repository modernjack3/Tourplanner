package at.fhtw.tourplanner.view.controller;

import at.fhtw.tourplanner.viewmodel.LogFormViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;

public class LogFormController {

    @FXML public DatePicker datePicker;
    @FXML public TextField timeField;
    @FXML public TextField commentField;
    @FXML public ComboBox<String> difficultyBox;
    @FXML public TextField distanceField;
    @FXML public TextField totalTimeField;
    @FXML public TextField ratingField;

    private LogFormViewModel viewModel;

    public void setViewModel(LogFormViewModel vm) {
        this.viewModel = vm;
        initializeFields();
        bindFields();
    }

    private void initializeFields() {
        // Populate the difficulty combo
        difficultyBox.getItems().addAll("Easy", "Medium", "Hard");
        // Du könntest hier einen Default setzen,
        // aber wenn wir im Edit-Modus sind, überschreibt das Bindung eventuell.
    }

    private void bindFields() {
        datePicker.valueProperty().bindBidirectional(viewModel.dateProperty());
        timeField.textProperty().bindBidirectional(viewModel.timeProperty());
        commentField.textProperty().bindBidirectional(viewModel.commentProperty());
        difficultyBox.valueProperty().bindBidirectional(viewModel.difficultyProperty());

        distanceField.textProperty().bindBidirectional(viewModel.distanceProperty(), new NumberStringConverter());
        totalTimeField.textProperty().bindBidirectional(viewModel.totalTimeProperty(), new NumberStringConverter());
        ratingField.textProperty().bindBidirectional(viewModel.ratingProperty(), new NumberStringConverter());
    }
}
