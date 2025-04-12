/*
 * Copyright 2024 Mateusz Ciołkowski Szymon Kaźmierczak
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 International License.
 * You may not use the material for commercial purposes. You may copy, modify, distribute, and perform the work,
 * as long as you give appropriate credit, provide a link to the license, and indicate if changes were made.
 * Full license text: https://creativecommons.org/licenses/by-nc/4.0/legalcode
 */

package sudoku.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BacktrackingSudokuSolver implements SudokuSolver, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int BOARD_SIZE = 9;

    @Override
    public boolean solve(SudokuBoard board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board.get(i, j) == 0) {  
                    List<Integer> numbers = shuffledNumbers();
                    for (int number : numbers) {
                        if (isSafe(i, j, number, board)) {  
                            board.set(i, j, number);  
                            if (this.solve(board)) {  
                                return true;
                            }
                            board.getSudokuField(i,j).setUnsolvedValue(); 
                        }
                    }
                    return false;  
                }
            }
        }
        return true;  
    }

    public boolean isSafe(int row, int col, int num, SudokuBoard board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board.get(row, i) == num) {
                return false;  
            }
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board.get(i, col) == num) {
                return false;  
            }
        }

        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.get(startRow + i, startCol + j) == num) {
                    return false;  
                }
            }
        }

        return true;  
    }

    public List<Integer> shuffledNumbers() {
        List<Integer> numbers = new ArrayList<>();
        for (int num = 1; num <= BOARD_SIZE; num++) {
            numbers.add(num);
        }
        Collections.shuffle(numbers, new Random());
        return numbers;
    }
}
