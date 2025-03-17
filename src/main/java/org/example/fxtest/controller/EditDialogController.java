package org.example.fxtest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.fxtest.model.Note;
import org.example.fxtest.util.DateUtil;

public class EditDialogController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField textField;
    @FXML
    private TextField expiryField;

    private Stage dialogStage;
    private Note note;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setNote(Note note) {
        this.note = note;

        nameField.setText(note.getName());
        textField.setText(note.getText());
        expiryField.setText(DateUtil.format(note.getExpiryDate()));
    }

    /**
     * Returns true, если пользователь кликнул OK, в другом случае false.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Вызывается, когда пользователь кликнул по кнопке OK.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            note.setName(nameField.getText());
            note.setText(textField.getText());
            note.setExpiryDate(DateUtil.parse(expiryField.getText()));

            this.okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Вызывается, когда пользователь кликнул по кнопке Cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Проверяет пользовательский ввод в текстовых полях.
     *
     * @return true, если пользовательский ввод корректен
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "No valid name!\n";
        }
        if (textField.getText() == null || textField.getText().isEmpty()) {
            errorMessage += "No valid text!\n";
        }
        if (expiryField.getText() == null || expiryField.getText().isEmpty()) {
            errorMessage += "No valid time!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // Показываем сообщение об ошибке.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}