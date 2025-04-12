package sudoku.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuSolverTest {

    @Test
    void correctFieldCheck() {
            SudokuSolver solver = new BacktrackingSudokuSolver();
            SudokuBoard sudokuBoard = new SudokuBoard(solver);
            sudokuBoard.solveGame();
        assertTrue(sudokuBoard.checkBoard());

    }

}
