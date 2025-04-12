/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sudoku.game.exception.SudokuException;
import sudoku.game.exception.SudokuNullPointerException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class SudokuBoard implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    public static final int BOARD_SIZE = 9;
    private List<List<SudokuField>> board;
    private SudokuSolver solver;
    private static final Logger logger = LoggerFactory.getLogger(SudokuBoard.class);
    private static final ResourceBundle messages = ResourceBundle.getBundle(
            "bundle.exception", java.util.Locale.getDefault());

    public SudokuBoard(SudokuSolver solver) {
        if (solver == null) {
            String errorMassage = messages.getString("error.nullPointer");
            logger.error(errorMassage);
            throw new SudokuNullPointerException(SudokuNullPointerException.NULL_POINTER);
        }
        this.solver = solver;

        List<List<SudokuField>> tempBoard = new ArrayList<>(BOARD_SIZE);
        for (int i = 0; i < BOARD_SIZE; i++) {
            List<SudokuField> row = new ArrayList<>(BOARD_SIZE);
            for (int j = 0; j < BOARD_SIZE; j++) {
                row.add(new SudokuField());  
            }
            tempBoard.add(Collections.unmodifiableList(row));  
        }
        this.board = Collections.unmodifiableList(tempBoard);
    }


    public int get(int x, int y) {
        return board.get(x).get(y).getFieldValue();
    }

    public void set(int x, int y, int value) {
        board.get(x).get(y).setFieldValue(value);
    }

    public void setToDefault(int x, int y) {
        board.get(x).get(y).setToDefault();
    }

    public boolean checkBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {

            if (!getColumn(i).verify()) {
                return false;
            }
            if (!getRow(i).verify()) {
                return false;
            }
            if (i % 3 == 0) {
                for (int j = 0; j < BOARD_SIZE; j = j + 3) {
                    if (!getBox(i, j).verify()) {
                        return false;
                    }
                }
            }

        }
        return true;
    }

    public void solveGame() {
        solver.solve(this);
    }

    public SudokuRow getRow(int y) {
        List<SudokuField> fieldsToCheck = new ArrayList<>(BOARD_SIZE);
        for (int x = 0; x < BOARD_SIZE; x++) {
            fieldsToCheck.add(board.get(x).get(y));
        }
        return new SudokuRow(fieldsToCheck);
    }

    public SudokuColumn getColumn(int x) {
        List<SudokuField> fieldsToCheck = new ArrayList<>(BOARD_SIZE);

        for (int y = 0; y < BOARD_SIZE; y++) {
            fieldsToCheck.add(board.get(x).get(y));
        }
        return new SudokuColumn(fieldsToCheck);
    }

    public SudokuBox getBox(int x, int y) {
        List<SudokuField> fieldsToCheck = new ArrayList<>(BOARD_SIZE);
        for (int i = 0; i < SudokuBox.SIZE_BOX; i++) {
            for (int j = 0; j < SudokuBox.SIZE_BOX; j++) {
                fieldsToCheck.add(board.get(x / 3 * 3 + i).get(y / 3 * 3 + j));
            }
        }
        return new SudokuBox(fieldsToCheck);
    }


    public void applyLevel(LevelsSudoku difficulty) {
        difficulty.applyDifficulty(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SudokuBoard board1 = (SudokuBoard) o;

        return new EqualsBuilder()
                .append(board, board1.board)
                .isEquals();
    }


    public SudokuField getSudokuField(int row, int col) {
        return board.get(row).get(col);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(board)
                .append(solver)
                .toHashCode();
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();

        for (List<SudokuField> row : board) {
            for (SudokuField field : row) {
                boardString.append(field.getFieldValue()).append(" ");
            }
            boardString.append(System.lineSeparator()); 
        }
        boardString.append(System.lineSeparator()); 
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("board", System.lineSeparator() + boardString.toString().trim())
                .toString();
    }

    @Override
    public SudokuBoard clone() {
        try {
            SudokuBoard clone = (SudokuBoard) super.clone();

            List<List<SudokuField>> clonedBoard = new ArrayList<>(BOARD_SIZE);
            for (int i = 0; i < BOARD_SIZE; i++) {
                List<SudokuField> clonedRow = new ArrayList<>(BOARD_SIZE);
                for (int j = 0; j < BOARD_SIZE; j++) {
                    clonedRow.add(this.board.get(i).get(j).clone());
                }
                clonedBoard.add(clonedRow);


                clone.board = clonedBoard;

            }
            return clone;
        } catch (CloneNotSupportedException e) {
            String errorMassage = messages.getString("error.cloningProblem");
            logger.error(errorMassage, e.getMessage());
            throw new SudokuException(SudokuException.CLONE_PROBLEM, e);
        }


    }
}
