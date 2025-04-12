/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sudoku.game.FileSudokuBoardDao;
import sudoku.game.JdbcSudokuBoardDao;
import sudoku.game.LevelsSudoku;
import sudoku.game.SudokuBoard;
import sudoku.game.exception.DaoException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class StartFormController {
    private static final Logger logger = LoggerFactory.getLogger(StartFormController.class);
    @FXML
    private Label difficultLabel;
    @FXML
    private RadioButton easyOption;
    @FXML
    private RadioButton mediumOption;
    @FXML
    private RadioButton hardOption;
    @FXML
    private Button startButton;
    @FXML
    private Button loadGameButton;
    @FXML
    private Button loadFromDatabaseButton;
    @FXML
    private Button authorsButton;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private ToggleGroup difficultyGroup;

    @FXML
    public void initialize() {
        Locale systemLocale = Locale.getDefault();
        String languageCode = systemLocale.getLanguage();

        languageComboBox.getItems().clear();
        languageComboBox.getItems().addAll("Polski", "English", "Español", "Deutsch");
        switch (languageCode) {
            case "pl" -> languageComboBox.setValue("Polski");
            case "en" -> languageComboBox.setValue("English");
            case "es" -> languageComboBox.setValue("Español");
            case "de" -> languageComboBox.setValue("Deutsch");
            default -> languageComboBox.setValue("Polski");
        }

        languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            changeLanguage();
        });

        difficultyGroup = new ToggleGroup();
        easyOption.setToggleGroup(difficultyGroup);
        mediumOption.setToggleGroup(difficultyGroup);
        hardOption.setToggleGroup(difficultyGroup);

        difficultyGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton selectedRadioButton = (RadioButton) newValue;
                logger.debug("Dificulty level changed to: " + selectedRadioButton.getText());
            }
        });
    }

    @FXML
    public void changeLanguage() {
        String selectedLanguage = languageComboBox.getValue();

        switch (selectedLanguage) {
            case "Polski" -> Locale.setDefault(new Locale("pl", "PL"));
            case "English" -> Locale.setDefault(new Locale("en", "US"));
            case "Español" -> Locale.setDefault(new Locale("es", "ES"));
            case "Deutsch" -> Locale.setDefault(new Locale("de", "DE"));
            default -> Locale.setDefault(new Locale("pl", "PL"));
        }

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Bundle.messages", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StartForm.fxml"));
            loader.setResources(bundle);

            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) languageComboBox.getScene().getWindow();

            if (stage != null) {
                stage.setScene(scene);
                stage.show();
            } else {
                logger.error("Stage is null. Cannot reload the scene.");
            }
        } catch (IOException e) {
            logger.error("Error reloading the view: {}", e.getMessage());
        }
    }

    @FXML
    public void showAuthors() {
        ResourceBundle bundleAuthors = ResourceBundle.getBundle("view.AuthorsResourceBundle");
        ResourceBundle bundle = ResourceBundle.getBundle("Bundle.messages", Locale.getDefault());

        String author1 = bundleAuthors.getString("author1");
        String author2 = bundleAuthors.getString("author2");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(bundle.getString("authorsLabel.text") + ":\n" + author1 + "\n" + author2);
        alert.showAndWait();
        logger.debug("Authors were shown");
    }

    @FXML
    public void loadGameFromDatabase() {
        try (JdbcSudokuBoardDao dao = new JdbcSudokuBoardDao()) {
            List<String> boardNames = dao.names();
            ChoiceDialog<String> dialog = new ChoiceDialog<>(boardNames.isEmpty() ? "" : boardNames.get(0), boardNames);
            ResourceBundle bundle = ResourceBundle.getBundle("Bundle.messages", Locale.getDefault());
            dialog.setTitle(bundle.getString("loadFromDatabase.title"));
            dialog.setHeaderText(bundle.getString("loadFromDatabase.header"));
            dialog.setContentText(bundle.getString("loadFromDatabase.prompt"));


            Optional<String> result = dialog.showAndWait();
            result.ifPresent(boardName -> {
                try {
                    SudokuBoard loadedBoard = dao.read(boardName);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GameForm.fxml"));
                    loader.setResources(bundle);
                    Scene scene = new Scene(loader.load());

                    GameController controller = loader.getController();
                    controller.initializeWithBoard(loadedBoard);
                    controller.setBundle(bundle);

                    Stage stage = (Stage) loadFromDatabaseButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (DaoException | IOException e) {
                    logger.error("Error loading board: {}", e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to load the selected board from the database.");
                    alert.showAndWait();
                }
            });
        } catch (SQLException e) {
            logger.error("Error fetching board names: {}", e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed to retrieve board names from the database.");
            alert.showAndWait();
        }
    }

    @FXML
    public void loadGame() {
        File savesDirectory = new File("saves");
        if (!savesDirectory.exists() || !savesDirectory.isDirectory()) {
            savesDirectory.mkdirs();
        }

        File[] files = savesDirectory.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("No saved games found.");
            alert.showAndWait();
            return;
        }

        List<String> fileNames = List.of(files).stream()
                .map(File::getName)
                .map(name -> name.substring(0, name.lastIndexOf('.')))
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(fileNames.get(0), fileNames);
        ResourceBundle bundle = ResourceBundle.getBundle("Bundle.messages", Locale.getDefault());
        dialog.setTitle(bundle.getString("loadFromFile.title"));
        dialog.setHeaderText(bundle.getString("loadFromFile.header"));
        dialog.setContentText(bundle.getString("loadFromFile.prompt"));

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fileName -> {
            try {
                FileSudokuBoardDao dao = new FileSudokuBoardDao(savesDirectory.getPath());
                SudokuBoard loadedBoard = dao.read(fileName);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GameForm.fxml"));
                loader.setResources(bundle);
                Scene scene = new Scene(loader.load());

                GameController controller = loader.getController();
                controller.initializeWithBoard(loadedBoard);
                controller.setBundle(bundle);

                Stage stage = (Stage) loadGameButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                logger.error("Error loading game from file: {}", e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Failed to load the selected game file.");
                alert.showAndWait();
            }
        });
    }


    @FXML
    public void startGame() {
        logger.debug("Start Game");
        LevelsSudoku selectedLevel = LevelsSudoku.EASY;
        if (mediumOption.isSelected()) {
            selectedLevel = LevelsSudoku.MEDIUM;
        } else if (hardOption.isSelected()) {
            selectedLevel = LevelsSudoku.HARD;
        }

        try {
            Locale currentLocale = Locale.getDefault();
            ResourceBundle bundle = ResourceBundle.getBundle("Bundle.messages", currentLocale);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GameForm.fxml"));
            loader.setResources(bundle);
            Scene scene = new Scene(loader.load());

            GameController controller = loader.getController();
            controller.initialize(selectedLevel);
            controller.setBundle(bundle);

            Stage stage = (Stage) easyOption.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            logger.error("Starting game error {}", e.getMessage());
        }
    }
}
