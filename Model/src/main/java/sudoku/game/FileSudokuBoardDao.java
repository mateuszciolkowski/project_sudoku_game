/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sudoku.game.exception.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FileSudokuBoardDao implements Dao<SudokuBoard> {
    private static final String FILE_EXTENSION = ".ser";
    private String saveDirectory;
    private static final Logger logger = LoggerFactory.getLogger(FileSudokuBoardDao.class.getName());
    private static final ResourceBundle messages = ResourceBundle.getBundle(
            "bundle.exception", java.util.Locale.getDefault());

    public FileSudokuBoardDao(String saveDirectory) {
        this.saveDirectory = saveDirectory;

        File directory = new File(saveDirectory);
        if (!directory.exists() && !directory.mkdirs()) {
            String errorMessage = messages.getString("error.problemCreatingDirectory");
            logger.error(errorMessage, saveDirectory);
            throw new DaoException(DaoException.INVALID_DIRECTORY + " " + saveDirectory);
        }
    }

    @Override
    public SudokuBoard read(String name) throws SudokuException {
        String fileName = saveDirectory + File.separator + name + FILE_EXTENSION;
        try (ObjectInputStream inputData = new ObjectInputStream(new FileInputStream(fileName))) {
            return (SudokuBoard) inputData.readObject();
        } catch (ClassNotFoundException | IOException e) {
            String errorMessage = messages.getString("error.readProblem");
            logger.error(e.getLocalizedMessage(), errorMessage);
            throw new SudokuFileReadException(SudokuFileReadException.FILE_READ_ERROR + " " + fileName);
        }

    }

    @Override
    public void write(SudokuBoard obj, String fileName) throws SudokuException {
        if (obj == null || fileName == null || fileName.isEmpty()) {
            String errorMessage = messages.getString("error.nullPointer");
            logger.error(errorMessage);
            throw new SudokuNullPointerException(SudokuNullPointerException.NULL_POINTER);
        }

        String filePath = saveDirectory + "/" + fileName + FILE_EXTENSION;
        try (ObjectOutputStream outputData = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputData.writeObject(obj);
        } catch (IOException e) {
            String errorMessage = messages.getString("error.writeProblem");
            logger.error(e.getLocalizedMessage(), errorMessage);
            throw new SudokuFileWriteException(SudokuFileWriteException.FILE_WRITE_ERROR + " " + filePath);
        }
    }


    @Override
    public List<String> names() throws SudokuException {
        File savedSudokuBoards = new File(saveDirectory);
        if (!savedSudokuBoards.exists() || !savedSudokuBoards.isDirectory()) {
            String errorMessage = messages.getString("error.invalidDirectory");
            logger.error(errorMessage);
            throw new DaoException(DaoException.INVALID_DIRECTORY);
        }

        return Arrays.stream(savedSudokuBoards.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION)))
                .map(file -> file.getName().replace(FILE_EXTENSION, ""))
                .collect(Collectors.toList());
    }


    @Override
    public void close() throws Exception {
        String close = messages.getString("close.file");
        logger.debug(close);
    }
}
