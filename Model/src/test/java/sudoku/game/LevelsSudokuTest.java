package sudoku.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LevelsSudokuTest {

    @Test
    public void LevelEasyTest(){
        SudokuBoard sudokuBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        sudokuBoard.solveGame();
        sudokuBoard.applyLevel(LevelsSudoku.EASY);
        int sum = 0;
        for (int row = 0; row < SudokuBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < SudokuBoard.BOARD_SIZE; col++) {
                if(sudokuBoard.get(row,col)==0){
                    sum++;
                }
            }
        }
        assertEquals(sum,35);
    }

    @Test
    public void LevelMediumTest(){
        SudokuBoard sudokuBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        sudokuBoard.solveGame();
        sudokuBoard.applyLevel(LevelsSudoku.MEDIUM);
        int sum = 0;
        for (int row = 0; row < SudokuBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < SudokuBoard.BOARD_SIZE; col++) {
                if(sudokuBoard.get(row,col)==0){
                    sum++;
                }
            }
        }
        assertEquals(sum,40);
    }
    @Test
    public void LevelHardTest(){
        SudokuBoard sudokuBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        sudokuBoard.solveGame();
        sudokuBoard.applyLevel(LevelsSudoku.HARD);
        int sum = 0;
        for (int row = 0; row < SudokuBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < SudokuBoard.BOARD_SIZE; col++) {
                if(sudokuBoard.get(row,col)==0){
                    sum++;
                }
            }
        }
        assertEquals(sum,55);
    }
}
