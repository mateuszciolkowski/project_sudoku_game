package sudoku.game;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import sudoku.game.exception.DaoException;
import sudoku.game.exception.SudokuException;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuBoardDaoFactoryTest {

    private static final String TEST_DIRECTORY = "testDirectoryTest";

    @AfterEach
    void cleanup() {
        try {
            File testDirectory = new File(TEST_DIRECTORY);
            if (testDirectory.exists()) {
                FileUtils.deleteDirectory(testDirectory);
            }
        } catch (IOException e) {
            fail("Failed to clean up test directory: " + e.getMessage());
        }
    }

    @Test
    void getFileDaoTest() {
        try {
            Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(TEST_DIRECTORY);

            assertEquals(0, dao.names().size(), "Directory should initially be empty");

            SudokuSolver solver = new BacktrackingSudokuSolver();
            SudokuBoard board = new SudokuBoard(solver);
            dao.write(board, "testBoard1");

            assertEquals(1, dao.names().size(), "Directory should contain one board after writing");
            assertTrue(dao.names().contains("testBoard1"), "Saved board name should appear in the names list");

            SudokuBoard readBoard = dao.read("testBoard1");
            assertEquals(board, readBoard, "The read board should match the written board");

        } catch (DaoException e) {
            fail("Unexpected exception during test: " + e.getMessage());
        }
    }

    @Test
    void invalidDirectoryTest() {
        assertThrows(RuntimeException.class, () ->
                        SudokuBoardDaoFactory.getFileDao("invalid\0directory"),
                "Expected an exception for invalid directory"
        );
    }

    @Test
    void namesEmptyDirectoryTest() {
        try {
            Dao<SudokuBoard> dao = SudokuBoardDaoFactory.getFileDao(TEST_DIRECTORY);
            assertTrue(dao.names().isEmpty(), "Names list should be empty for a new directory");
        } catch (DaoException e) {
            fail("Unexpected exception during test: " + e.getMessage());
        }
    }
}
