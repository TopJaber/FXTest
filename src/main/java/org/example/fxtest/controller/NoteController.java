package org.example.fxtest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.fxtest.MainApplication;
import org.example.fxtest.model.Note;
import org.example.fxtest.util.DateUtil;

public class NoteController {
    @FXML
    private TableView<Note> noteTable;
    @FXML
    private TableColumn<Note, String> nameColumn;
    @FXML
    private TableColumn<Note, String> expiryColumn;

    @FXML
    private Label nameLabel;
    @FXML
    private Label textLabel;
    @FXML
    private Label createDateLabel;
    @FXML
    private Label expiryDateLabel;

    // Ссылка на главное приложение.
    private MainApplication mainApp;

    public NoteController() {
    }

    /**
     * Инициализация класса-контроллера. Этот метод вызывается автоматически
     * после того, как fxml-файл будет загружен.
     */
    @FXML
    private void initialize() {
        // Инициализация таблицы адресатов с двумя столбцами.
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        expiryColumn.setCellValueFactory(cellData -> cellData.getValue().expiryProperty());
        showNoteDetails(null);
        noteTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showNoteDetails(newValue));
    }

    public void setMainApp(MainApplication mainApp) {
        this.mainApp = mainApp;

        this.noteTable.setItems(mainApp.getNoteData());
    }

    private void showNoteDetails(Note note) {
        if (note != null) {
            // Заполняем метки информацией из объекта person.
            nameLabel.setText(note.getName());
            textLabel.setText(note.getText());
            createDateLabel.setText(DateUtil.format(note.getCreateDate()));
            expiryDateLabel.setText(DateUtil.format(note.getExpiryDate()));
        } else {
            nameLabel.setText("");
            textLabel.setText("");
            createDateLabel.setText("");
            expiryDateLabel.setText("");
        }
    }

    @FXML
    private void handleDeleteNote() {
        int selectedIndex = noteTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Note deleted = noteTable.getItems().remove(selectedIndex);
            this.mainApp.deleteTask(deleted);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Note Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleNewNote() {
        Note tempNote = new Note("null2", "null2", "2022-05-25 18:00");
        boolean okClicked = mainApp.showNoteEditDialog(tempNote);
        if (okClicked) {
            mainApp.getNoteData().add(tempNote);
        }
        this.mainApp.scheduleNotification(tempNote.getExpiryDate());
    }

    @FXML
    private void handleEditNote() {
        Note selectedNote = noteTable.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            Note old = selectedNote;
            String oldDate = DateUtil.format(selectedNote.getExpiryDate());

            boolean okClicked = mainApp.showNoteEditDialog(selectedNote);
            if (okClicked) {
                showNoteDetails(selectedNote);
            }
            String newDate = DateUtil.format(selectedNote.getExpiryDate());
            if(!oldDate.equals(newDate)) {
                this.mainApp.changeSchedule(old, selectedNote);
            }
        } else {
            // Ничего не выбрано.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Note Selected");
            alert.setContentText("Please select a note in the table.");

            alert.showAndWait();
        }
    }
}