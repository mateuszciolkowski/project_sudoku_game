package sudoku.game;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import sudoku.game.exception.DaoException;
import sudoku.game.exception.SudokuFileReadException;
import sudoku.game.exception.SudokuNullPointerException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileSudokuBoardDaoTest {

    @AfterEach
    public void cleanUp() throws IOException {
        File directory = new File(".");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    @Test
    void readTest() throws DaoException {
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(".")) {
            SudokuSolver solver = new BacktrackingSudokuSolver();
            SudokuBoard board = new SudokuBoard(solver);

            dao.write(board, "testBoard1");

            SudokuBoard readedBoard = dao.read("testBoard1");
            assertEquals(board, readedBoard, "The read board does not match the written board");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void namesTest() throws DaoException {
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(".")) {
            SudokuSolver solver = new BacktrackingSudokuSolver();
            SudokuBoard board1 = new SudokuBoard(solver);
            SudokuBoard board2 = new SudokuBoard(solver);

            dao.write(board1, "testBoard1");
            dao.write(board2, "testBoard2");

            List<String> directoryData = dao.names();

            assertTrue(directoryData.contains("testBoard1"), "Missing testBoard1 in names");
            assertTrue(directoryData.contains("testBoard2"), "Missing testBoard2 in names");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void invalidCreateDirectoryTest() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                SudokuBoardDaoFactory.getFileDao("invalid\0directory")
        );
        assertNotNull(exception.getMessage(), "Expected an exception when creating an invalid directory");
    }

    @Test
    void readNotFoundTest() {
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(".")) {
            assertThrows(SudokuFileReadException.class, () -> dao.read("fileDoesNotExist"));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void nullWrite() {
        try (FileSudokuBoardDao dao = new FileSudokuBoardDao(".")) {
            assertThrows(SudokuNullPointerException.class, () -> dao.write(null, "nullBoard"));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void namesEmptyDirectoryTest() {
        File testDirectory = new File("testDirectory");
        try {
            FileUtils.deleteDirectory(testDirectory);

            try (FileSudokuBoardDao dao = new FileSudokuBoardDao("testDirectory")) {
                List<String> names = dao.names();
                assertTrue(names.isEmpty(), "Directory should be empty, but names are present");
            }
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            testDirectory.delete();
        }
    }
}
