package org.example.fxtest.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import org.example.fxtest.MainApplication;

import java.io.File;
import java.io.IOException;

public class RootFileController {
    private MainApplication mainApp;

    public void setMainApp(MainApplication mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleNew() {
        mainApp.getNoteData().clear();
        this.mainApp.stop();
        mainApp.setNoteFilePath(null);
    }

    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Показываем диалог загрузки файла
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadNoteDataFromFile(file);
        }
    }

    @FXML
    private void handleSave() {
        File personFile = mainApp.getNoteFilePath();
        if (personFile != null) {
            mainApp.saveNoteDataToFile(personFile);
        } else {
            handleSaveAs();
        }
    }

    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // File type filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Ensure the file has .xml extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }

            try {
                // Force file creation if it doesn't exist
                if (!file.exists()) {
                    boolean created = file.createNewFile();
                    System.out.println("File created: " + created);
                }

                // Check write permissions
                if (!file.canWrite()) {
                    throw new IOException("File is not writable.");
                }

                System.out.println("Selected file: " + file.getAbsolutePath());
                System.out.println("File exists: " + file.exists());
                System.out.println("File can write: " + file.canWrite());

                mainApp.saveNoteDataToFile(file);
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Error");
                alert.setHeaderText("Cannot Save File");
                alert.setContentText("File is not writable: " + file.getAbsolutePath());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("AddressApp");
        alert.setHeaderText("About");
        alert.setContentText("Author: Kirill");

        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}