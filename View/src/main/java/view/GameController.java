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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sudoku.game.*;
import sudoku.game.exception.DaoException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private ResourceBundle bundle;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button resetButton;

    @FXML
    private Button backButton;

    @FXML
    private Button saveToFileButton;

    @FXML
    private Button saveToDatabaseButton;

    private SudokuBoard originalBoard;
    private SudokuBoard editableBoard;

    public void initialize(LevelsSudoku difficulty) {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        originalBoard = new SudokuBoard(solver);
        originalBoard.solveGame();
        originalBoard.applyLevel(difficulty);

        editableBoard = originalBoard.clone();

        displayBoard();
    }

    @FXML
    public void saveBoardToFile() {
        if (editableBoard != null) {
            boolean isBoardValid = true;

            for (int i = 0; i < 9; i++) {
                if (!editableBoard.getBox(i, i).isValidBoard()
                        || !editableBoard.getColumn(i).isValidBoard()
                        || !editableBoard.getRow(i).isValidBoard()) {
                    isBoardValid = false;
                    break;
                }
            }

            if (isBoardValid) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle(bundle.getString("saveToFile.title"));
                dialog.setHeaderText(bundle.getString("saveToFile.header"));
                dialog.setContentText(bundle.getString("saveToFile.prompt"));

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(fileName -> {
                    try (FileSudokuBoardDao fileDao = new FileSudokuBoardDao("saves")) {
                        fileDao.write(editableBoard, fileName);
                        showAlertSaveBoard(bundle.getString("saveSuccess.text"), Alert.AlertType.INFORMATION);
                    } catch (Exception e) {
                        logger.warn("Save to file failed: {}", e.getMessage());
                        showAlertSaveBoard(bundle.getString("saveUnsuccess.text"), Alert.AlertType.ERROR);
                    }
                });
            } else {
                showAlertSaveBoard(bundle.getString("saveUnsuccess.text"), Alert.AlertType.ERROR);
                logger.warn("Save unsuccess");
            }
        } else {
            showAlertSaveBoard(bundle.getString("saveNPE.text"), Alert.AlertType.ERROR);
            logger.error("Save error");
        }
    }


    @FXML
    public void saveBoardToDatabase() {
        if (editableBoard != null) {
            boolean isBoardValid = true;

            for (int i = 0; i < 9; i++) {
                if (!editableBoard.getBox(i, i).isValidBoard()
                        || !editableBoard.getColumn(i).isValidBoard()
                        || !editableBoard.getRow(i).isValidBoard()) {
                    isBoardValid = false;
                    break;
                }
            }

            if (isBoardValid) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle(bundle.getString("saveToDatabase.title"));
                dialog.setHeaderText(bundle.getString("saveToDatabase.header"));
                dialog.setContentText(bundle.getString("saveToDatabase.prompt"));

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(boardName -> {
                    try (JdbcSudokuBoardDao dao = new JdbcSudokuBoardDao()) {
                        dao.write(editableBoard, boardName);
                        showAlertSaveBoard(bundle.getString("saveSuccessDB.text"), Alert.AlertType.INFORMATION);
                    } catch (SQLException | DaoException e) {
                        logger.error("Save to database failed: {}", e.getMessage());
                        showAlertSaveBoard(bundle.getString("saveUnsuccessDB.text"), Alert.AlertType.ERROR);
                    }
                });
            } else {
                showAlertSaveBoard(bundle.getString("saveUnsuccess.text"), Alert.AlertType.ERROR);
                logger.warn("Save unsuccess");
            }
        } else {
            showAlertSaveBoard(bundle.getString("saveNPE.text"), Alert.AlertType.ERROR);
            logger.error("Save error");
        }
    }

    private void showAlertSaveBoard(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayBoard() {
        gridPane.getChildren().clear();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                TextField textField = new TextField();
                textField.setPrefSize(50, 45);
                textField.setStyle("-fx-font-size: 20px; -fx-alignment: center;");

                int value = editableBoard.get(i, j);

                if (value != 0) {
                    textField.setText(String.valueOf(value));
                    textField.setDisable(true);
                } else {
                    TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
                        String newText = change.getControlNewText();
                        if (newText.matches("[1-9]?")) {
                            return change;
                        }
                        return null;
                    });
                    textField.setTextFormatter(textFormatter);

                    int finalI = i;
                    int finalJ = j;

                    textField.textProperty().addListener((obs, oldValue, newValue) -> {
                        if (newValue != null && !newValue.isEmpty()) {
                            editableBoard.set(finalI, finalJ, Integer.parseInt(newValue));
                        } else {
                            editableBoard.set(finalI, finalJ, 0);
                        }
                    });
                }

                gridPane.add(textField, j, i);
            }
        }
    }


    @FXML
    public void resetBoard() {
        editableBoard = originalBoard.clone();
        displayBoard();
    }

    @FXML
    public void goBackToDifficulty() {
        try {
            Locale currentLocale = Locale.getDefault();
            ResourceBundle bundle = ResourceBundle.getBundle("Bundle.messages", currentLocale);

            Stage currentStage = (Stage) backButton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StartForm.fxml"));
            loader.setResources(bundle);
            Scene difficultyScene = new Scene(loader.load());

            currentStage.setScene(difficultyScene);
        } catch (IOException e) {
            logger.error("Returning to StartForm error {}", e.getMessage());
        }
    }

    @FXML
    public void checkSudoku() {
        if (editableBoard.checkBoard()) {
            showAlert(bundle.getString("correctSolution.text"));
            logger.debug("Correct solution");
        } else {
            showAlert(bundle.getString("incorrectSolution.text"));
            logger.debug("Incorrect solution");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void initializeWithBoard(SudokuBoard loadedBoard) {
        this.originalBoard = loadedBoard;
        this.editableBoard = loadedBoard.clone();
        displayBoard();
    }
}
