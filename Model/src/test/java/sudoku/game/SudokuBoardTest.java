package sudoku.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class SudokuBoardTest {
    private SudokuBoard sudokuBoard;
    public SudokuSolver solver;


    @Test
    void logTest() {
        final Logger logger = LoggerFactory.getLogger(SudokuBoardTest.class);
        System.out.println("Classloader path: " + SudokuBoardTest.class.getResource("/logback.xml"));
//        System.out.println("Logback configuration: " + System.getProperty("logback.configurationFile"));
        logger.error("Test log message!");
    }

    @BeforeEach
    void setUp() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        sudokuBoard = new SudokuBoard(solver);
        sudokuBoard.solveGame();
    }

    @Test
    void isBoardNotEmpty() {
        int WIDTH = 9;
        int HEIGHT = 9;

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                assertTrue(sudokuBoard.get(i, j) != 0);
            }
        }
    }

    @Test
    void checkBoardRowTest() {
        sudokuBoard.set(3, 0, sudokuBoard.get(3, 8));
        assertFalse(sudokuBoard.checkBoard());
    }

    @Test
    void checkBoardColumnTest() {
        sudokuBoard.set(0, 8, sudokuBoard.get(0, 2));
        assertFalse(sudokuBoard.checkBoard());
    }

    @Test
    void checkBoardBoxTest() {
        sudokuBoard.set(8, 8, sudokuBoard.get(8, 7));
        assertFalse(sudokuBoard.checkBoard());
    }

    @Test
    void getRowTest() {
        assertNotNull(sudokuBoard.getRow(0));
    }

    @Test
    void getColumnTest() {
        assertNotNull(sudokuBoard.getColumn(0));
    }

    @Test
    void getBoxTest() {
        assertNotNull(sudokuBoard.getBox(0, 0));
    }



    @Test
    void isEqualsTest() {
        assertEquals(sudokuBoard, sudokuBoard);
        assertNotEquals(sudokuBoard, null);
    }

    @Test
    void equalsNullTest() {
        assertFalse(sudokuBoard.equals(null));
    }

    @Test
    void isEqualsByFieldsTest() {
        SudokuBoard sudokuBoard_Test = new SudokuBoard(new BacktrackingSudokuSolver());
        sudokuBoard_Test.solveGame();
        assertNotEquals(sudokuBoard, sudokuBoard_Test);
    }


    @Test
    void testEqualsDifferentType() {
        assertNotEquals(sudokuBoard, new Object());
    }

    @Test
    void equalsAfterCopingAllElementsTest() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard sudokuBoard_test = new SudokuBoard(solver);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sudokuBoard_test.set(i, j, sudokuBoard.get(i, j));
            }
        }

        assertTrue(sudokuBoard.equals(sudokuBoard_test));
    }

    @Test
    void testHashCode() {
        sudokuBoard.solveGame();
        sudokuBoard.hashCode();
    }

    @Test
    void testToString() {
        sudokuBoard.solveGame();
        sudokuBoard.toString();
    }

    @Test
    void cloneTest() {
        SudokuSolver solver = new BacktrackingSudokuSolver();
        SudokuBoard board = new SudokuBoard(solver);
        board.solveGame();
        board.set(0, 0, 5);
        SudokuBoard clonedBoard = board.clone();
        assertNotSame(board, clonedBoard);

        assertEquals(board.get(0,0),clonedBoard.get(0,0));
        clonedBoard.set(0, 0, 8);
        assertNotEquals(board.get(0,0),clonedBoard.get(0,0));
    }

}