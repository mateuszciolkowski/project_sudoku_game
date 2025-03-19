package sudoku.game.exception;

public class SudokuFileReadException extends DaoException {
    public static final String FILE_READ_ERROR = "error.readProblem";

    public SudokuFileReadException(String message) {
        super(message);
    }
}
