package sudoku.game;

import org.junit.jupiter.api.*;
import sudoku.game.exception.DaoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JdbcSudokuBoardDaoTest {

    private JdbcSudokuBoardDao dao;
    private SudokuBoard testBoard;

    @BeforeEach
    void setUp() throws SQLException {
        dao = new JdbcSudokuBoardDao();
        testBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        testBoard.solveGame();
        ensureDatabaseStructure();
    }

    @AfterEach
    void tearDown() throws SQLException {
        dao.close();
    }

    private void ensureDatabaseStructure() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sudoku", "postgres", "admin");
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS SudokuFields");
            stmt.executeUpdate("DROP TABLE IF EXISTS SudokuBoards");

            stmt.executeUpdate("CREATE TABLE SudokuBoards (\n" +
                    "id SERIAL PRIMARY KEY,\n" +
                    "name VARCHAR(255) UNIQUE NOT NULL\n" +
                    ")");
            stmt.executeUpdate("CREATE TABLE SudokuFields (\n" +
                    "board_id INT NOT NULL,\n" +
                    "row INT NOT NULL,\n" +
                    "col INT NOT NULL,\n" +
                    "value INT NOT NULL,\n" +
                    "FOREIGN KEY (board_id) REFERENCES SudokuBoards(id)\n" +
                    ")");
        }
    }

    @Test
    void testSaveAndLoadBoard() {
        String boardName = "TestBoard";
        try {
            dao.write(testBoard, boardName);

            SudokuBoard loadedBoard = dao.read(boardName);

            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    assertEquals(testBoard.get(row, col), loadedBoard.get(row, col),
                            "Values at position (" + row + "," + col + ") are not equal");
                }
            }
        } catch (DaoException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testLoadNonExistentBoard() {
        String nonExistentBoard = "NonExistentBoard";
        assertThrows(DaoException.class, () -> dao.read(nonExistentBoard),
                "Loading a non-existent board should throw DaoException");
    }

    @Test
    void testSaveDuplicateBoard() {
        String boardName = "DuplicateBoard";
        try {
            dao.write(testBoard, boardName);
            assertThrows(DaoException.class, () -> dao.write(testBoard, boardName),
                    "Saving a board with a duplicate name should throw DaoException");
        } catch (DaoException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testGetAllBoardNames() {
        try {
            dao.write(testBoard, "Board1");
            dao.write(testBoard, "Board2");

            List<String> names = dao.names();

            assertTrue(names.contains("Board1"), "Board1 should be in the list of names");
            assertTrue(names.contains("Board2"), "Board2 should be in the list of names");
        } catch (DaoException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testRollbackOnError() {
        String boardName = "RollbackBoard";
        try {
            dao.write(testBoard, boardName);

            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sudoku", "postgres", "admin");
                 Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DROP TABLE SudokuFields");
            }

            assertThrows(DaoException.class, () -> dao.write(testBoard, boardName),
                    "Writing a board after table removal should throw DaoException");
        } catch (SQLException e) {
            fail("Unexpected SQL exception: " + e.getMessage());
        }
    }

    @Test
    void testConnectionClose() {
        try {
            dao.close();
            assertThrows(DaoException.class, () -> dao.names(),
                    "Accessing after closing the connection should throw SQLException");
        } catch (SQLException e) {
            fail("Unexpected exception during close test: " + e.getMessage());
        }
    }
}
