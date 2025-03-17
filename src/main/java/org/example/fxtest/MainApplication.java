package org.example.fxtest;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import org.example.fxtest.controller.EditDialogController;
import org.example.fxtest.controller.NoteController;
import org.example.fxtest.controller.RootFileController;
import org.example.fxtest.model.Note;
import org.example.fxtest.model.NoteListWrapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.*;
import java.time.LocalTime;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.fxtest.util.DateUtil;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class MainApplication extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private final ObservableList<Note> noteData = FXCollections.observableArrayList();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private ConcurrentHashMap<LocalDateTime, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    public MainApplication() {
        this.noteData.add(new Note("null", "null", DateUtil.format(LocalDateTime.now()), "2021-05-25 18:00"));
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        this.primaryStage.setTitle("NoteApp");

        this.primaryStage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("/3669317_note_add_ic_icon.png"))));
        initRootLayout();

        showNoteOverview();
    }

    private void showNotification() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Напоминание");
        alert.setHeaderText(null);
        alert.setContentText("Пора сделать что-то важное!");
        alert.showAndWait();
    }

    @Override
    public void stop() {
        scheduler.shutdown();
    }

    public void initRootLayout() {
        try {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class
                    .getResource("root-file.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootFileController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Пытается загрузить последний открытый файл с адресатами.
        File file = getNoteFilePath();
        if (file != null) {
            loadNoteDataFromFile(file);
        }
    }

    /**
     * Показывает в корневом макете сведения об адресатах.
     */
    public void showNoteOverview() {
        try {
            // Загружаем сведения об адресатах.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("main-menu.fxml"));
            VBox overview = loader.load();

            // Помещаем сведения об адресатах в центр корневого макета.
            rootLayout.setCenter(overview);

            NoteController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ObservableList<Note> getNoteData() {
        return noteData;
    }

    public boolean showNoteEditDialog(Note note) {
        try {
            // Загружаем fxml-файл и создаём новую сцену
            // для всплывающего диалогового окна.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApplication.class.getResource("edit-dialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Note");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Передаём адресата в контроллер.
            EditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setNote(note);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getNoteFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApplication.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    public void setNoteFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApplication.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Обновление заглавия сцены.
            primaryStage.setTitle("NoteApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Обновление заглавия сцены.
            primaryStage.setTitle("NoteApp");
        }
    }

    public void scheduleNotification(LocalDateTime targetTime) {
        long delay = java.time.Duration.between(LocalDateTime.now(), targetTime).toMillis();
        tasks.put(targetTime, scheduler.schedule(() -> Platform.runLater(this::showNotification), delay, TimeUnit.MILLISECONDS));
    }

    public void changeSchedule(Note before, Note edited) {
        //Если меняется время истечения
        if(before.getExpiryDate().isAfter(LocalDateTime.now())) {
            ScheduledFuture<?> future = tasks.remove(before.getExpiryDate());
            if(future != null) {
                future.cancel(false);
            }
        }
        if(edited.getExpiryDate().isAfter(LocalDateTime.now())) {
            scheduleNotification(edited.getExpiryDate());
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Данное время уже истекло");
            alert.showAndWait();
        }
    }

    public void deleteTask(Note deleted) {
        if(deleted.getExpiryDate().isAfter(LocalDateTime.now())) {
            ScheduledFuture<?> future = tasks.remove(deleted.getExpiryDate());
            if(future != null) {
                future.cancel(false);
            }
        }
    }

    public void loadNoteDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(NoteListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Чтение XML из файла и демаршализация.
            NoteListWrapper wrapper = (NoteListWrapper) um.unmarshal(file);
            for(Note n : wrapper.getNotes()) {
                scheduleNotification(n.getExpiryDate());
            }
            noteData.clear();
            noteData.addAll(wrapper.getNotes());

            // Сохраняем путь к файлу в реестре.
            setNoteFilePath(file);

        } catch (Exception e) { // catches ANY exception
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Сохраняет текущую информацию об адресатах в указанном файле.
     *
     */
    public void saveNoteDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(NoteListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            NoteListWrapper wrapper = new NoteListWrapper();
            wrapper.setNotes(noteData);
            // Обёртываем наши данные об адресатах.
            // Маршаллируем и сохраняем XML в файл.
            m.marshal(wrapper, file);
            // Сохраняем путь к файлу в реестре.
            setNoteFilePath(file);
        } catch (Exception e) { // catches ANY exception
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
}