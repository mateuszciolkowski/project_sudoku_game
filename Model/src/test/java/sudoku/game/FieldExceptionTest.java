package sudoku.game;

import org.junit.jupiter.api.Test;
import sudoku.game.exception.FieldException;
import sudoku.game.exception.SudokuNullPointerException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FieldExceptionTest {

    @Test
    void throwExceptionTest() throws FieldException {
        List<SudokuField> validFields = new ArrayList<>(9);
        List<SudokuField> invalidFields = new ArrayList<>(8);

        for (int i = 0; i < 9; i++) {
            validFields.add(new SudokuField());
        }

        for (int i = 0; i < 8; i++) {
            invalidFields.add(new SudokuField());
        }

        assertDoesNotThrow(() -> new SudokuFieldsGroup(validFields), "Constructor should not throw an exception for valid fields");

        assertThrows(FieldException.class, () -> new SudokuFieldsGroup(invalidFields), "Constructor should throw FieldException for a list with less than 9 fields");
    }

    @Test
    void throwExceptionValidSetTest() throws FieldException {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        board.solveGame();

        assertDoesNotThrow(() -> board.set(0, 0, 1), "Method set should not throw an exception for valid values");

        assertThrows(FieldException.class, () -> board.set(0, 0, 10), "Method set should throw FieldException for values outside the valid range (1-9).");
        assertThrows(FieldException.class, () -> board.set(0, 0, -1), "Method set should throw FieldException for values outside the valid range (1-9).");


    }

    @Test
    void throwExceptionNullSolverTest() throws SudokuNullPointerException {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board;

        assertDoesNotThrow(() -> new SudokuBoard(solver), "Constructor should not throw a null pointer exception ");

        assertThrows(SudokuNullPointerException.class, () -> new SudokuBoard(null), "Constructor should throw NullPointerException");
    }
}
